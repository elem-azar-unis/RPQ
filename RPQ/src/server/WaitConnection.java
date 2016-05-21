package server;

import java.net.Socket;
import connector.ServerConnector;

class WaitConnection implements Runnable
{
	private ServerConnector server=null;
	private PriorityQueue spq=null;
	WaitConnection(int port, PriorityQueue pq)
	{
		server=new ServerConnector(port);
		spq=pq;
	}
	public void run()
	{
		while(true)
		{
			Socket recv=server.accept();
			System.out.println("new client");
			new Thread(new Communicator(recv,spq)).start();
		}
	}
}
