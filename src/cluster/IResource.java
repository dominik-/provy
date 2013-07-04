package cluster;

import cluster.ec2.TimeKey;

/**
 * Interface describing an resource. Each {@link IResource} must have a single
 * {@link IResourceType} reference. A set of {@link IResource}s is managed by an
 * {@link IResourceManager}.
 * 
 * @author Dominik
 * 
 */
public interface IResource extends Comparable<IResource> {

	public String getResourceId();

	public void setResourceId(String id);

	/**
	 * Returns a resource's publicly visible address.
	 * 
	 * @return the external adress.
	 */
	public String getExternalAddress();

	/**
	 * Sets a resource's publicly visible address.
	 * 
	 * @param address
	 *            the new external address
	 */
	public void setExternalAddress(String address);

	/**
	 * Getter for internal address (if different from public address).
	 * 
	 * @return address only visible within the cloud
	 */
	public String getInternalAddress();

	/**
	 * Setter for internal address.
	 * 
	 * @param address
	 *            the new internal address
	 */
	public void setInternalAddress(String address);

	/**
	 * Specifies the type of this resource as an {@link IResourceType}.
	 * 
	 * @param type
	 *            the {@link IResourceType} of this resource
	 * @return <code>true</code> if the type could be successfully set.
	 */
	public boolean setResourceType(IResourceType type);

	/**
	 * Getter for the associated {@link IResourceType}.
	 * 
	 * @return the {@link IResourceType} of this {@link IResource}.
	 */
	public IResourceType getResourceType();

	/**
	 * Insert a new point of time into the list of time measurements.
	 * 
	 * @param key
	 *            a {@link TimeKey}.
	 * @param time
	 *            the point of time in milliseconds
	 * @return true if the time could be successfully inserted (i.e. no
	 *         override)
	 */
	public boolean addTime(TimeKey key, long time);
	
	/**
	 * Returns <code>true</code>, if the resource is actually launched.
	 * @return if the resource is launched.
	 */
	public boolean isLaunched();
	
	/**
	 * Sets the launched-state to true.
	 */
	public void setLaunched();

}
