package server;

/**
 * focus on the sqrt root of the height of the heap
 * if the root is changed, it will be updated.
 * otherwise, the version of the heap is increased linearly.
 * the threshold for update is "rate" times the focused element number.
 **/
public class LinearVersionCtrl implements VersionCtrl
{
	private int rate=0;
	private int k=0;
	private int threshold=modification;
	private int version=0;
	private static final int modification=4;

	/**
	 * @param r the rate*k=threshold.
	 */
	public LinearVersionCtrl(int r)
	{
		rate=r;
	}
	/**
	 * k=sqrt(log2(size)), up to 5 levels
	 * threshold=k*rate+modification;
	 * @see server.VersionCtrl#update(int, int)
	 */
	public boolean update(int size, int level)
	{
		double h=Math.log((double)size)/Math.log(2.0);
		k=(int) Math.sqrt(h);
		k=Math.min(k,5);
		threshold=(int)Math.pow(k,2)*rate+modification;
		if(level==0)
			version+=threshold;
		else if(level<=k)
			version++;
		return version>=threshold;
	}
	public int tell()
	{
		return version<threshold?0:k;
	}	
	public void reset()
	{
		version=0;
	}
}
