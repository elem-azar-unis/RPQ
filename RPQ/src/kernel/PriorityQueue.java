package kernel;

import java.util.Arrays;

public class PriorityQueue<V extends Comparable<V>,T extends Element<?,V>>
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
     * children of queue[n] are queue[2*n+1] and queue[2*(n+1)].  The
     * priority queue is ordered by elements' decreasing ordering. 
     * For each node n in the heap and each descendant d of n, n >= d.  
     * The element with the highest value is in queue[0], assuming the queue is nonempty.
     */	
	@SuppressWarnings("unchecked")
	Node[] elements=(PriorityQueue<V,T>.Node[])new Object[INITIAL_CAPACITY];
	/**
     * The size of the Priority Queue (the number of elements it contains).
     *
     * @serial
     */
    private int size=0;
    /**
     * Increases the capacity by doubling it.
     */
    private void grow()
    {
    	elements=Arrays.copyOf(elements,2*elements.length);
    }
}
