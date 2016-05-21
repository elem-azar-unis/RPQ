package kernel;

import message.Content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Synchronized on the queue.
 * The kernel priority queue for client. It uses ElementTable to find an element in constant time.
 * 
 * <p>This is an unbounded priority queue based on heap. This is a maximum priority queue.
 * The priority is provided by user defined Comparable.
 * 
 * <p>The basic operations: Insert, Alter, Delete max, Get max
 * 
 * <p>Also can get or remove one element given the identifier.
 * @param <K> The type of identifier.
 * @param <V> The type of value of priority.
 * @param <T> The type of element in the priority queue.
 */
@SuppressWarnings("unchecked")
public class ClientPriorityQueue<K,V extends Comparable<V>,T extends Element<K,V>>
{
	private static final int INITIAL_CAPACITY=8;
	/**
     * Priority queue represented as a balanced binary heap: the two
     * children of queue[n] are queue[2*n+1] and queue[2*n+2].  The
     * priority queue is ordered by elements' decreasing ordering. 
     * For each node n in the heap and each descendant d of n, n >= d.  
     * The element with the highest value is in queue[0], assuming the queue is nonempty.
     */
	private Object[] elements= new Object[INITIAL_CAPACITY];
	/**
     * The size of the Priority Queue (the number of elements it contains).
     *
     * @serial
     */
    private int size=0;
    /**
     * The hash table of the element.
     */
    private ElementTable<K,T> table= new ElementTable<>();
    /**
     * Increases the capacity by doubling it.
     */
    private void grow()
    {
    	elements=Arrays.copyOf(elements,2*elements.length);
    }
    /**
     * Fix the heap from position k (let's call it x). Maintaining heap invariant by
     * promoting x up the tree until it is less than or equal to its parent, or is the root.
     * @param k The position to be fixed.
     */
    private void shiftUP(int k)
    {
    	T e= (T) elements[k];
    	while(k>0)
    	{
    		int parent=(k-1)>>>1;
    		if(e.compareTo((T) elements[parent])<=0)
    			break;
    		elements[k]=elements[parent];
			((T)elements[k]).index=k;
    		k=parent;
    	}
    	elements[k]=e;
    	e.index=k;
    }
    /**
     * Fix the heap from position k (let's call it x). Maintaining heap invariant by
     * demoting x down the tree until it is greater than or equal to its children, or is a leaf.
     * @param k The position to be fixed.
     */
    private void shiftDown(int k)
    {
    	T e= (T) elements[k];
    	int half=size>>>1;            // loop while a non-leaf
    	while(k<half)
    	{
    		int child=(k<<1)+1;       // assume left child is largest
    		int right=child+1;
    		if(right<size && ((T)elements[child]).compareTo((T) elements[right])<0)
    			child=right;
    		if(e.compareTo((T) elements[child])>=0)
    			break;
    		elements[k]=elements[child];
			((T)elements[k]).index=k;
    		k=child;
    	}
    	elements[k]=e;
    	e.index=k;
    }
    /**
     * Insert a new element in the priority queue.
     * @param e The element to be inserted.
     */
    public void insert(T e)
    {
    	synchronized (this)
		{		
	    	table.add(e);
	    	if(size==elements.length)
	    		grow();
	    	elements[size]=e;
	    	e.index=size;
	    	size++;
	    	shiftUP(e.index);
		}
    }
    /**
     * @param key the identifier
     * @return true if the priority queue contains the element having the identifier "key"
     */
    public boolean contains(K key)
    {
    	return table.get(key)!=null;
    }
    /**
     * Change the priority of the element having the identifier "key".
     * @param key the identifier.
     * @param value the desired value.
     */
    public void alter(K key,V value)
    {
    	synchronized (this)
		{
    		T temp=table.get(key);
    		if(temp==null)return;
	    	int index=temp.index;
	    	if(((T)elements[index]).priority.compareTo(value)>0)
	    	{
				((T)elements[index]).priority=value;
	    		shiftDown(index);
	    	}
	    	else
	    	{
				((T)elements[index]).priority=value;
	    		shiftUP(index);
	    	}
		}
    }
    /**
     * Get the element having the max priority.
     * @return The element having the max priority
     */
    public T getMax()
    {
    	synchronized (this)
		{
    		return size==0? null : (T) elements[0];
		}
    }
    /**
     * Delete the element having the max priority.
     * @return The element having the max priority
     */
    public T deleteMax()
    {
    	synchronized (this)
		{
	    	if(size==0)return null;
	    	T rtn= (T) elements[0];
	    	size--;
	    	elements[0]=elements[size];
	    	shiftDown(0);
	    	table.remove(rtn);
	    	return rtn;
		}
    }
    /**
     * Get the element having the identifier "key"
     * @param key The identifier
     * @return The element whose identifier is "key"
     */
    public T get(K key)
    {
    	synchronized (this)
		{
    		return table.get(key);
		}
    }
	/**
	 * Get the level 0 to level l to update.
	 * @param l the upper level
	 * @return an list of the update information
	 */
	public ArrayList<Content<?, ?>> getUpdate(int l)
	{
		int n=(int)Math.pow(2,l)-1;
		ArrayList<Content<?, ?>> lst=new ArrayList<>(n);
		synchronized (this)
		{
			n=n>size? size:n;
			for(int i=0;i<n;i++)
				lst.add(new Content<>((T) elements[i]));
		}
		return lst;
	}
    /**
     * remove the element having the identifier "key"
     * @param key The identifier
     * @return The element whose identifier is "key"
     */
    public T remove(K key)
    {
    	synchronized (this)
		{
	    	T rtn=table.get(key);
	    	if(rtn==null)return null;
	    	int index=rtn.index;
	    	table.remove(rtn);
	    	size--;
	    	elements[index]=elements[size];
	    	if(rtn.compareTo((T) elements[index])>0)
	    		shiftDown(index);
	    	else
				shiftUP(index);
	    	return rtn;
		}
    }

	public void update(ArrayList<Content<?, ?>>arr,V inf)
	{
		synchronized (this)
		{
			if(arr.size()==0)return;
			HashSet<K> temp = new HashSet<>();
			for (Content<?, ?> content : arr)
			{
				temp.add((K) content.key);
				if (contains((K) content.key))
				{
					alter((K) content.key, (V) content.value);
				} else
				{
					insert((T) new Element<>((K) content.key, (V) content.value));
				}
			}
			while(!temp.contains(((T) elements[0]).key))
			{
				((T)elements[0]).priority=inf;
				shiftDown(0);
			}
			for (int i = 0; i < arr.size(); i++)
			{
				if (!temp.contains(((T) elements[i]).key))
				{
					((T)elements[i]).priority=inf;
					shiftDown(i);
				}
			}
		}
	}

	public K getOne(int in)
	{
		return ((T)elements[Math.abs(in)%size]).key;
	}
	public int getSize()
	{
		return size;
	}
}
