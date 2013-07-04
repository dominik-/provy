package cluster;

import java.util.Properties;

/**
 * Represents a resource type, which is assumed by an {@link IResource}.
 * 
 * @author Dominik
 * 
 */
public interface IResourceType {

	/**
	 * Name of the resource, used as an identifier.
	 * @return the name of this {@link IResourceType}
	 */
	public String getName();
	
	/**
	 * The unique identifier for the system to be run on the IaaS resource.
	 * 
	 * @return the ID of an OS image
	 */
	public String getImageId();

	/**
	 * Setter for the image ID (e.g. Amazon Machine Image)
	 * 
	 * @param id
	 *            the ID of the image to be loaded.
	 */
	public void setImageId(String id);

	/**
	 * Getter for additional properties of this {@link IResourceType}.
	 * @return reference to the properties
	 */
	public Properties getProperties();
	
	/**
	 * Assigns a set of arbitrary properties to this {@link IResourceType}
	 * @param new properties to be set.
	 */
	public void setProperties(Properties properties);

}
