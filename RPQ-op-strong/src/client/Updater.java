package client;

import java.io.IOException;

import message.Message;
import connector.ClientConnector;
import connector.Sender;

class Updater
{
	Sender to=null;
	boolean reseted=false;
	public Updater(PriorityQueue cpq)
	{
		to=new Sender(cpq.conn);
	}
	public void reset(ClientConnector c)
	{
		synchronized (to)
		{
			to.reset(c);
			reseted=true;
			to.notify();
		}	
	}
	public void send(Message m)
	{
		while(true)
		{
			try
			{
				to.send(m);
				break;
			}
			catch (IOException e)
			{
				synchronized (to)
				{
					try
					{
						while(!reseted)	
							to.wait();
					}
					catch (InterruptedException e1)
					{
						e1.printStackTrace();
					}
					reseted=false;
				}
			}
		}
	}
}
