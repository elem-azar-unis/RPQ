package server;

import java.io.IOException;
import java.net.Socket;

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
	@Override
	public void run()
	{
		// TODO 自动生成的方法存根
		
	}
}
