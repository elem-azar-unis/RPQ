package message;


public class Delta extends Message
{
	private static final long serialVersionUID = -6745048987132057917L;
	public Content<?,?> content;
	public Delta(Content<?,?> c)
	{
		content=c;
		type=Message.DELTA;
	}
}
