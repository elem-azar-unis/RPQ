package client;

import kernel.ClientPriorityQueue;
import kernel.Element;
import message.Alter;
import message.Content;
import message.Delete;
import message.Delta;
import message.Insert;
import connector.ClientConnector;

public class PriorityQueue
{
	ClientPriorityQueue<String, Integer, Element<String,Integer>> queue=new ClientPriorityQueue<>();
	ClientConnector conn=null;
	Communicator communicator=null;
	Updater updater=null;
	Content<String, Integer> deleteReply=null;
	/**
	 * need the IP address and the port number of the server priority queue.
	 * @param ip
	 * @param port
	 */
	public PriorityQueue(String ip,int port)
	{
		conn=new ClientConnector(ip, port);
		conn.connect();
		communicator=new Communicator(this);
		new Thread(communicator).run();
		updater=new Updater(this);
		new Thread(updater).run();
	}
	void reconnect()
	{
		conn.connect();
		communicator.reset(conn);
		updater.reset(conn);
	}
	void setReply(Content<String, Integer> c)
	{
		synchronized (deleteReply)
		{
			deleteReply=c;
			deleteReply.notify();
		}
	}
	public void alter(String key,Integer value)
	{
		updater.add(new Alter(new Content<>(key, value)));
		queue.alter(key, value);
	}
	public Element<String, Integer> delete()
	{
		synchronized (deleteReply)
		{
			updater.add(new Delete(null));
			try
			{
				if (deleteReply==null)				
					deleteReply.wait(30000);
				//timeout
				if (deleteReply==null)
					return null;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			Element<String, Integer> rtn=queue.remove(deleteReply.key);
			rtn.priority=deleteReply.value;
			deleteReply=null;
			return rtn;
		}
	}
	public void delta(String key,Integer value)
	{
		synchronized (queue)
		{
			updater.add(new Delta(new Content<>(key, value)));
			int k=queue.get(key).priority+value;
			queue.alter(key, k);
		}
	}
	public void insert(Element<String, Integer> e)
	{
		updater.add(new Insert(e));
		queue.insert(e);
	}
	public Element<String, Integer> max()
	{
		return queue.getMax();
	}
}
