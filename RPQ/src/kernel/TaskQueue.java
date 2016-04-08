package kernel;

import java.util.Collection;
import java.util.HashSet;


/**
 * Synchronized. Used by server. It is a set of the elememt changed but not applied.
 */
public class TaskQueue
{
	Collection<Element<?,?>> tasks=(new HashSet<Element<?,?>>());
	public boolean isEmpty()
	{
		synchronized (tasks)
		{
			return tasks.isEmpty();
		}
	}
	/**
	 * Add an unapplied changed element to the queue.
	 * Won't add an element again if it has been in the queue.
	 * @param e the element to be added
	 */
	public void add(Element<?,?> e)
	{
		synchronized (tasks)
		{
			tasks.add(e);
		}	
	}
	/**
	 * Get the next element to be applied. remove this element from the queue.
	 * @return the next element to be applied
	 */
	public Element<?,?> get()
	{
		synchronized (tasks)
		{
			if(tasks.isEmpty())
				return null;
			Element<?,?> rtn=tasks.iterator().next();
			tasks.remove(rtn);
			return rtn;
		}
	}
	/**
	 * Get the max element in the queue.
	 * @return the element with the bigest (future) priority.
	 */
	public Element<?,?> max()
	{
		synchronized (tasks)
		{
			if(tasks.isEmpty())
				return null;
			Element<?,?> rtn=null;
			for(Element<?,?> e:tasks)
			{
				if(rtn==null || rtn.compareTo(e)<0)
				{
					rtn=e;
				}
			}
			return rtn;
		}
	}
}
