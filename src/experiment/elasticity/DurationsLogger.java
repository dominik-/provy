package experiment.elasticity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cluster.INode;
import cluster.ec2.EC2Resource;
import cluster.ec2.TimeKey;

public class DurationsLogger {
	private final static String LOGS_PATH = "logs/";
	private final static String FILE_PREFIX = "launch-";
	private final static String FILE_SUFFIX = ".log";
	private final static SimpleDateFormat FILENAME_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss");
	private final static SimpleDateFormat ENTRY_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss Z");
	// TODO add more times as soon as measured/measurable
	private final static String[] VALUES = { "request_duration(ms)",
			"boot_duration(ms)", "ssh_duration(ms)", "config_duration(ms)" };

	private ArrayList<INode> nodes;

	public DurationsLogger(ArrayList<INode> nodes) {
		this.nodes = nodes;
	}

	public void logLaunchDurations() {

		File logfile = new File(LOGS_PATH
				+ FILE_PREFIX
				+ FILENAME_DATE_FORMAT.format(new Date(System
						.currentTimeMillis())) + FILE_SUFFIX);
		PrintWriter writer;
		try {
			logfile.createNewFile();
			writer = new PrintWriter(logfile);

			writer.println(getKeyLine());

			for (INode node : nodes) {
				EC2Resource ec2res = (EC2Resource) node.getResource();
				writer.print(ENTRY_DATE_FORMAT.format(new Date(ec2res.getTimes().get(
						TimeKey.REQUEST_TIME)))
						+ ","
						+ node.getNodeId()
						+ ","
						+ node.getResource().getResourceType().getImageId()
						+ ","
						+ node.getResource().getInternalAddress());
				writer.print(","
						+ (ec2res.getTimes().get(TimeKey.CONFIRMATION_TIME) - ec2res
								.getTimes().get(TimeKey.REQUEST_TIME)));
				writer.print(","
						+ (ec2res.getTimes().get(TimeKey.BOOTUP_TIME) - ec2res
								.getTimes().get(TimeKey.CONFIRMATION_TIME)));
				writer.print(","
						+ (ec2res.getTimes().get(TimeKey.SSH_TIME) - ec2res
								.getTimes().get(TimeKey.BOOTUP_TIME)));
				writer.print(","
						+ (ec2res.getTimes().get(TimeKey.CONFIGURATION_TIME) - ec2res
								.getTimes().get(TimeKey.SSH_TIME)));
				writer.println(";");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getKeyLine() {
		StringBuilder keys = new StringBuilder(
				"requestTime,name,ami,privateDNS");
		// use one LaunchedInstance object to get all used TimeKeys
		for (String key : VALUES) {
			keys.append(",");
			keys.append(key);
		}
		keys.append(";");
		return keys.toString();
	}
}
