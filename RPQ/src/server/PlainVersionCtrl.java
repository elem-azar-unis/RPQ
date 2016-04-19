package server;

/**
 * Update the RPQ when the root of the RPQ is affected.
 * Only update the root.
 */
public class PlainVersionCtrl implements VersionCtrl
{
	boolean root=false;
	public boolean update(int size, int level)
	{
		root=(level==0);
		return root;
	}
	public int tell()
	{
		return root?1:0;
	}
	public void reset()
	{
		root=false;
	}
}
