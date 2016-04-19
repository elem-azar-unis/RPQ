package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import message.Update;
import kernel.Element;
import kernel.ServerPriorityQueue;
import kernel.TaskQueue;

public class PriorityQueue
{
	ServerPriorityQueue<String,Integer, Element<String,Integer>> queue=new ServerPriorityQueue<>();
	TaskQueue tasks=new TaskQueue();
	VersionCtrl versionCtrl=new PlainVersionCtrl();
	List<Node> clients=new LinkedList<Node>();
	/**
	 * 3 threads: wait connection, updater, applier
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
						queue.apply(e);			
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
						Update upd=new Update(queue.getUpdate(versionCtrl.tell()));
						synchronized (clients)
						{
							for(Node n:clients)
							{
								n.to.writeObject(upd);
							}
						}
					}
				}
			}
			catch (InterruptedException | IOException e)
			{
				e.printStackTrace();
			}
		}	
	}
	//TODO 顶层操作堆的函数，维护版本号，要notify apply者，更新者。
}

class Node
{
	ObjectOutputStream to;
	public Node(ObjectOutputStream t)
	{
		to=t;
	}
}