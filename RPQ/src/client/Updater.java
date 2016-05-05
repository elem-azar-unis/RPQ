package client;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import connector.ClientConnector;
import connector.Sender;
import message.Message;

class Updater implements Runnable
{
	private final Queue<Message> messages= new LinkedList<>();
	private Sender to=null;
	private boolean rested =false;
	Updater(PriorityQueue cpq)
	{
		to=new Sender(cpq.conn);
	}
	void add(Message m)
	{
		synchronized (messages)
		{
			messages.add(m);
			messages.notify();
		}
	}
	public void reset(ClientConnector c)
	{
		synchronized (to)
		{
			to.reset(c);
			rested =true;
			to.notify();
		}	
	}
	public void run()
	{
		while(true)
		{
			try
			{
				while(true)
				{
					synchronized (messages)
					{
						while (messages.isEmpty())
							messages.wait();
						to.send(messages.peek());
						messages.remove();
					}
				}
			}
			catch (IOException e)
			{
				synchronized (to)
				{
					try
					{
						while(!rested)
							to.wait();
					}
					catch (InterruptedException e1)
					{
						e1.printStackTrace();
					}
					rested =false;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
