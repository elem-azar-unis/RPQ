package message;

import java.util.ArrayList;

public class Update extends Message
{
	private static final long serialVersionUID = -369787836302136866L;
	public ArrayList<Content<?,?>> lst;
	public Update(ArrayList<Content<?,?>> l)
	{
		type=Message.UPDATE;
		lst=l;
	}
}
