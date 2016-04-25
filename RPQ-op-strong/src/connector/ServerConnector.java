package connector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnector
{
	ServerSocket server=null;
	/**
	 * @param port The port of the server
	 */
	public ServerConnector(int port)
	{
		try
		{
			server=new ServerSocket(port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public Socket accept()
	{
		Socket temp=null;
		try
		{
			temp=server.accept();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return temp;
	}
	public void close()
	{
		try
		{
			if(server!=null)
				server.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
