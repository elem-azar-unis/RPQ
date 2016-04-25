package message;

import kernel.Element;

public class Insert extends Message
{
	private static final long serialVersionUID = -8172980305687693094L;
	public Element<?,?> elememt;
	public Insert(Element<?,?> e)
	{
		type=Message.INSERT;
		elememt=e;
	}
}
