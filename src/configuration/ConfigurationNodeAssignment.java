package configuration;


public class ConfigurationNodeAssignment {

	private String configFileName;
	private String nodeTypeName;
	private String placeholder;
	
	public ConfigurationNodeAssignment(String configFileName, String nodeTypeName, String placeholder){
		this.configFileName = configFileName;
		this.nodeTypeName = nodeTypeName;
		this.placeholder = placeholder;
	}
	
	public String getConfigFileName() {
		return configFileName;
	}
	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}
	public String getNodeTypeName() {
		return nodeTypeName;
	}
	public void setNodeTypeName(String nodeTypeName) {
		this.nodeTypeName = nodeTypeName;
	}
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	
}
