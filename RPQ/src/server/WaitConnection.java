package server;

import java.net.Socket;
import connector.ServerConnector;

public class WaitConnection implements Runnable
{
	ServerConnector server=null;
	public WaitConnection(int port)
	{
		server=new ServerConnector(port);
	}
	public void run()
	{
		while(true)
		{
			Socket recv=server.accept();
			//TODO new Thread(new Communicator(***).start();负责接受客户端的操作信息。
		}
	}
}
