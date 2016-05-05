package client;

import java.io.IOException;

import kernel.Element;
import message.Content;
import message.Delete;
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
			synchronized (cpq.deleteReplyLock)
			{
				cpq.deleteReply=(Content<String, Integer>) m.content;
				cpq.deleteReplyLock.notify();
			}
		}
		else
		{
			cpq.queue.remove((String) m.content.key);
		}
	}
}
