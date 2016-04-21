package server;

import message.Update;

/**
 * The thread's priority is 7.
 * Update the change to each client.
 */
class Updater implements Runnable
{
	PriorityQueue spq;
	public Updater(PriorityQueue spq)
	{
		this.spq=spq;
	}
	public void run()
	{
		try
		{
			while(true)
			{
				synchronized (spq.versionCtrl)
				{
					while(spq.versionCtrl.tell()==0)
						spq.versionCtrl.wait();
					spq.broadcast(new Update(spq.queue.getUpdate(spq.versionCtrl.tell())));
					spq.versionCtrl.reset();
				}
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}	
}
