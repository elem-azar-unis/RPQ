package server;

import java.io.IOException;
import java.net.Socket;

import kernel.Element;
import message.Alter;
import message.Delta;
import message.Insert;
import message.Max;
import message.Message;
import message.Update;
import connector.Receiver;
import connector.Sender;

class Communicator implements Runnable
{
	private PriorityQueue spq=null;
	private Receiver in=null;
	private Sender out=null;
	Communicator(Socket socket, PriorityQueue pq)
	{
		try
		{
			spq=pq;
			in=new Receiver(socket);
			out=new Sender(socket);
			out.send(new Update(spq.queue.getUpdate(spq.versionCtrl.tell())));		
			spq.addClient(new Node(out));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public void run()
	{
		try
		{
			while(true)
			{
				Message m=in.recv();
				switch (m.type)
				{
					case Message.ALTER :
					{
						alter((Alter) m);
						break;
					}						
					case Message.DELETE:
					{	
						delete();
						break;
					}
					case Message.DELTA:
					{
						delta((Delta) m);
						break;
					}
					case Message.INSERT:
					{
						insert((Insert) m);
						break;
					}
					case Message.MAX:
					{
						max();
						break;
					}
					default :
						break;
				}
				spq.increaseCount();
			}
		}
		catch (ClassNotFoundException | IOException e)
		{
			e.printStackTrace();
		}
	}
	private void alter(Alter m)
	{
		spq.alter((String)m.content.key, (Integer)m.content.value);
	}
	private void delete()
	{
		spq.delete(out);
	}
	private void delta(Delta m)
	{
		spq.delta((String)m.content.key, (Integer)m.content.value);
	}
	@SuppressWarnings("unchecked")
	private void insert(Insert m)
	{
		spq.insert((Element<String, Integer>) m.element);
	}
	private void max() throws IOException
	{
		Max max=new Max(spq.max());
		out.send(max);
	}
}
