package kernel;

import java.util.ArrayList;
import java.util.Arrays;

import message.Content;

/**
 * The kernel priority queue for server. It uses ElementTable to find an element in constant time.
 * 
 * <p>This is an unbounded priority queue based on heap. This is a maximum priority queue.
 * The priority is provided by user defined Comparable.
 * 
 * <p>The basic operations: Insert, Alter, Delete max, Get max, Apply.
 * Insert, Delete max, Apply are synchronized on the queue.
 * 
 * <p>Insert and alter don't take immediate effect. They have two steps. 
 * First is to update the desired value.
 * Then, apply this desired value and fix the heap.
 * This is efficient when multiple writes on the same element within a short period.
 * It also make a response in a constant time. Only applying is time consuming.
 * 
 * <p>Also can get or remove one element given the identifier.
 * @param <K> The type of identifier.
 * @param <V> The type of value of priority.
 * @param <T> The type of element in the priority queue.
 */
public class ServerPriorityQueue<K,V extends Comparable<V>,T extends Element<K,V>>
{
	private class Node
	{
		T element;
		/**
		 * Used only by the server RPQ. The change of the priority that is to be applied in the future.
		 */
		V desired=null;
		Node(T element){this.element=element;}
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
    private ElementTable<K,T> table= new ElementTable<>();
    /**
     * @return The height of the priority queue
     */
    public int getHeight()
    {
    	if(size==0)return -1;
    	return (int)(Math.log((double)size)/Math.log(2.0));
    }
    public int getSize(){return size;}
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
     * You need to apply it for the insertion to take place.
     * @param e The element to be inserted.
     */
    public void insert(T e)
    {
    	synchronized (elements)
		{
	    	table.add(e);
	    	if(size==elements.length)
	    		grow();
	    	elements[size]=new Node(e);
	    	synchronized (e)
			{
	    		elements[size].desired=e.priority;
		    	e.priority=null;
		    	e.index=size;
			}
	    	size++;
		}
    }
    /**
     * Change the priority of the element having the identifier "key".
     * You need to  apply it for the alter to take place.
     * @param key the identifier.
     * @param value the desired value.
     * @return The element
     */
    public T alter(K key,V value)
    {
    	T rtn=table.get(key);
    	if(rtn==null)return null;
    	int index=rtn.index;
    	synchronized (rtn)
		{
    		elements[index].desired=value;
		}
    	return rtn;
    }
    /**
     * @param index the element
     * @return the desired priority of the element
     */
    public V getDesired(int index)
    {
    	return elements[index].desired;
    }
    /**
     * Get the element having the max priority.
     * @param tasks 
     * @return The element having the max priority
     */
    public Content<K,V> getMax(TaskQueue tasks)
    {
    	if(size==0)return null;
    	if(tasks.isEmpty())return new Content<K,V>(elements[0].element); 
    	
    	int index=-1;
    	for(Element<?,?>e:tasks.tasks)
    	{
    		if(index==-1 || elements[index].desired.compareTo(elements[e.index].desired)<0)
    			index=e.index;
    	}
    	

    	T top=null;
    	for(int i=0;i<=getHeight();i++)
    	{
    		boolean get=false;
    		for(int j=(int) Math.pow(2,i)-1;j<Math.max(size,Math.pow(2,i));j++)
    		{
    			if(!tasks.has(elements[j].element))
    			{
    				get=true;
    				if (top==null || top.compareTo(elements[j].element)<0)
    					top=elements[j].element;
    			}
    		}
    		if(get)break;
    	}
    	
    	if(top==null)return new Content<K,V>(elements[index].element.key,elements[index].desired);
    	return (top.priority.compareTo(elements[index].desired)<0)?
    			new Content<K,V>(elements[index].element.key,elements[index].desired) : new Content<K,V>(top);
    	/*return (elements[0].element.priority.compareTo(elements[index].desired)<0)?
    			elements[index].element : elements[0].element;*/
    }
    /**
     * Delete the element having the max priority.
     * @return The element having the max priority
     */
    public T deleteMax()
    {
    	synchronized (elements)
		{
	    	if(size==0)return null;
	    	T rtn=elements[0].element;
	    	size--;
	    	elements[0]=elements[size];
	    	shiftDown(0);
	    	table.remove(rtn);
	    	return rtn;
		}
    }
    /**
     * Write the desired priority.
     * @return The highest affected level.
     * @param tar the element to be changed.
     */
    public int apply(T tar)
    {
    	synchronized (elements)
		{
	    	int index=tar.index;
	    	V pre=tar.priority;
	    	synchronized (tar)
			{
	    		tar.priority=elements[index].desired;
		    	elements[index].desired=null;
			}    	
	    	if(pre==null || pre.compareTo(tar.priority)<0)
	    		shiftUP(index);
	    	else 
	    		shiftDown(index);
	    	index=(index<tar.index)?index:tar.index;
	    	return (int)(Math.log((double)(index+1))/Math.log(2.0));
		}
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
     * Get the level 0 to level l to update.
     * @param l the upper level
     * @return an list of the update information
     */
    public ArrayList<Content<?, ?>> getUpdate(int l)
    {
    	int n=(int)Math.pow(2,l)-1;
    	ArrayList<Content<?, ?>> lst=new ArrayList<>(n);
    	synchronized (elements)
		{
    		n=n>size? size:n;
			for(int i=0;i<n;i++)
				lst.add(new Content<K,V>(elements[i].element));
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
    	synchronized (elements)
		{
	    	T rtn=table.get(key);
	    	if(rtn==null) return null;
	    	int index=rtn.index;
	    	table.remove(rtn);
	    	size--;
	    	elements[index]=elements[size];
	    	if(rtn.compareTo(elements[index].element)>0)
	    		shiftDown(index);
	    	else
				shiftUP(index);
	    	return rtn;
		}
    }
}
