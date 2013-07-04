package cluster.ec2;

import java.util.Properties;

import cluster.IResourceType;

public class EC2ResourceType implements IResourceType {

	private final static PortRange[] PORTS = { new PortRange(50000, 50100),
			new PortRange(60000, 60030), new PortRange(9000, 9001),
			new PortRange(22, 22), new PortRange(80, 80),
			new PortRange(4242, 4242), new PortRange(2181, 2181),
			new PortRange(2181, 2181, "udp") };
	private String name;
	private String ami;
	private String instanceSize;
	private String keyPair;
	private String securityGroup;
	private String userDataFile;
	private String privateKeyLocation;
	private String userName;
	private Properties properties;

	public EC2ResourceType(String name, String ami, String instanceSize,
			String keyPair, String securityGroup, String userDataFile,
			String privateKeyLocation, String userName) {
		this.name = name;
		this.ami = ami;
		this.instanceSize = instanceSize;
		this.keyPair = keyPair;
		this.securityGroup = securityGroup;
		this.userDataFile = userDataFile;
		this.privateKeyLocation = privateKeyLocation;
		this.userName = userName;
	}

	@Override
	public String getImageId() {
		return ami;
	}

	@Override
	public void setImageId(String id) {
		this.ami = id;
	}

	public String getInstanceSize() {
		return instanceSize;
	}

	public void setInstanceSize(String instanceSize) {
		this.instanceSize = instanceSize;
	}

	public String getKeyPair() {
		return keyPair;
	}

	public void setKeyPair(String keyPair) {
		this.keyPair = keyPair;
	}

	public String getSecurityGroup() {
		return securityGroup;
	}

	public void setSecurityGroup(String securityGroup) {
		this.securityGroup = securityGroup;
	}

	public String getUserData() {
		return userDataFile;
	}

	public void setUserData(String userDataFile) {
		this.userDataFile = userDataFile;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public PortRange[] getPortRanges() {
		return PORTS;
	}

	public String getPrivateKeyLocation() {
		return privateKeyLocation;
	}

	public void setPrivateKeyLocation(String privateKeyLocation) {
		this.privateKeyLocation = privateKeyLocation;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String getName() {
		return name;
	}

}
