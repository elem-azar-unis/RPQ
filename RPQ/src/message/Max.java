package message;

/**
 * Sent from server means an anser. Sent from client means a request.
 */
public class Max extends Message
{
	private static final long serialVersionUID = 5203397981969463207L;
	public Content<?,?> content;
	public Max(Content<?,?> c)
	{
		type=Message.MAX;
		content=c;
	}
}
