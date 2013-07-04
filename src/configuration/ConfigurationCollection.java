package configuration;

import java.util.ArrayList;

public class ConfigurationCollection {

	private ArrayList<ConfigurationNodeAssignment> assignments;

	public ConfigurationCollection() {
		assignments = new ArrayList<>();
	}

	public void addAssignment(ConfigurationNodeAssignment assignment) {
		assignments.add(assignment);
	}

	public void addAssignment(String configFileName, String nodeTypeName,
			String placeholder) {
		assignments.add(new ConfigurationNodeAssignment(configFileName,
				nodeTypeName, placeholder));
	}
	
	public ArrayList<ConfigurationNodeAssignment> getAssignments(){
		return assignments;
	}

}
