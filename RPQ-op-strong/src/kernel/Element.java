package kernel;

import java.io.Serializable;

/**
 * <p>This is the element in the priority queue. 
 * You can extend this class if you want. Remember to implement the interface {@link Serializable}.
 * 
 * <p>Since the data structure is distributed, 
 * each element should have an unique identifier for each node to locate it.
 * 
 * <p>Each element has its priority, the type of which should have implemented {@link Comparable}.
 * 
 * @param <K> Type of the identifier.
 * @param <V> The type of the priority of this element. It should've implemented {@link Comparable}.
 */
public class Element<K,V extends Comparable<V>> implements Comparable<Element<?,?>>, Serializable
{
	private static final long serialVersionUID = -7385240577239007826L;
	public K key=null;
	public V priority=null;
	/**The index of this element in the priority queue.*/
	transient int index=-1;
	public Element(){}
	public Element(K key,V priority){this.key=key;this.priority=priority;}
	/**
	 * @return The level of the Object in the local priority queue.
	 */
	public int getLevel()
	{
		return (int)(Math.log((double)(index+1))/Math.log(2.0));
	}
	public int getIndex()
	{
		return index;
	}
	/**
	 * Compares this element with the specified element for order. 
	 * Returns a negative integer, zero, or a positive integer as 
	 * this element is less than, equal to, or greater than the specified element. <p>
	 * 
	 * Note that if an element's priority is {@code null}, it means that its priority is negative infinity.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public int compareTo(Element<?, ?> e)
	{
		if (this.priority==null && e.priority==null)
			return 0;
		else if(this.priority==null)
			return -1;
		else if(e.priority==null)
			return 1;
		else
			return this.priority.compareTo((V) e.priority);
	}
}