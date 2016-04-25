package client;

import kernel.KernelPriorityQueue;
import kernel.Element;
import message.Alter;
import message.Content;
import message.Delete;
import message.Delta;
import message.Insert;
import message.Max;
import connector.ClientConnector;

public class PriorityQueue
{
	KernelPriorityQueue<String, Integer, Element<String,Integer>> queue=new KernelPriorityQueue<>();
	ClientConnector conn=null;
	Communicator communicator=null;
	Updater updater=null;
	Content<String, Integer> reply=null;
	Boolean replied=false;
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
	}
	void reconnect()
	{
		conn.connect();
		communicator.reset(conn);
		updater.reset(conn);
	}
	void setReply(Content<String, Integer> c)
	{
		synchronized (reply)
		{
			reply=c;
			reply.notify();
		}
	}
	void serReplied()
	{
		synchronized (replied)
		{
			replied=true;
			replied.notify();
		}
	}
	public void alter(String key,Integer value)
	{
		updater.send(new Alter(new Content<>(key, value)));
		queue.alter(key, value);
		wait_for_replied();
	}
	private void wait_for_replied()
	{
		synchronized (replied)
		{
			try
			{
				while (!replied)				
					replied.wait();
				replied=false;
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	public Element<String, Integer> delete()
	{
		synchronized (reply)
		{
			updater.send(new Delete(null));
			try
			{
				while (reply==null)				
					reply.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			Element<String, Integer> rtn=queue.remove(reply.key);
			rtn.priority=reply.value;
			reply=null;
			return rtn;
		}
	}
	public void delta(String key,Integer value)
	{
		synchronized (queue)
		{
			updater.send(new Delta(new Content<>(key, value)));
			int k=queue.get(key).priority+value;
			queue.alter(key, k);
		}
		wait_for_replied();
	}
	public void insert(Element<String, Integer> e)
	{
		updater.send(new Insert(e));
		queue.insert(e);
		wait_for_replied();
	}
	public Element<String, Integer> max()
	{
		synchronized (reply)
		{
			updater.send(new Max(null));
			try
			{
				while (reply==null)				
					reply.wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			Element<String, Integer> rtn=queue.get(reply.key);
			rtn.priority=reply.value;
			reply=null;
			return rtn;
		}
	}
}
