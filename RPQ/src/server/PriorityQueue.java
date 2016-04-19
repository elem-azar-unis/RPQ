package server;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import kernel.Element;
import kernel.ServerPriorityQueue;
import kernel.TaskQueue;
import message.Content;
import message.Delete;
import message.Message;
import message.Update;
import connector.Sender;

public class PriorityQueue
{
	ServerPriorityQueue<String,Integer, Element<String,Integer>> queue=new ServerPriorityQueue<>();
	TaskQueue tasks=new TaskQueue();
	VersionCtrl versionCtrl=new PlainVersionCtrl();
	List<Node> clients=new LinkedList<Node>();
	/**
	 * 3 threads(priority): wait connection(5), updater(7), applier(3)
	 * @param port the server port
	 */
	public PriorityQueue(int port)
	{
		new Thread(new WaitConnection(port, this)).start();
		Thread apl=new Thread(new Applier());
		apl.setPriority(3);
		apl.start();
		Thread upd=new Thread(new Updater());
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
	/**
	 * The thread's priority is 3.
	 * Apply one change every time.
	 */
	class Applier implements Runnable
	{

		public void run()
		{
			try
			{
				while(true)
				{
					synchronized (tasks)
					{					
						while(tasks.isEmpty())
							tasks.wait();
						@SuppressWarnings("unchecked")
						Element<String,Integer> e=(Element<String, Integer>) tasks.get();
						int level=queue.apply(e);
						if(versionCtrl.update(queue.getHeight(), level))
							versionCtrl.notify();
					}	
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}		
	}
	/**
	 * The thread's priority is 7.
	 * Update the change to each client.
	 */
	class Updater implements Runnable
	{
		public void run()
		{
			try
			{
				while(true)
				{
					synchronized (versionCtrl)
					{
						while(versionCtrl.tell()==0)
							versionCtrl.wait();
						broadcast(new Update(queue.getUpdate(versionCtrl.tell())));
						versionCtrl.reset();
					}
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}	
	}
	//TODO 顶层操作堆的函数，维护版本号，要notify apply者。
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

class Node
{
	Sender to;
	public Node(Sender t)
	{
		to=t;
	}
}