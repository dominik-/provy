package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import cluster.INode;
import cluster.INodeManager;

/**
 * Class to update configuration files. Uses a copy of the configs_template
 * folder and writes EC2 internal DNS-names into files as specified by the
 * node_assignment. Default files, such as "slaves" and "masters" are also
 * generated.
 * 
 * @author Dominik
 * 
 */
public class ConfigurationUpdater {
	private final static String TMP_PATH = "configs/";
	private ConfigurationCollection configurations;
	private INodeManager nodeManager;
	private String templateFolder;
	private String targetFile;

	public ConfigurationUpdater(INodeManager nodeManager,
			String templateFolder, String targetFile) {
		configurations = new ConfigurationCollection();
		this.nodeManager = nodeManager;
		this.templateFolder = templateFolder;
		this.targetFile = targetFile;
		configurations.addAssignment("ganglia/gmond.conf", "ycsb", "GMETAD_IP");
		configurations
				.addAssignment("ganglia/gmetad.conf", "ycsb", "GMETAD_IP");
		configurations.addAssignment("hbase/hbase-site.xml", "master",
				"MASTER_IP");
		configurations.addAssignment("hbase/hbase-site.xml", "master", "ZK_IP");
		configurations.addAssignment("hadoop/hdfs-site.xml", "master",
				"MASTER_IP");
		configurations.addAssignment("hbase/hadoop-metrics.properties", "ycsb",
				"GMETAD_IP");
		configurations.addAssignment("hadoop/hadoop-metrics2.properties",
				"ycsb", "GMETAD_IP");
	}

	public void cloneConfiguration(String path) {
		try {
			FileUtils.copyDirectory(new File(path), new File(TMP_PATH));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void replaceInFile(File file, String replaced, String replacement) {
		String wholeFile;
		try {
			FileInputStream input = new FileInputStream(file);
			wholeFile = IOUtils.toString(input, "UTF-8");
			wholeFile = wholeFile.replaceAll(replaced, replacement);
			input.close();
			FileOutputStream output = new FileOutputStream(file);
			IOUtils.write(wholeFile, output, "UTF-8");
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void writeLinesToFile(File target, ArrayList<INode> nodes) {
		FileOutputStream output;
		try {
			if (!target.exists()) {
				target.createNewFile();
			}
			output = new FileOutputStream(target);
			ArrayList<String> lines = new ArrayList<>();
			for (INode node : nodes) {
				lines.add(node.getResource().getInternalAddress());
			}
			IOUtils.writeLines(lines, IOUtils.LINE_SEPARATOR_UNIX, output);
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateConfiguration() {
		cloneConfiguration(templateFolder);
		for (ConfigurationNodeAssignment assignment : configurations
				.getAssignments()) {
			replaceInFile(new File(TMP_PATH + assignment.getConfigFileName()),
					assignment.getPlaceholder(), nodeManager
							.findNodeByTypeName(assignment.getNodeTypeName())
							.getResource().getInternalAddress());
		}
		ConfigurationNodeListing slavesListing = new ConfigurationNodeListing(
				nodeManager, "slave");
		ConfigurationNodeListing mastersListing = new ConfigurationNodeListing(
				nodeManager, "master");
		ConfigurationNodeListing ycsbListing = new ConfigurationNodeListing(
				nodeManager, "ycsb");

		writeLinesToFile(new File(TMP_PATH + "hadoop/slaves"),
				slavesListing.getNodes());
		writeLinesToFile(new File(TMP_PATH + "hbase/regionservers"),
				slavesListing.getNodes());
		writeLinesToFile(new File(TMP_PATH + "hadoop/masters"),
				mastersListing.getNodes());
		ArrayList<INode> allNodes = new ArrayList<>();
		allNodes.addAll(slavesListing.getNodes());
		allNodes.addAll(mastersListing.getNodes());
		allNodes.addAll(ycsbListing.getNodes());
		writeLinesToFile(new File (TMP_PATH + "internaldnslist"), allNodes);
	}

	public void makeZip() {
		File zipFile = new File(targetFile);
		try {
			if (zipFile.exists()) {
				zipFile.delete();
				zipFile.createNewFile();
			}
			ConfigurationZip.zip(new File(TMP_PATH), zipFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
