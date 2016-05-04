package server;

import kernel.Element;

/**
 * The thread's priority is 3.
 * Apply one change every time.
 */
class Applier implements Runnable
{
	private PriorityQueue spq;
	Applier(PriorityQueue spq)
	{
		this.spq=spq;
	}
	public void run()
	{
		try
		{
			while(true)
			{
				synchronized (spq.tasks)
				{					
					while(spq.tasks.isEmpty())
						spq.tasks.wait();
					@SuppressWarnings("unchecked")
					Element<String,Integer> e=(Element<String, Integer>) spq.tasks.get();
					int level=spq.queue.apply(e);
					if(spq.versionCtrl.update(spq.queue.getHeight(), level))
						spq.versionCtrl.notify();
				}	
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}		
}
