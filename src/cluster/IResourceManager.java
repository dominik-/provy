package cluster;

import java.util.ArrayList;
import java.util.Set;

/**
 * Interface describing a wrapper to an actual IaaS cloud provider. It provides
 * methods for resource request and release and other management
 * functionalities.
 * 
 * @author Dominik
 * 
 */
public interface IResourceManager {

	/**
	 * Method to contain the authentication procedure, e.g. logging in as a user
	 * with a public IaaS provider.
	 * 
	 * @return <code>true</code> if authentication was successful.
	 */
	public boolean authenticateWithProvider(String authenticationFile);
	
	public void providerSetup(String setupFile);

	/**
	 * Requests a number of resources from the provider, i.e. resource
	 * provisioning. One call of this method should be dispatched as a single
	 * request for the specified number of resources.
	 * 
	 * @param resourceType
	 *            the {@link IResourceType} describing the requested resource's
	 *            capabilities and resource configuration.
	 * @param number
	 *            the number of identical resources
	 * @return an {@link ArrayList} containing references to created
	 *         {@link IResource} objects.
	 */
	public ArrayList<IResource> requestResources(IResourceType resourceType,
			int number);

	/**
	 * Requests the given resources to be released (shut down) by the provider.
	 * 
	 * @param resources
	 *            a list of {@link IResource}s to be released.
	 * @return an {@link ArrayList} containing references to the shut down
	 *         resources.
	 */
	public ArrayList<IResource> releaseResources(IResource... resources);
	
	/**
	 * Getter for a list of all resources managed by this {@link IResourceManager}.
	 * @return
	 */
	public ArrayList<IResource> getResources();
	
	public void releaseAllResources();
	
	public boolean setupResources(ArrayList<IResource> resources) throws InterruptedException;

}
