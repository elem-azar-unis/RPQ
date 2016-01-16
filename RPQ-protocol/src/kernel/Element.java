package kernel;

/**
 * <p>You need to extend this abstract class. 
 * This is the element in the priority queue.
 * 
 * <p>Since the data structure is distributed, 
 * each element should have an unique identifier for each node to locate it.
 * 
 * <p>Is used in the priority queue. It should be conparable by inplementing
 * {@link Comparable}
 * 
 * @param <K> Type of the identifier.
 */
public abstract class Element<K> implements Comparable<Element<K>>
{
	/**
	 * The identifier.
	 */
	public K key=null;
	public Element(){}
	public Element(K key){this.key=key;}
}