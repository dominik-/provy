package cluster;

import java.util.ArrayList;

/**
 * Describes the functionality that is required to setup a Cluster.
 * 
 * @author Dominik
 * 
 */
public interface INodeManager {

	public boolean initiateCluster();

	/**
	 * Method that handles adding of new {@link INode}s to the cluster. Includes
	 * resource requests to an IaaS provider and node initialization.
	 * 
	 * @param manager
	 *            an {@link IResourceManager} for wrapping resource requests to
	 *            an actual provider.
	 * @param nodeType
	 *            the {@link INodeType} which should be used for the creation of
	 *            {@link INode}s
	 * @param resourceType
	 *            the {@link IResourceType} of the node to be added.
	 * @param number
	 *            the number of nodes (of the same type) to be added
	 *            simultaneously.
	 * @return an {@link ArrayList} containing references to the newly added
	 *         nodes.
	 * @throws InterruptedException 
	 */
	public ArrayList<INode> addNodes(IResourceManager manager,
			INodeType nodeType, IResourceType resourceType, int number) throws InterruptedException;

	/**
	 * Method that handles removal of {@link INode}s from the cluster.
	 * 
	 * @param manager
	 *            an {@link IResourceManager} for wrapping resource requests to
	 *            an actual provider.
	 * @param nodes
	 *            the {@link INode}s which should be removed
	 * 
	 * @return an {@link ArrayList} containing references to the removed nodes.
	 */
	public ArrayList<INode> removeNodes(IResourceManager manager,
			INode... nodes);
	
	public ArrayList<INode> getNodes();
	
	public boolean configureNodes(IResourceManager manager, ArrayList<INode> nodes) throws InterruptedException;
	
	public INode findNodeByTypeName(String typeName);
	
	public ArrayList<INode> findNodesByPrefix(String prefix);

}
