package connector;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import message.Message;

public class Sender
{
	private ObjectOutputStream out=null;
	public Sender(Socket s) throws IOException
	{
		out=new ObjectOutputStream(s.getOutputStream());
	}
	public Sender(ClientConnector c)
	{
		out=c.out;
	}
	/**
	 * used when reconnected
	 * @param s the socket
	 * @throws IOException
	 */
	public void reset(Socket s) throws IOException
	{
		if(out!=null)
			try
			{
				out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		out=new ObjectOutputStream(s.getOutputStream());
	}
	/**
	 * used when reconnected
	 * @param c ClientConnector
	 */
	public void reset(ClientConnector c) 
	{
		if(out!=null)
			try
			{
				out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		out=c.out;
	}
	public void close()
	{
		if(out!=null)
			try
			{
				out.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}
	public void send(Message m) throws IOException
	{
		//System.out.println("send "+m.toString());
		out.writeObject(m);
	}
}
