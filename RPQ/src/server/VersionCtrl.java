package server;

/**
 * Control the version of the RPQ. Tell the server if an update is needed and how to update.
 */
public interface VersionCtrl
{
	/**
	 * Update the version of the server RPQ.
	 * @param size The size of the server RPQ
	 * @param level The highest affected level.
	 * @return if the RPQ need to be updated to the clients
	 */
	public boolean update(int size,int level);
	/**
	 * @return the level needed to be updated to the clients, 0 means no need, 1 means root, ...
	 */
	public int tell();
	/**
	 * The previous update has been done. Reset the controler.
	 */
	public void reset();
}
