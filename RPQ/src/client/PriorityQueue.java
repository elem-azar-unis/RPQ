package client;

import kernel.ClientPriorityQueue;
import kernel.Element;
import message.Alter;
import message.Content;
import message.Delete;
import message.Delta;
import message.Insert;
import connector.ClientConnector;

import java.util.ArrayList;

public class PriorityQueue
{
	final ClientPriorityQueue<String, Integer, Element<String,Integer>> queue=new ClientPriorityQueue<>();
	ClientConnector conn=null;
	private Communicator communicator=null;
	private Updater updater=null;
	Content<String, Integer> deleteReply=null;
	final Boolean deleteReplyLock=true;
	/**
	 * need the IP address and the port number of the server priority queue.
	 * @param ip IP
	 * @param port port
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
	public ArrayList<Content<?, ?>> getK()
	{
		int i=queue.getSize();
		double h=Math.log((double)i)/Math.log(2.0);
		i=Math.min((int) Math.sqrt(h),5);
		return queue.getUpdate(i);
	}
	void reconnect()
	{
		conn.connect();
		communicator.reset(conn);
		updater.reset(conn);
	}
	void setReply(Content<String, Integer> c)
	{
		synchronized (deleteReplyLock)
		{
			deleteReply=c;
			deleteReplyLock.notify();
		}
	}
	public void alter(String key,Integer value)
	{
		updater.add(new Alter(new Content<>(key, value)));
		queue.alter(key, value);
	}
	public Element<String, Integer> delete()
	{
		synchronized (deleteReplyLock)
		{
			updater.add(new Delete(null));
			try
			{
				if (deleteReply==null)
					deleteReplyLock.wait(30000);
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
	public String getOne(int in){return queue.getOne(in);}
	public int getSize()
	{
		return queue.getSize();
	}
}
