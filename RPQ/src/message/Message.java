package message;

import java.io.Serializable;

public class Message implements Serializable
{
	private static final long serialVersionUID = -8006817488241702192L;

	//constant types
	public static final short INSERT=0;
	public static final short DELETE=1;
	public static final short ALTER=2;
	public static final short DELTA=3;
	public static final short MAX=4;
	public static final short UPDATE=5;
	
	public short type;

	public String toString()
	{
		switch (type)
		{
			case INSERT:return "INSERT";
			case DELETE:return "DELETE";
			case ALTER:return "ALTER";
			case DELTA:return "DELTA";
			case MAX:return "MAX";
			case UPDATE:return "UPDATE";
		}
		return "UNKNOWN";
	}
}
