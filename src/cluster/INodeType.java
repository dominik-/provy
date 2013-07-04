package cluster;

/**
 * The type (and configuration) of a node. For example "master".
 * 
 * @author hadoop-user
 * 
 */
public interface INodeType {

	/**
	 * Returns the name of a node type. Usually a hint describing its
	 * functionality.
	 * 
	 * @return the name of this node type
	 */
	public String getName();

	/**
	 * Setter for the node type name.
	 * 
	 * @param name
	 *            the name for a node type
	 */
	public void setName(String name);

	/**
	 * Getter for the boot script. E.g. a bash-script run at boot time by
	 * Amazon's userdata mechanism.
	 * 
	 * @return the script to be executed at boot time.
	 */
	public String getBootScript();

	/**
	 * Specifies the location of a boot script file. Should include an
	 * appropriate loading mechanism to read from file.
	 * 
	 * @param bootScript
	 */
	public void setBootScript(String bootScriptFile);

	/**
	 * Getter for a configuration script, which is run after boot. For example
	 * configuration exchange or process starts.
	 * 
	 * @return a {@link String}-representation of the configuration script.
	 */
	public String getConfigurationScript();

	/**
	 * Specifies the location of a configuration script file. Should include an
	 * appropriate loading mechanism to read from file.
	 * 
	 * @param configScript
	 */
	public void setConfigurationScript(String configScriptFile);

	/**
	 * Getter for a script, which should be run after the configuration of the
	 * cluster has changed.
	 * 
	 * @return
	 */
	public String getUpdateScript();

	/**
	 * Specifies the location of a script file. Should include an appropriate
	 * loading mechanism to read from file.
	 * 
	 * @param updateScript
	 */
	public void setUpdateScript(String updateScriptFile);

}
