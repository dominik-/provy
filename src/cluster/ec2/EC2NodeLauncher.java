package cluster.ec2;

import cluster.INode;
import cluster.INodeLauncher;
import experiment.elasticity.ElasticNodeManager;

public class EC2NodeLauncher implements INodeLauncher {

	INode node;
	ElasticNodeManager nodeManager;
	EC2ResourceManager resourceManager;
	Thread thread;

	public EC2NodeLauncher(INode node) {
		this.node = node;
		thread = new Thread(this);
	}

	@Override
	public void run() {
		EC2Resource ec2res = (EC2Resource) node.getResource();
		ec2res.establishConnection();
		node.issueCommand(node.getNodeType().getConfigurationScript(), 2);
		node.getResource().addTime(TimeKey.CONFIGURATION_TIME,
				System.currentTimeMillis());
		node.setOperable(true);
	}

	@Override
	public void setNode(INode node) {
		this.node = node;
	}

	@Override
	public INode getNode() {
		return node;
	}

	public void launch() {
		thread.start();
	}

}
