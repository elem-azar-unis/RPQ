package message;

import java.io.Serializable;

import kernel.Element;

/**
 * The essential content of the element.
 * @param <K> The type of the identifier.
 * @param <V> The type of the value.
 */
public class Content<K,V extends Comparable<V>> implements Serializable
{
	private static final long serialVersionUID = 456795462902591271L;
	public K key;
	public V value;
	public Content(K k,V v)
	{
		key=k;
		value=v;
	}
	public Content(Element<K,V> e)
	{
		key=e.key;
		value=e.priority;
	}
}
