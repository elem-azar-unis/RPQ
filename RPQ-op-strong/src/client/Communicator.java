package client;

import java.io.IOException;

import kernel.Element;
import message.Alter;
import message.Content;
import message.Delete;
import message.Insert;
import message.Max;
import message.Message;
import message.Update;
import connector.ClientConnector;
import connector.Receiver;

class Communicator implements Runnable
{
	private PriorityQueue cpq=null;
	private Receiver in=null;
	Communicator(PriorityQueue cpq)
	{
		this.cpq=cpq;
		in=new Receiver(cpq.conn);
	}
	void reset(ClientConnector c)
	{
		in.reset(c);
	}
	public void run()
	{
		while(true)
		{
			try
			{
				while(true)
				{
					Message m=in.recv();
					switch (m.type)
					{
						case Message.UPDATE :
						{
							update((Update) m);
							break;
						}
						case Message.DELETE :
						{
							delete((Delete) m);
							break;
						}
						case Message.ACK :
						{
							ack();
							break;
						}
						case Message.ALTER :
						{
							alter((Alter) m);
							break;
						}
						case Message.INSERT :
						{
							insert((Insert) m);
							break;
						}
						case Message.MAX :
						{
							max((Max) m);
							break;
						}
						default :
							break;
					}
				}
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				cpq.reconnect();
			}
		}
	}
	private void update(Update m)
	{
		for (Content<?, ?> content : m.lst)
		{
			if(cpq.queue.contains((String) content.key))
			{
				cpq.queue.alter((String) content.key,(Integer) content.value);
			}
			else 
			{
				cpq.queue.insert(new Element<>((String) content.key, (Integer) content.value));
			}
		}
	}
	@SuppressWarnings("unchecked")
	private void delete(Delete m)
	{
		if(m.acquired)
		{
			synchronized (cpq.replyLock)
			{
				cpq.reply=(Content<String, Integer>) m.content;
				cpq.replyLock.notify();
			}
		}
		else
		{
			cpq.queue.remove((String) m.content.key);
		}
	}
	private void ack()
	{
		synchronized (cpq.replyLock)
		{
			cpq.replied=true;
			cpq.replyLock.notify();
		}
	}
	private void alter(Alter m)
	{
		cpq.alter((String)m.content.key, (Integer)m.content.value);
	}
	@SuppressWarnings("unchecked")
	private void insert(Insert m)
	{
		cpq.insert((Element<String, Integer>) m.element);
	}
	@SuppressWarnings("unchecked")
	private void max(Max m)
	{
		synchronized (cpq.replyLock)
		{
			cpq.reply=(Content<String, Integer>) m.content;
			cpq.replyLock.notify();
		}
	}
}
