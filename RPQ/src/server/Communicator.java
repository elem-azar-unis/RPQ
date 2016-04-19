package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Communicator implements Runnable
{
	PriorityQueue spq=null;
	ObjectInputStream in=null;
	ObjectOutputStream out=null;
	public Communicator(Socket socket,PriorityQueue pq)
	{
		try
		{
			spq=pq;
			in=new ObjectInputStream(socket.getInputStream());
			out=new ObjectOutputStream(socket.getOutputStream());
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
