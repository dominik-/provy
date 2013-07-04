package experiment.elasticity;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import configuration.ConfigurationUpdater;
import configuration.S3Uploader;

import cluster.INode;
import cluster.INodeManager;
import cluster.INodeType;
import cluster.IResourceManager;
import cluster.IResourceType;
import cluster.ec2.EC2ResourceManager;
import cluster.ec2.EC2ResourceType;
import cluster.ec2.TimeKey;
import experiment.IExperimentSetup;

public class ScalingExperimentSetup implements IExperimentSetup {

	private final static String[] REQUIRED_CONFIG_ENTRIES = {"config_bucket","config_file","template_folder"};
	private final static Logger logger = Logger
			.getLogger(ScalingExperimentSetup.class.getSimpleName());
	ArrayList<IResourceType> resourceTypes;
	ArrayList<INodeType> nodeTypes;
	private ElasticNodeManager nodeManager;
	private EC2ResourceManager resourceManager;
	private ConfigurationUpdater config;
	private EC2ResourceType masterResource;
	private EC2ResourceType slaveResource;
	private EC2ResourceType ycsbResource;
	private ElasticNodeType masterNode;
	private ElasticNodeType slaveNode;
	private ElasticNodeType ycsbNode;
	private ArrayList<INode> initialSetup;
	private String configBucket;
	private String configFile;
	private String templateFolder;
	private String experimentConfig;

	public ScalingExperimentSetup() {
		nodeManager = new ElasticNodeManager();
		resourceManager = new EC2ResourceManager();
		nodeTypes = new ArrayList<>();
		resourceTypes = new ArrayList<>();
		experimentConfig = "experiments/test-experiment.properties";
		configureSetup(experimentConfig);
		config = new ConfigurationUpdater(nodeManager, templateFolder,
				configFile);
		masterResource = new EC2ResourceType("masterResource", "ami-f3e2ff87",
				"m1.small", "hbase", "hbase", "setups/userdata_master.txt",
				"aws/hbase.pem", "ubuntu");
		slaveResource = new EC2ResourceType("slaveResource", "ami-f3e2ff87",
				"m1.small", "hbase", "hbase", "setups/userdata_slave.txt",
				"aws/hbase.pem", "ubuntu");
		ycsbResource = new EC2ResourceType("ycsbResource", "ami-f3e2ff87",
				"m1.small", "hbase", "hbase", "setups/userdata_ycsb.txt",
				"aws/hbase.pem", "ubuntu");
		resourceTypes.add(masterResource);
		resourceTypes.add(slaveResource);
		resourceTypes.add(ycsbResource);
		masterNode = new ElasticNodeType();
		masterNode.setName("master");
		masterNode.setConfigurationScript("setups/config_master.txt");
		masterNode.setUpdateScript("setups/update_master.txt");
		slaveNode = new ElasticNodeType();
		slaveNode.setName("slave");
		slaveNode.setConfigurationScript("setups/config_slave.txt");
		slaveNode.setUpdateScript("setups/update_slave.txt");
		ycsbNode = new ElasticNodeType();
		ycsbNode.setName("ycsb");
		ycsbNode.setConfigurationScript("setups/config_ycsb.txt");
		ycsbNode.setUpdateScript("setups/update_ycsb.txt");
		nodeTypes.add(masterNode);
		nodeTypes.add(slaveNode);
		nodeTypes.add(ycsbNode);
		ArrayList<INode> master = nodeManager.addNodes(resourceManager,
				masterNode, masterResource, 1);
		ArrayList<INode> slave = nodeManager.addNodes(resourceManager,
				slaveNode, slaveResource, 1);
		ArrayList<INode> ycsb = nodeManager.addNodes(resourceManager, ycsbNode,
				ycsbResource, 1);
		initialSetup = new ArrayList<>();
		initialSetup.addAll(master);
		initialSetup.addAll(slave);
		initialSetup.addAll(ycsb);
	}

	public void configureSetup(String experimentConfig) {
		Properties properties = loadConfiguration(experimentConfig);
		if (properties == null)
			throw new RuntimeException("Experiment configuration failed.");
			configBucket = properties.getProperty("config_bucket");
			configFile = properties.getProperty("config_file");
			templateFolder=properties.getProperty("template_folder");
	}

	public Properties loadConfiguration(String fileName) {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Properties file '" + fileName
					+ "' not found.");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not access '" + fileName
					+ "' file.");
		}
		// check if all required properties are contained
		for (String key : REQUIRED_CONFIG_ENTRIES) {
			if (!properties.containsKey(key)) {
				return null;
			}
		}
		return properties;
	}

	@Override
	public ArrayList<IResourceType> getResourceTypes() {
		return resourceTypes;
	}

	@Override
	public ArrayList<INodeType> getNodeTypes() {
		return nodeTypes;
	}

	@Override
	public INodeManager getNodeManager() {
		return nodeManager;
	}

	@Override
	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	public boolean checkReadyState(ArrayList<INode> nodes) {
		boolean addedOperable = false;
		while (!addedOperable) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			addedOperable = true;
			for (INode node : nodes) {
				if (!node.isOperable()) {
					addedOperable = false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean scaleOut(int number) throws InterruptedException {
		ArrayList<INode> added = nodeManager.addNodes(resourceManager,
				slaveNode, slaveResource, number);
		nodeManager.launchNodes(resourceManager, added);
		update(added);
		nodeManager.configureNodes(resourceManager, added);
		checkReadyState(added);
		DurationsLogger logger = new DurationsLogger(added);
		logger.logLaunchDurations();
		return true;
	}

	public boolean update(ArrayList<INode> exempt) {
		config.updateConfiguration();
		config.makeZip();
		S3Uploader uploader = new S3Uploader();
		uploader.uploadFile(configFile, configBucket);
		boolean isExempt = false;
		for (INode node : nodeManager.getNodes()) {
			isExempt = false;
			for (INode exemptCheck : exempt) {
				if (exemptCheck.getResource().compareTo(node.getResource()) == 0) {
					isExempt = true;
				}
			}
			if (!isExempt) {
				node.issueCommand(node.getNodeType().getUpdateScript(), 1);
				node.getResource().addTime(TimeKey.UPDATE_TIME,
						System.currentTimeMillis());
			}
		}
		return true;
	}

	@Override
	public boolean scaleIn(int number) {
		ArrayList<INode> candidates = nodeManager.findNodesByPrefix("slave");
		int maxNumber = candidates.size();
		INode[] toRemove = new INode[number];
		for (int i = 0; i < number; i++) {
			toRemove[i] = candidates.get(maxNumber - i - 1);
		}
		nodeManager.removeNodes(resourceManager, toRemove);
		return true;
	}

	@Override
	public boolean createSetup() {
		nodeManager.launchNodes(resourceManager, initialSetup);
		DurationsLogger logger = new DurationsLogger(initialSetup);
		config.updateConfiguration();
		config.makeZip();
		S3Uploader uploader = new S3Uploader();
		uploader.uploadFile(configFile, configBucket);
		try {
			nodeManager.configureNodes(resourceManager, initialSetup);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		checkReadyState(initialSetup);
		logger.logLaunchDurations();
		return true;
	}

}
