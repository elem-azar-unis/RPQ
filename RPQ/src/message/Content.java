package message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import kernel.Element;

/**
 * The essential content of the element.
 * Use serializing to deep clone the value, in case that it may change before the message is delivered.
 * @param <K> The type of the identifier.
 * @param <V> The type of the value.
 */
public class Content<K,V extends Comparable<V>> implements Serializable
{
	private static final long serialVersionUID = 456795462902591271L;
	public K key;
	public V value;
	@SuppressWarnings("unchecked")
	public Content(K k,V v)
	{	 
		try
		{
			key=k;
			ByteArrayOutputStream bo=new ByteArrayOutputStream();    
			ObjectOutputStream oo=new ObjectOutputStream(bo);   
			oo.writeObject(v);
			ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());    
			ObjectInputStream oi=new ObjectInputStream(bi);
			value=(V) oi.readObject();
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}		
	}
	public Content(Element<K,V> e)
	{
		this(e.key,e.priority);
	}
}
