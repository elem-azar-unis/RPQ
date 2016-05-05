package message;

/**
 * Sent from server means an answer. Sent from client means a request.
 */
public class Delete extends Message
{
	private static final long serialVersionUID = -4410531767400429895L;
	public Content<?,?> content;
	public boolean acquired=false;
	public Delete(Content<?,?> c)
	{
		type=Message.DELETE;
		content=c;
	}
}
