package message;

public class Ack extends Message
{
	private static final long serialVersionUID = -6523897934816597666L;
	public Ack()
	{
		type=Message.ACK;
	}
}
