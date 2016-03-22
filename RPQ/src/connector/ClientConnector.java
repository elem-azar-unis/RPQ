package connector;

import java.io.IOException;
import java.net.Socket;

public class ClientConnector
{
	String ip;
	int port;
	Socket socket=null;
	/**
	 * continue to run
	 */
	private boolean run=true;
	public ClientConnector(String ip,int port)
	{
		this.ip=ip;
		this.port=port;
	}
	public void set_stop()
	{
		run=false;
	}
	public void close()
	{
		try
		{
			run=false;
			if(socket!=null)
				socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Connect or reconnect the server
	 * @return the socket connected to the server
	 */
	public Socket connect()
	{
		run=true;
		socket=null;
		while(run)
		{
			try
			{
				socket=new Socket(ip,port);
			}
			catch (IOException e)
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		return socket;
	}
}
