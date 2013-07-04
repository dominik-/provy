package experiment.elasticity;

import cluster.INode;
import cluster.INodeType;
import cluster.IResource;
import cluster.ec2.EC2Resource;

public class ElasticEC2Node implements INode {

	private String id;
	private ElasticNodeType nodeType;
	private EC2Resource resource;
	private boolean operable;

	@Override
	public String getNodeId() {
		return id;
	}

	@Override
	public void setNodeId(String id) {
		this.id = id;
	}

	@Override
	public boolean setNodeType(INodeType nodeType) {
		this.nodeType = (ElasticNodeType) nodeType;
		return true;
	}

	@Override
	public INodeType getNodeType() {
		return nodeType;
	}

	@Override
	public boolean setResource(IResource resource) {
		this.resource = (EC2Resource) resource;
		return false;
	}

	@Override
	public IResource getResource() {
		return resource;
	}

	@Override
	public String issueCommand(String command, int timeout) {
		return resource.getSSHConnector().issueSSHCommand(command,
				resource.getResourceTypeImpl().getUserName(), timeout);
	}

	@Override
	public boolean resourceAttached() {
		return resource != null;
	}

	@Override
	public boolean isOperable() {
		return operable;
	}

	@Override
	public void setOperable(boolean operable) {
		this.operable = operable;
	}
	
	@Override
	public String toString(){
		return getNodeId() + "type: ("+ getNodeType() + ") resource: (" + getResource() + ")";
	}

}
