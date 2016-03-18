package message;

public class Alter extends Message
{

	private static final long serialVersionUID = -4362632843738190583L;
	public Content<?,?> content;
	public Alter(Content<?,?>c)
	{
		content=c;
		type=Message.ALTER;
	}
}
