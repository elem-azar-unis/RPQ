package client;

import connector.ClientConnector;
import connector.Receiver;

class Communicator implements Runnable
{
	PriorityQueue cpq=null;
	Receiver in=null;
	public Communicator(PriorityQueue cpq)
	{
		this.cpq=cpq;
		in=new Receiver(cpq.conn);
	}
	public void reset(ClientConnector c)
	{
		in.reset(c);
	}
	@Override
	public void run()
	{
		// TODO 自动生成的方法存根
		
	}

}
