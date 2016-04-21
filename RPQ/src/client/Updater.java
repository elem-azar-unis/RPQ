package client;

import java.util.LinkedList;
import java.util.Queue;

import connector.ClientConnector;
import connector.Sender;
import message.Message;

class Updater implements Runnable
{
	Queue<Message> messages=new LinkedList<Message>();
	PriorityQueue cpq=null;
	Sender to=null;
	public Updater(PriorityQueue cpq)
	{
		this.cpq=cpq;
		to=new Sender(cpq.conn);
	}
	public void add(Message m)
	{
		synchronized (messages)
		{
			messages.add(m);
			messages.notify();
		}
	}
	public void reset(ClientConnector c)
	{
		synchronized (to)
		{
			to.reset(c);
			to.notify();
		}	
	}
	@Override
	public void run()
	{
		// TODO 自动生成的方法存根
		
	}
}
