package cluster;

/**
 * Interface describing an actual node within a cluster. A node is typed with
 * {@link INodeType} and linked to an {@link IResource}. An {@link INode} wraps
 * both the resource properties and functionality of a node in a cluster. A set
 * of {@link INode}s is managed by an {@link INodeManager}.
 * 
 * @author Dominik
 * 
 */
public interface INode {

	/**
	 * The node's unique ID.
	 * 
	 * @return unique ID.
	 */
	public String getNodeId();

	/**
	 * Sets the node's ID to the given Value
	 * 
	 * @param id
	 */
	public void setNodeId(String id);

	/**
	 * Sets the type of an {@link INode} implementation.
	 * 
	 * @return the {@link INodeType} of this node class.
	 */
	public boolean setNodeType(INodeType nodeType);

	/**
	 * The type of an {@link INode} implementation. A class implementing
	 * {@link INodeType}.
	 * 
	 * @return the {@link INodeType} of this node class.
	 */
	public INodeType getNodeType();

	/**
	 * Sets the resource for this {@link INode}. An attached {@link IResource}
	 * is a prerequisite for other methods.
	 * 
	 * @param resource
	 *            the {@link IResource} to be attached.
	 * @return true, if the resource could be attached.
	 */
	public boolean setResource(IResource resource);

	/**
	 * Returns the {@link IResource} linked to this {@link INode}.
	 * 
	 * @return
	 */
	public IResource getResource();

	/**
	 * Method for unified command issuing (e.g. via SSH) to a node. Requires an
	 * {@link IResource} attached with this {@link INode}.
	 * 
	 * @param command
	 *            the command to be issued as a {@link String}
	 * @param timeout
	 *            the timeout command execution
	 * @return a return value, e.g. exit status.
	 */
	public String issueCommand(String command, int timeout);

	/**
	 * Checks whether an {@link IResource} is attached to this {@link INode}.
	 * 
	 * @return <code>true</code> if an {@link IResource} is attached.
	 */
	public boolean resourceAttached();
	
	/**
	 * Should return true if the node is fully operable.
	 * @return <code>true</code> if the {@link INode} is operable.
	 */
	public boolean isOperable();
	
	/**
	 * Setter for the operable state of this node.
	 * @param operable the operable state
	 */
	public void setOperable(boolean operable);

}
