package experiment.elasticity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cluster.INode;
import cluster.INodeManager;
import cluster.INodeType;
import cluster.IResource;
import cluster.IResourceManager;
import cluster.IResourceType;
import cluster.ec2.EC2NodeLauncher;
import cluster.ec2.EC2Resource;

public class ElasticNodeManager implements INodeManager {

	private final static Logger logger = Logger
			.getLogger(ElasticNodeManager.class.getSimpleName());
	private ArrayList<INode> nodes;
	private boolean nodesAdding;
	private HashMap<INodeType, Integer> typeCounter;

	public ElasticNodeManager() {
		nodes = new ArrayList<>();
		typeCounter = new HashMap<>();
	}

	@Override
	public boolean initiateCluster() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<INode> addNodes(IResourceManager manager,
			INodeType nodeType, IResourceType resourceType, int number) {
		ArrayList<IResource> resource = manager.requestResources(resourceType,
				number);
		ArrayList<INode> addedNodes = new ArrayList<>();
		ElasticEC2Node node;
		for (int i = 0; i < number; i++) {
			node = new ElasticEC2Node();
			node.setNodeType(nodeType);
			logger.log(Level.INFO,
					"creating node of type '" + nodeType.getName() + "'.");
			node.setResource(resource.get(i));
			addedNodes.add(node);
		}
		nodes.addAll(addedNodes);
		return addedNodes;
	}
	
	public void launchNodes(IResourceManager manager, ArrayList<INode> nodes){
		ArrayList<IResource> resources = new ArrayList<>();
		for (INode node : nodes) {
			resources.add(node.getResource());
		}

		for (INode node : nodes) {
			int cur = checkTypeCounter(node.getNodeType());
			node.setNodeId(node.getNodeType().getName() + cur);
			typeCounter.put(node.getNodeType(), ++cur);
		}
		
		int readyResources = 0;
		while (readyResources < nodes.size()) {
			try {
				Thread.sleep(100);
				manager.setupResources(resources);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int j = 0; j < resources.size(); j++) {
				if (resources.get(j).isLaunched()) {
					resources.remove(j);
					readyResources++;
				}
			}
		}
	}

	@Override
	public boolean configureNodes(IResourceManager manager,
			ArrayList<INode> nodes) throws InterruptedException {

		ArrayList<EC2NodeLauncher> launchers = new ArrayList<>();

			// remove ready resources for next setup iteration
			for (int k = 0; k < nodes.size(); k++) {
				EC2NodeLauncher launcher = null;
				if (nodes.get(k).getResource().isLaunched()) {
					launcher = new EC2NodeLauncher(nodes.get(k));
					launcher.launch();
					launchers.add(launcher);
				}
			}
		return true;
	}

	@Override
	public ArrayList<INode> removeNodes(IResourceManager manager,
			INode... nodes) {
		for (INode node : nodes){
			findNodeByTypeName("master").issueCommand("hbase/hbase-0.94.6/bin/graceful_stop.sh "+node.getResource().getInternalAddress(), 2);
			this.nodes.remove(node);
			manager.releaseResources(node.getResource());
		}
		//turn on autobalancer again
		findNodeByTypeName("master").issueCommand("echo \"balance_switch true\" | hbase/hbase-0.94.6/bin/hbase shell",1);
		return null;
	}

	@Override
	public ArrayList<INode> getNodes() {
		return nodes;
	}

	@Override
	public INode findNodeByTypeName(String typeName) {
		INode found = null;
		for (INode node : nodes) {
			if (node.getNodeType().getName().equalsIgnoreCase(typeName)) {
				found = node;
			}
		}
		return found;
	}

	public ArrayList<INode> findNodesByPrefix(String prefix) {
		ArrayList<INode> nodesFound = new ArrayList<>();
		for (INode node : nodes) {
			if (node.getNodeId().startsWith(prefix)) {
				nodesFound.add(node);
			}
		}
		return nodesFound;
	}

	private int checkTypeCounter(INodeType type) {
		if (!typeCounter.containsKey(type)) {
			return 0;
		} else {
			return typeCounter.get(type);
		}
	}
	
	public String toString(){
		return nodes.toString();
	}

}
