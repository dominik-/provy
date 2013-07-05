package cluster.ec2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;

import cluster.AbstractResourceManager;
import cluster.IResource;
import cluster.IResourceType;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AttachVolumeRequest;
import com.amazonaws.services.ec2.model.AttachVolumeResult;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

/**
 * @author Dominik
 * 
 */
public class EC2ResourceManager extends AbstractResourceManager {

	private final static Logger logger = Logger
			.getLogger(EC2ResourceManager.class.getSimpleName());

	public final static String DEFAULT_AWS_AUTH = "aws/credentials.properties";
	public final static String DEFAULT_AWS_CONF = "aws/settings.properties";
	public final static String[] REQUIRED_CONFIG_ENTRIES = { "endpoint",
			"availability_zone" };

	private AmazonEC2Client ec2;
	private String endpoint;
	private String availabilityZone;

	private ArrayList<IResource> resources;

	public EC2ResourceManager() {
		this(null, null);
	}

	public EC2ResourceManager(String credentialsFilePath,
			String settingsFilePath) {
		resources = new ArrayList<>();
		boolean authenticated = authenticateWithProvider(credentialsFilePath);
		if (!authenticated)
			throw new RuntimeException("Authentication failed. Aborting.");
		providerSetup(settingsFilePath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cluster.IResourceManager#authenticateWithProvider()
	 */
	@Override
	public boolean authenticateWithProvider(String authenticationFile) {
		AWSCredentials credentials;
		if (authenticationFile == null) {
			try {
				credentials = new EnvironmentVariableCredentialsProvider()
						.getCredentials();
				logger.log(Level.INFO, "Credentials loaded from environment.");
				ec2 = new AmazonEC2Client(credentials);
			} catch (AmazonClientException ase) {
				try {
					credentials = new PropertiesCredentials(new File(
							DEFAULT_AWS_AUTH));
					if (!checkCredentialsValid(credentials)) {
						return false;
					}
					logger.log(Level.INFO,
							"Credentials loaded from default file.");
					ec2 = new AmazonEC2Client(credentials);
				} catch (FileNotFoundException e) {
					logger.log(Level.SEVERE,
							"No credentials found in 'aws/credentials.properties'.");
					return false;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					logger.log(Level.SEVERE,
							"Could not access AWS credentials in file '"
									+ authenticationFile + "'.");
					return false;
				}
				return true;
			}
		} else {
			try {
				credentials = new PropertiesCredentials(new File(
						authenticationFile));
				if (!checkCredentialsValid(credentials)) {
					return false;
				}
				logger.log(Level.INFO,
						"Credentials loaded from specified file.");
				ec2 = new AmazonEC2Client(credentials);
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, "No credentials found in file '"
						+ authenticationFile + "'.");
				return false;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				logger.log(Level.SEVERE,
						"Could not access AWS credentials in file '"
								+ authenticationFile + "'.");
				return false;
			}

		}
		return true;
	}

	private boolean checkCredentialsValid(AWSCredentials credentials) {
		if (credentials.getAWSAccessKeyId().isEmpty()) {
			return false;
		} else if (credentials.getAWSSecretKey().isEmpty()) {
			return false;
		}
		return true;
	}

	public void providerSetup(String setupFile) {
		Properties properties;
		if (setupFile == null) {
			properties = loadAWSConfiguration(DEFAULT_AWS_CONF);
		} else {
			properties = loadAWSConfiguration(setupFile);
		}
		if (properties == null)
			throw new RuntimeException("Configuration failed.");
		this.endpoint = properties.getProperty("endpoint");
		this.availabilityZone = properties.getProperty("availability_zone");
		ec2.setEndpoint(endpoint);
	}

	public Properties loadAWSConfiguration(String fileName) {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Properties file '" + fileName
					+ "' not found.");
			return null;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not access '" + fileName
					+ "' file.");
			return null;
		}
		// check if all required properties are contained
		for (String key : REQUIRED_CONFIG_ENTRIES) {
			if (!properties.containsKey(key)) {
				return null;
			}
		}
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cluster.IResourceManager#requestResources(cluster.IResourceType,
	 * int)
	 */
	@Override
	public ArrayList<IResource> requestResources(IResourceType resourceType,
			int number) {
		ArrayList<IResource> res = new ArrayList<>();
		EC2ResourceType ec2resType = (EC2ResourceType) resourceType;
		for (int i = 0; i < number; i++) {
			res.add(new EC2Resource(ec2resType));
		}
		// create EC2 request
		RunInstancesRequest req = new RunInstancesRequest(
				ec2resType.getImageId(), number, number)
				.withInstanceType(ec2resType.getInstanceSize())
				.withKeyName(ec2resType.getKeyPair())
				.withPlacement(new Placement(availabilityZone))
				.withSecurityGroupIds(ec2resType.getSecurityGroup())
				.withUserData(
						encodeUserData(loadUserData(ec2resType.getUserData())));
		// add measurements for request times
		// TODO is this the most appropriate position for measurement?
		long requestTime = System.currentTimeMillis();
		for (IResource resource : res) {
			resource.addTime(TimeKey.REQUEST_TIME, requestTime);
		}
		// send out EC2 request
		RunInstancesResult result = ec2.runInstances(req);
		// measure when the result is returned
		long confirmationTime = System.currentTimeMillis();
		List<Instance> ec2instances = new ArrayList<>();

		for (Instance inst : result.getReservation().getInstances()) {
			ec2instances.add(inst);
		}
		for (int j = 0; j < number; j++) {
			res.get(j).setResourceId(ec2instances.get(j).getInstanceId());
			res.get(j).addTime(TimeKey.CONFIRMATION_TIME, confirmationTime);
		}
		logger.log(Level.INFO,
				"Number of launched EC2 instances: " + res.size());
		addInboundTCPPermission(getLocalExternalIp(),
				ec2resType.getSecurityGroup(), ec2resType.getPortRanges());
		resources.addAll(res);
		return res;
	}

	private static String encodeUserData(String userData) {
		byte[] bytes = org.apache.commons.codec.binary.Base64.encodeBase64(userData.getBytes());
		return new String(bytes);
	}

	public void addInboundTCPPermission(String ip, String securityGroupName,
			PortRange[] portRanges) {
		ArrayList<String> ipRanges = new ArrayList<String>();
		ipRanges.add(ip + "/32");

		ArrayList<IpPermission> ipPermissions = new ArrayList<IpPermission>();
		IpPermission perm;
		for (PortRange range : portRanges) {
			perm = new IpPermission();
			perm.setIpProtocol(range.getProtocol());
			perm.setFromPort(range.getLower());
			perm.setToPort(range.getUpper());
			perm.setIpRanges(ipRanges);
			ipPermissions.add(perm);
		}

		AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest(
				securityGroupName, ipPermissions);
		try {
			ec2.authorizeSecurityGroupIngress(ingressRequest);
			logger.log(Level.INFO, "Added IP Address " + ip
					+ " to Inbound connections in security group "
					+ securityGroupName);
		} catch (AmazonServiceException ase) {
			logger.log(Level.INFO,
					"Remote IP Address already in permissions of security group.");
		}
	}

	private String loadUserData(String userdataFile) {
		if (userdataFile == null || userdataFile.equals("")) {
			return "";
		}
		File userdata = new File(userdataFile);
		StringBuilder userdataAsString = new StringBuilder();
		// TODO prevent too large files being loaded and run into error?
		try {
			BufferedReader reader = new BufferedReader(new FileReader(userdata));
			String line = reader.readLine();
			while (line != null) {
				userdataAsString.append(line);
				userdataAsString.append("\n");
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING, "Userdata File could not be found. ");
			return "";
		} catch (IOException e) {
			logger.log(Level.WARNING,
					"Userdata file was found, but could not be accessed.");
			return "";
		}
		return userdataAsString.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cluster.IResourceManager#releaseResources(cluster.IResource[])
	 */
	@Override
	public ArrayList<IResource> releaseResources(IResource... resources) {
		ArrayList<String> instanceIds = new ArrayList<>();
		for (IResource res : resources) {
			instanceIds.add(res.getResourceId());
		}
		TerminateInstancesRequest termReq = new TerminateInstancesRequest(
				instanceIds);
		long requestTime = System.currentTimeMillis();
		for (IResource resource : resources) {
			resource.addTime(TimeKey.REQUEST_RELEASE_TIME, requestTime);
		}
		// TerminateInstancesResult result =
		ec2.terminateInstances(termReq);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cluster.IResourceManager#releaseAllResources()
	 */
	@Override
	public void releaseAllResources() {
		// TODO Auto-generated method stub
	}

	public ArrayList<Instance> getInstanceReferences(
			ArrayList<String> instanceIds) {
		ArrayList<Instance> ec2instances = new ArrayList<>();
		DescribeInstancesResult describeResult = ec2
				.describeInstances(new DescribeInstancesRequest()
						.withInstanceIds(instanceIds));
		for (Reservation reservation : describeResult.getReservations()) {
			for (Instance instance : reservation.getInstances()) {
				ec2instances.add(instance);
			}
		}
		return ec2instances;
	}

	public Instance getInstanceReference(String instanceId) {
		Instance ec2instance = null;
		DescribeInstancesResult describeResult = ec2
				.describeInstances(new DescribeInstancesRequest()
						.withInstanceIds(instanceId));
		for (Reservation reservation : describeResult.getReservations()) {
			// can only be one instance with unique id
			for (Instance instance : reservation.getInstances()) {
				ec2instance = instance;
			}
		}
		return ec2instance;
	}

	@Override
	public boolean setupResources(ArrayList<IResource> resources)
			throws InterruptedException {
		for (int i = 0; i < resources.size(); i++) {
			Instance ec2instance = getInstanceReference(resources.get(i)
					.getResourceId());
			if (ec2instance.getState().getName().equalsIgnoreCase("running")) {
				resources.get(i).addTime(TimeKey.BOOTUP_TIME,
						System.currentTimeMillis());
				resources.get(i).setInternalAddress(
						ec2instance.getPrivateDnsName());
				resources.get(i).setExternalAddress(
						ec2instance.getPublicDnsName());
				resources.get(i).setResourceId(ec2instance.getInstanceId());
				resources.get(i).setLaunched();
			}
		}
		return true;
	}

	public void addTagToInstance(String instanceId, String tagKey,
			String tagValue) {
		Tag tag = new Tag(tagKey, tagValue);
		CreateTagsRequest request = new CreateTagsRequest().withTags(tag)
				.withResources(instanceId);
		ec2.createTags(request);
	}

	public String addVolumeToInstance(String deviceName, String instanceId,
			String volumeId) {
		AttachVolumeRequest req = new AttachVolumeRequest()
				.withDevice(deviceName).withInstanceId(instanceId)
				.withVolumeId(volumeId);
		AttachVolumeResult res = ec2.attachVolume(req);
		return res.getAttachment().getState();
	}

}
