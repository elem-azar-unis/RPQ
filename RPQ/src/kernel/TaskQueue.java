package kernel;

import java.util.Collection;
import java.util.HashSet;


/**
 * Synchronized. Used by server. It is a set of the element changed but not applied.
 */
public class TaskQueue
{
	final Collection<Element<?,?>> tasks=(new HashSet<>());
	public boolean isEmpty()
	{
		synchronized (this)
		{
			return tasks.isEmpty();
		}
	}
	/**
	 * @param e the element
	 * @return if the element is in the task queue.
	 */
	public boolean has(Element<?,?> e)
	{
		return tasks.contains(e);
	}
	/**
	 * Add an unapplied changed element to the queue.
	 * Won't add an element again if it has been in the queue.
	 * @param e the element to be added
	 */
	public void add(Element<?,?> e)
	{
		synchronized (this)
		{
			tasks.add(e);
			notify();
		}	
	}
	/**
	 * Get the next element to be applied. remove this element from the queue.
	 * @return the next element to be applied
	 */
	public Element<?,?> get()
	{
		synchronized (this)
		{
			if(tasks.isEmpty())
				return null;
			Element<?,?> rtn=tasks.iterator().next();
			tasks.remove(rtn);
			return rtn;
		}
	}
}

