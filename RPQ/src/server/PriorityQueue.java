package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import kernel.Element;
import kernel.ServerPriorityQueue;
import kernel.TaskQueue;
import message.Content;
import message.Delete;
import message.Message;
import connector.Sender;

public class PriorityQueue
{
	ServerPriorityQueue<String,Integer, Element<String,Integer>> queue=new ServerPriorityQueue<>();
	final TaskQueue tasks=new TaskQueue();
	final VersionCtrl versionCtrl=new LinearVersionCtrl(4);
	private final List<Node> clients= new LinkedList<>();
	private int count=0;
	private final Boolean count_lock=true;
	void increaseCount()
	{
		synchronized (count_lock)
		{
			count++;
		}
	}
	public int getCount()
	{
		return count;
	}
	public ArrayList<Content<?, ?>> getK()
	{
		int i=queue.getSize();
		double h=Math.log((double)i)/Math.log(2.0);
		i=Math.min((int) Math.sqrt(h),5);
		return queue.getUpdate(i);
	}
	/**
	 * 3 threads(priority): wait connection(5), updater(7), applier(3)
	 * @param port the server port
	 */
	public PriorityQueue(int port)
	{
		new Thread(new WaitConnection(port, this)).start();
		Applier applier = new Applier(this);
		Thread apl=new Thread(applier);
		apl.setPriority(3);
		apl.start();
		Updater updater = new Updater(this);
		Thread upd=new Thread(updater);
		upd.setPriority(7);
		upd.start();
	}
	void addClient(Node n)
	{
		synchronized (clients)
		{
			clients.add(n);
		}
	}
	void broadcast(Message m)
	{
		synchronized (clients)
		{
			Iterator<Node> it=clients.iterator();
			while(it.hasNext())
			{
				try
				{
					it.next().to.send(m);
				}
				catch (IOException e)
				{
					it.remove();
					e.printStackTrace();
				}
			}
		}
	}
	public void alter(String key,Integer value)
	{
		Element<String,Integer> temp=queue.alter(key, value);
		if(temp==null)return;
		tasks.add(temp);
	}
	public Element<String,Integer> delete()
	{
		synchronized (versionCtrl)
		{
			synchronized (tasks)
			{
				while(!tasks.isEmpty())
				{	
					@SuppressWarnings("unchecked")
					Element<String,Integer> e=(Element<String, Integer>) tasks.get();
					int level=queue.apply(e);
					versionCtrl.update(queue.getHeight(), level);
				}
				Element<String,Integer> rtn=queue.deleteMax();
				if(versionCtrl.update(queue.getHeight(),0))
					versionCtrl.notify();
				Delete m=new Delete(new Content<>(rtn));
				broadcast(m);
				return rtn;
			}
		}
	}
	public void delete(Sender out)
	{
		synchronized (versionCtrl)
		{
			synchronized (tasks)
			{
				while(!tasks.isEmpty())
				{	
					@SuppressWarnings("unchecked")
					Element<String,Integer> e=(Element<String, Integer>) tasks.get();
					int level=queue.apply(e);
					versionCtrl.update(queue.getHeight(), level);
				}
				Element<String,Integer> rtn=queue.deleteMax();
				if(versionCtrl.update(queue.getHeight(),0))
					versionCtrl.notify();
				Delete m=new Delete(new Content<>(rtn));
				synchronized (clients)
				{
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
		}
	}
	public void delta(String key,Integer d)
	{
		Element<String,Integer> temp=queue.get(key);
		if(temp==null)return;
		synchronized (temp)
		{
			Integer i=queue.getDesired(temp.getIndex());
			if(i==null)i=temp.priority+d;
			else i=i+d;
			queue.alter(key,i);
		}
		tasks.add(temp);
	}
	public void insert(Element<String,Integer> e)
	{
		queue.insert(e);
		tasks.add(e);
	}
	/**
	 * note: the answer may be a future value
	 */
	public Content<String, Integer> max()
	{
		synchronized (tasks)
		{
			return queue.getMax(tasks);
		}
	}
}
