package kernel;

import java.util.Arrays;

/**
 * The kernel priority queue for server. It uses ElementTable to find an element in constant time.
 * <p>This is an unbounded priority queue based on heap. This is a maximam priority queue.
 * The priority is provided by user difined Comparable.
 * <p>The basic operations: Insert, Alter, Delete max, Get max, Append
 * <p>Insert and alter don't take immediate effect. They have two steps. 
 * First is to update the desierd value. 
 * Then, append this deiserd value and fix the heap.
 * <p>Also can get or remove one element given the identifier.
 * @param <K> The type of identifier.
 * @param <V> The type of value of priority.
 * @param <T> The type of element in the priority queue.
 */
public class ServerPriorityQueue<K,V extends Comparable<V>,T extends Element<K,V>>
{
	class Node
	{
		T element;
		/**
		 * Used only by the server RPQ. The change of the priority that is to be applied in the future.
		 */
		V desired=null;
		public Node(T element){this.element=element;}
	}
	private static final int INITIAL_CAPACITY=8;
	/**
     * Priority queue represented as a balanced binary heap: the two
     * children of queue[n] are queue[2*n+1] and queue[2*n+2].  The
     * priority queue is ordered by elements' decreasing ordering. 
     * For each node n in the heap and each descendant d of n, n >= d.  
     * The element with the highest value is in queue[0], assuming the queue is nonempty.
     */	
	@SuppressWarnings("unchecked")
	private Node[] elements=(ServerPriorityQueue<K, V, T>.Node[]) new Object[INITIAL_CAPACITY];
	/**
     * The size of the Priority Queue (the number of elements it contains).
     *
     * @serial
     */
    private int size=0;
    /**
     * The hash table of the element.
     */
    private ElementTable<K,T> table=new ElementTable<K,T>();
    /**
     * Increases the capacity by doubling it.
     */
    private void grow()
    {
    	elements=Arrays.copyOf(elements,2*elements.length);
    }
    /*
    /**
     * swap the two element in the priority queue.
     * @param a the first element's position
     * @param b the second element's position
     *
    private void swap(int a,int b)
    {
    	Node temp=elements[a];
    	elements[a]=elements[b];
    	elements[b]=temp;
    	elements[a].element.index=a;
    	elements[b].element.index=b;
    }
    */
    /**
     * Fix the heap from position k (let's call it x). Maintaining heap invariant by
     * promoting x up the tree until it is less than or equal to its parent, or is the root.
     * @param k The position to be fixed.
     */
    private void shiftUP(int k)
    {
    	Node e=elements[k];
    	while(k>0)
    	{
    		int parent=(k-1)>>>1;
    		if(e.element.compareTo(elements[parent].element)<=0)
    			break;
    		elements[k]=elements[parent];
    		elements[k].element.index=k;
    		k=parent;
    	}
    	elements[k]=e;
    	e.element.index=k;
    }
    /**
     * Fix the heap from position k (let's call it x). Maintaining heap invariant by
     * demoting x down the tree until it is greater than or equal to its children, or is a leaf.
     * @param k The position to be fixed.
     */
    private void shiftDown(int k)
    {
    	Node e=elements[k];
    	int half=size>>>1;            // loop while a non-leaf
    	while(k<half)
    	{
    		int child=(k<<1)+1;       // assume left child is largest
    		int right=child+1;
    		if(right<size && elements[child].element.compareTo(elements[right].element)<0)
    			child=right;
    		if(e.element.compareTo(elements[child].element)>=0)
    			break;
    		elements[k]=elements[child];
    		elements[k].element.index=k;
    		k=child;
    	}
    	elements[k]=e;
    	e.element.index=k;
    }
    /**
     * Insert a new element in the priority queue.
     * You need to append it for the insertion to take place.
     * @param e The element to be inserted.
     */
    public void insert(T e)
    {
    	table.add(e);
    	if(size==elements.length)
    		grow();
    	elements[size]=new Node(e);
    	elements[size].desired=e.priority;
    	e.priority=null;
    	e.index=size;
    	size++;
    }
    /**
     * Change the proiroty of the element having the identifier "key".
     * You need to  append it for the alter to take place.
     * @param key the identifier.
     * @param value the disierd value.
     */
    public void alter(K key,V value)
    {
    	int index=table.get(key).index;
    	elements[index].desired=value;
    }
    /**
     * Get the element having the max priority.
     * @return The element having the max priority
     */
    public T getMax()
    {
    	return size==0? null : elements[0].element;
    }
    /**
     * Delete the element having the max priority.
     * @return The element having the max priority
     */
    public T deleteMax()
    {
    	if(size==0)return null;
    	T rtn=elements[0].element;
    	size--;
    	elements[0]=elements[size];
    	shiftDown(0);
    	table.remove(rtn);
    	return rtn;
    }
    /**
     * Write the desierd priority.
     * @param tar the element to be changed.
     */
    public void append(T tar)
    {
    	int index=tar.index;
    	V pre=tar.priority;
    	tar.priority=elements[index].desired;
    	elements[index].desired=null;
    	if(pre==null || pre.compareTo(tar.priority)<0)
    		shiftUP(index);
    	else 
    		shiftDown(index);
    }
    /**
     * Get the element having the identifier "key"
     * @param key The identifier
     * @return The element whose identifier is "key"
     */
    public T get(K key)
    {
    	return table.get(key);
    }
    /**
     * remove the element having the identifier "key"
     * @param key The identifier
     * @return The element whose identifier is "key"
     */
    public T remove(K key)
    {
    	T rtn=table.get(key);
    	int index=rtn.index;
    	table.remove(rtn);
    	size--;
    	elements[index]=elements[size];
    	shiftDown(index);
    	return rtn;
    }
}
