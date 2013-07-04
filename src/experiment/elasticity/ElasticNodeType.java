package experiment.elasticity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cluster.INodeType;

public class ElasticNodeType implements INodeType {

	private final static Logger logger = Logger.getLogger(ElasticNodeType.class
			.getSimpleName());
	private String name;
	private String bootScript;
	private String configurationScript;
	private String updateScript;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	private String loadBashScript(String file) {
		if (file == null || file.equals("")) {
			return "";
		}
		File script = new File(file);
		StringBuilder scriptBuild = new StringBuilder();
		// TODO prevent too large files being loaded and run into error?
		try {
			BufferedReader reader = new BufferedReader(new FileReader(script));
			String line = reader.readLine();
			while (line != null) {
				scriptBuild.append(line);
				scriptBuild.append("\n");
				line = reader.readLine();
			}
			reader.close();
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING, "Script File could not be found. ");
			return "";
		} catch (IOException e) {
			logger.log(Level.WARNING,
					"Script file was found, but could not be accessed.");
			return "";
		}
		return scriptBuild.toString();
	}

	@Override
	public String getBootScript() {
		return bootScript;
	}

	@Override
	public void setBootScript(String bootScriptFile) {
		this.bootScript = loadBashScript(bootScriptFile);
	}

	@Override
	public String getConfigurationScript() {
		return configurationScript;
	}

	@Override
	public void setConfigurationScript(String configScriptFile) {
		this.configurationScript = loadBashScript(configScriptFile);
	}

	@Override
	public String getUpdateScript() {
		return updateScript;
	}

	@Override
	public void setUpdateScript(String updateScriptFile) {
		this.updateScript = loadBashScript(updateScriptFile);
	}

}
