package kernel;

import java.util.Hashtable;

/**
 * The hash table of the elements. We use this to find the element in constant time.
 * @param <K> The type of the identifier of the element.
 * @param <V> The type of the element.
 */
class ElementTable<K,V extends Element<K,?>>
{
	Hashtable<K,V> table=new Hashtable<K,V>();
	/**
	 * Maps the <code>key</code> of the <code>element</code> to the element
     * in this table. The element can't be <code>null</code>. <p>
     *
     * The element can be retrieved by calling the <code>get</code> method
     * with a key that is equal to the original key.
     * 
	 * @param element the element
	 */
	void add(V element)
	{
		table.put(element.key,element);
	}
	/**
	 * Use the identifier to get the element.
	 * @param key The identifier of the element.
	 * @return The element has the identifier "key". Return {@code null} if can not find it.
	 */
	V get(K key)
	{
		return table.get(key);
	}
}