package server;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import kernel.Element;
import kernel.KernelPriorityQueue;
import message.Ack;
import message.Alter;
import message.Content;
import message.Delete;
import message.Insert;
import message.Message;
import connector.Sender;

public class PriorityQueue
{
	final KernelPriorityQueue<String,Integer, Element<String,Integer>> queue=new KernelPriorityQueue<>();
	private final List<Node> clients = new LinkedList<>();
	private int count=0;
	private final Boolean countLock=true;
	void increaseCount()
	{
		synchronized (countLock)
		{
			count++;
		}
	}
	public int getCount()
	{
		return count;
	}
	/**
	 * 3 threads(priority): wait connection(5), updater(7), applier(3)
	 * @param port the server port
	 */
	public PriorityQueue(int port)
	{
		new Thread(new WaitConnection(port, this)).start();
	}
	void addClient(Node n)
	{
		synchronized (clients)
		{
			clients.add(n);
		}
	}
	private void broadcast(Sender out, Message m)
	{
		Iterator<Node> it=clients.iterator();
		while(it.hasNext())
		{
			try
			{
				Sender to=it.next().to;
				if(to==out)
					to.send(new Ack());
				else 
					to.send(m);
			}
			catch (IOException e)
			{
				it.remove();
				e.printStackTrace();
			}
		}
	}
	public void alter(String key,Integer value,Sender to)
	{
		synchronized (clients)
		{
			queue.alter(key, value);
			broadcast(to,new Alter(new Content<>(key, value)));
		}
	}
	public Element<String,Integer> delete()
	{		
		synchronized (clients)
		{
			Element<String,Integer> rtn=queue.deleteMax();
			Delete m=new Delete(new Content<>(rtn));
			Iterator<Node> it=clients.iterator();
			while(it.hasNext())
			{
				try
				{
					Sender to=it.next().to;
					to.send(m);
				}
				catch (IOException e)
				{
					it.remove();
					e.printStackTrace();
				}
			}
			return rtn;
		}		
	}
	public void delete(Sender out)
	{	
		synchronized (clients)
		{
			Element<String,Integer> rtn=queue.deleteMax();
			Delete m=new Delete(new Content<>(rtn));
			Iterator<Node> it=clients.iterator();
			while(it.hasNext())
			{
				try
				{
					Sender to=it.next().to;
					m.acquired=(to==out);
					to.send(m);
				}
				catch (IOException e)
				{
					it.remove();
					e.printStackTrace();
				}
			}
		}
	}
	public void delta(String key,Integer d,Sender to)
	{
		synchronized (clients)
		{
			Element<String,Integer> temp=queue.get(key);
			if(temp==null)return;
			int k;
			synchronized (queue)
			{
				k=queue.get(key).priority+d;
				queue.alter(key, k);
			}
			broadcast(to,new Alter(new Content<>(key, k)));
		}
	}
	public void insert(Element<String,Integer> e,Sender to)
	{
		synchronized (clients)
		{
			queue.insert(e);
			broadcast(to, new Insert(e));
		}
	}
	public Content<String, Integer> max()
	{
		return new Content<>(queue.getMax());
	}
	public int getSize()
	{
		return queue.getSize();
	}
}
