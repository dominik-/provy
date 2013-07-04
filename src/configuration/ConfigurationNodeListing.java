package configuration;

import java.util.ArrayList;

import cluster.INode;
import cluster.INodeManager;

public class ConfigurationNodeListing {
	
	private String nodePrefix;
	private ArrayList<INode> nodes;
	
	public ConfigurationNodeListing(INodeManager manager, String nodePrefix){
		this.nodePrefix = nodePrefix;
		this.setNodes(manager.findNodesByPrefix(nodePrefix));
	}
	
	public String getNodePrefix() {
		return nodePrefix;
	}
	public void setNodePrefix(String nodePrefix) {
		this.nodePrefix = nodePrefix;
	}
	
	public ArrayList<INode> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<INode> nodes) {
		this.nodes = nodes;
	}
	@Override
	public String toString(){
		StringBuilder build = new StringBuilder();
		for (INode node : nodes){
			build.append(node.getResource().getInternalAddress()+"\n");
		}
		return build.toString();
	}
	
}
