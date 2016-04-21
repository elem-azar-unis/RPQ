package server;

import java.net.Socket;
import connector.ServerConnector;

class WaitConnection implements Runnable
{
	ServerConnector server=null;
	PriorityQueue spq=null;
	public WaitConnection(int port,PriorityQueue pq)
	{
		server=new ServerConnector(port);
		spq=pq;
	}
	public void run()
	{
		while(true)
		{
			Socket recv=server.accept();
			new Thread(new Communicator(recv,spq)).start();
		}
	}
}
