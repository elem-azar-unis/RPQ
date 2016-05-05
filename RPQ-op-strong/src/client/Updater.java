package client;

import java.io.IOException;

import message.Message;
import connector.ClientConnector;
import connector.Sender;

class Updater
{
	private Sender to=null;
	private boolean rested =false;
	Updater(PriorityQueue cpq)
	{
		to=new Sender(cpq.conn);
	}
	void reset(ClientConnector c)
	{
		synchronized (to)
		{
			to.reset(c);
			rested =true;
			to.notify();
		}	
	}
	void send(Message m)
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
		}
	}
}
