package server;

import java.io.IOException;
import java.net.Socket;

import kernel.Element;
import message.Alter;
import message.Delete;
import message.Delta;
import message.Insert;
import message.Max;
import message.Message;
import connector.Receiver;
import connector.Sender;

public class Communicator implements Runnable
{
	PriorityQueue spq=null;
	Receiver in=null;
	Sender out=null;
	public Communicator(Socket socket,PriorityQueue pq)
	{
		try
		{
			spq=pq;
			in=new Receiver(socket);
			out=new Sender(socket);
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
						delete((Delete) m);
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
						max((Max) m);
						break;
					}
					default :
						break;
				}
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
	private void delete(Delete m)
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
		spq.insert((Element<String, Integer>) m.elememt);
	}
	private void max(Max m) throws IOException
	{
		Max max=new Max(spq.max());
		out.send(max);
	}
}
