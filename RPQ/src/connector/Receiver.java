package connector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import message.Message;

public class Receiver
{
	private ObjectInputStream in=null;
	public Receiver(Socket s) throws IOException
	{
		in=new ObjectInputStream(s.getInputStream());
	}
	/**
	 * used when reconnected
	 * @param s the socket
	 * @throws IOException
	 */
	public void reset(Socket s) throws IOException
	{
		if(in!=null)
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		in=new ObjectInputStream(s.getInputStream());
	}
	public void close()
	{
		if(in!=null)
			try
			{
				in.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}
	public Message recv() throws ClassNotFoundException, IOException
	{
		return (Message)in.readObject();
	}
}
