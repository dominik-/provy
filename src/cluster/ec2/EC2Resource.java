package cluster.ec2;

import java.util.HashMap;

import ssh.SSHConnector;
import cluster.IResource;
import cluster.IResourceType;

public class EC2Resource implements IResource {

	private HashMap<TimeKey, Long> times;
	private String resourceId;
	private String publicDNS;
	private String privateDNS;
	private IResourceType resourceType;
	private SSHConnector connector;
	private boolean isLaunched;

	public EC2Resource(EC2ResourceType type) {
		this.times = new HashMap<>();
		this.resourceType = type;
		this.isLaunched = false;
	}

	@Override
	public String getResourceId() {
		return resourceId;
	}

	@Override
	public void setResourceId(String id) {
		this.resourceId = id;
	}

	@Override
	public String getExternalAddress() {
		return publicDNS;
	}

	@Override
	public void setExternalAddress(String address) {
		this.publicDNS = address;
	}

	@Override
	public String getInternalAddress() {
		return privateDNS;
	}

	@Override
	public void setInternalAddress(String address) {
		this.privateDNS = address;
	}

	@Override
	public boolean setResourceType(IResourceType type) {
		if (resourceType != null) {
			return false;
		} else {
			this.resourceType = type;
			return true;
		}
	}

	@Override
	public IResourceType getResourceType() {
		return resourceType;
	}

	public EC2ResourceType getResourceTypeImpl() {
		return (EC2ResourceType) resourceType;
	}

	/**
	 * Inserts a measurement to the time measurements.
	 * 
	 * @param key
	 * @param timeInMillis
	 * @return
	 */
	public boolean addTime(TimeKey key, long timeInMillis) {
		if (times.containsKey(key.toString())) {
			return false;
		}
		times.put(key, Long.valueOf(timeInMillis));
		return true;
	}

	public HashMap<TimeKey, Long> getTimes() {
		return times;
	}

	/**
	 * Creates a new {@link SSHConnector} object for this {@link IResource}.
	 * 
	 * @return
	 */
	public SSHConnector establishConnection() {
		this.connector = new SSHConnector(getExternalAddress(),
				getResourceTypeImpl().getPrivateKeyLocation());
		addTime(TimeKey.SSH_TIME, this.connector.getFirstConnectionTime());
		return this.connector;
	}

	public SSHConnector getSSHConnector() {
		return this.connector;
	}

	public boolean isLaunched() {
		return isLaunched;
	}

	public void setLaunched() {
		this.isLaunched = true;
	}

	@Override
	public int compareTo(IResource o) {
		if (this.getResourceId().equals(o.getResourceId())) {
			return 0;
		} else {
			return (this.getResourceId().compareTo(o.getResourceId()));
		}
	}
}
