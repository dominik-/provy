package cluster;

/**
 * Launcher, which is responsible for the startup and integration of a single node.
 * Runnable, because it should be possible to launch multiple nodes simultaneously.
 * @author dominik
 *
 */
public interface INodeLauncher extends Runnable {

	
	public void setNode(INode node);
	public INode getNode();
	
	
}
