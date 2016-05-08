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
	final KernelPriorityQueue<String, Integer, Element<String,Integer>> queue=new KernelPriorityQueue<>();
	ClientConnector conn=null;
	private Communicator communicator=null;
	private Updater updater=null;
	Content<String, Integer> reply=null;
	Boolean replied=false;
	final Boolean replyLock=true;
	/**
	 * need the IP address and the port number of the server priority queue.
	 * @param ip ip
	 * @param port port
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
	public void alter(String key,Integer value)
	{
		updater.send(new Alter(new Content<>(key, value)));
		queue.alter(key, value);
		wait_for_replied();
	}
	private void wait_for_replied()
	{
		synchronized (replyLock)
		{
			try
			{
				while (!replied)
					replyLock.wait();
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
		synchronized (replyLock)
		{
			updater.send(new Delete(null));
			try
			{
				while (reply==null)
					replyLock.wait();
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
		synchronized (replyLock)
		{
			updater.send(new Max(null));
			try
			{
				while (reply==null)
					replyLock.wait();
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
	public String getOne(int in){return queue.getOne(in);}
	public int getSize()
	{
		return queue.getSize();
	}
}
