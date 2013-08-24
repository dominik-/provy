package experiment.elasticity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cluster.ec2.EC2ResourceManager;
import experiment.IExperiment;
import experiment.IExperimentPhase;
import experiment.IExperimentSetup;

public class ScaleoutExperiment implements IExperiment {

	private ScalingExperimentSetup setup;
	private ArrayList<IExperimentPhase> phases;
	boolean setupFinished;
	private String experimentName;
	private ScalingExperimentLogger logger;

	public ScaleoutExperiment(String name, String configFile) {
		setupFinished = setupExperiment(configFile);
		phases = new ArrayList<>();
		this.experimentName = name;
		logger = new ScalingExperimentLogger(this);
	}

	@Override
	public ArrayList<IExperimentPhase> getPhases() {
		return phases;
	}

	@Override
	public boolean addPhase(IExperimentPhase phase) {
		return phases.add(phase);
	}

	@Override
	public void runExperiment() {
		EC2ResourceManager resourceManager = (EC2ResourceManager) setup
				.getResourceManager();
		ElasticNodeManager nodeManager = (ElasticNodeManager) setup
				.getNodeManager();
		resourceManager.addVolumeToInstance("/dev/xvdh", nodeManager
				.findNodeByTypeName("ycsb").getResource().getResourceId(),
				setup.getEbsVolume());

		SimplePhase createTable = new SimplePhase(
				"createTable",
				nodeManager.findNodeByTypeName("master"),
				1,
				"echo \"exit\" | echo \"create 'usertable', 'cf1'\" | ./hbase/hbase-0.94.6/bin/hbase shell");
		SimplePhase persistenceSetup = new SimplePhase("setup",
				nodeManager.findNodeByTypeName("ycsb"), 1,
				"sudo ./setup-experiment-persistence.sh");

		LoadPhase load = new LoadPhase("load", experimentName, 1, 4,
				nodeManager.findNodeByTypeName("ycsb"),
				nodeManager.findNodesByPrefix("slave"));

		ArrayList<IExperimentPhase> throughputsUpdate = new ArrayList<>();
		int initThreads = 35;
		for (int j = 0; j < 5; j++) {
			throughputsUpdate.add(new WarmupPhase("maxtp-update" + j,
					experimentName, nodeManager.findNodeByTypeName("ycsb"),
					300, "workload-read-50.ycsb", initThreads - j * 5));
		}
		initThreads = 25;
		ArrayList<IExperimentPhase> throughputsRead = new ArrayList<>();
		for (int j = 0; j < 4; j++) {
			throughputsRead.add(new WarmupPhase("maxtp-read" + j,
					experimentName, nodeManager.findNodeByTypeName("ycsb"),
					300, "workload-read-95.ycsb", initThreads - j * 5));
		}
		throughputsRead.add(new SimplePhase("createThroughputSummary",
				nodeManager.findNodeByTypeName("ycsb"), 1,
				"./max-throughput-getter.sh " + experimentName));
		throughputsUpdate.add(new SimplePhase("createThroughputSummary",
				nodeManager.findNodeByTypeName("ycsb"), 1,
				"./max-throughput-getter.sh " + experimentName));

		addPhase(createTable);
		addPhase(persistenceSetup);
		addPhase(load);
		phases.addAll(throughputsRead);

		ArrayList<IExperimentPhase> preparation = new ArrayList<>();
		ArrayList<IExperimentPhase> run = new ArrayList<>();
		preparation.add(createTable);
		preparation.add(persistenceSetup);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		String input = "";
		System.out
				.println("Entering Management CLI. To leave, type 'exit'. Available commands: cluster, prepare, load, maxtp-read, maxtp-update, start-read, start-update, scaleout, scalein");
		try {
			while (!input.equalsIgnoreCase("exit")) {
				input = reader.readLine();
				if (input.equalsIgnoreCase("cluster")) {
					System.out.println(nodeManager);
				} else if (input.equalsIgnoreCase("prepare")) {
					logger.log(input);
					for (int i = 0; i < preparation.size(); i++) {
						Thread t = new Thread(preparation.get(i));
						while (i > 0 && preparation.get(i).dependsOnPrevious()
								&& !preparation.get(i - 1).isFinished()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						logger.log(preparation.get(i));
						t.start();
					}
				} else if (input.equalsIgnoreCase("load")) {
					logger.log(input);
					Thread t = new Thread(load);
					logger.log(load);
					t.start();
				} else if (input.equalsIgnoreCase("maxtp-read")) {
					logger.log(input);
					for (int i = 0; i < throughputsRead.size(); i++) {
						Thread t = new Thread(throughputsRead.get(i));
						while (i > 0
								&& throughputsRead.get(i).dependsOnPrevious()
								&& !throughputsRead.get(i - 1).isFinished()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						logger.log(throughputsRead.get(i));
						t.start();
					}

				} else if (input.equalsIgnoreCase("maxtp-update")) {
					logger.log(input);
					for (int i = 0; i < throughputsUpdate.size(); i++) {
						Thread t = new Thread(throughputsUpdate.get(i));
						while (i > 0
								&& throughputsUpdate.get(i).dependsOnPrevious()
								&& !throughputsUpdate.get(i - 1).isFinished()) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						logger.log(throughputsUpdate.get(i));
						t.start();
					}
				} else if (input.startsWith("start-read")) {
					if (input.split(" ").length < 4) {
						System.out.println("Required parameters (split by space): target throughput [req/s], #threads, #nodes added");
						continue;
					}
					logger.log(input);
					int throughputRead = Integer.parseInt(input.split(" ")[1]);
					int threadsRead = Integer.parseInt(input.split(" ")[2]);
					int number = Integer.parseInt(input.split(" ")[3]);
					run.add(new RunWorkloadPhase("run-read", experimentName,
							throughputRead, threadsRead, 660, nodeManager
									.findNodeByTypeName("ycsb"),
							"workload-read-95.ycsb"));
					if (number > 0) {
						run.add(new ScaleoutPhase("scaleout", setup, number));
					}
					for (int i = 0; i < run.size(); i++) {
						Thread t = new Thread(run.get(i));
						logger.log(run.get(i));
						t.start();
					}
				} else if (input.startsWith("start-update")) {
					if (input.split(" ").length < 4) {
						System.out.println("Required parameters (split by space): target throughput [req/s], #threads, #nodes added");
						continue;
					}
					logger.log(input);
					int throughputRead = Integer.parseInt(input.split(" ")[1]);
					int threadsRead = Integer.parseInt(input.split(" ")[2]);
					int number = Integer.parseInt(input.split(" ")[3]);
					run.add(new RunWorkloadPhase("run-update", experimentName,
							throughputRead, threadsRead, 660, nodeManager
									.findNodeByTypeName("ycsb"),
							"workload-read-50.ycsb"));
					if (number > 0) {
						run.add(new ScaleoutPhase("scaleout", setup, number));
					}
					for (int i = 0; i < run.size(); i++) {
						Thread t = new Thread(run.get(i));
						logger.log(run.get(i));
						t.start();
					}
				} else if (input.startsWith("scaleout")) {
					int number = Integer.parseInt(input.split(" ")[1]);
					if (input.split(" ").length != 1) {
						System.out.println("Required parameters: #nodes added");
						continue;
					}
					try {
						setup.scaleOut(number);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (input.startsWith("scalein")) {
					int number = Integer.parseInt(input.split(" ")[1]);
					if (input.split(" ").length != 1) {
						System.out.println("Required parameters: #nodes added");
						continue;
					}
					setup.scaleIn(number);
				}
			}
			reader.close();
			logger.closeLogger();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean setupExperiment(String configFile) {
		setup = new ScalingExperimentSetup(
				configFile);
		return setup.createSetup();
	}

	@Override
	public IExperimentSetup getExperimentSetup() {
		return setup;
	}

	@Override
	public String getName() {
		return experimentName;
	}

}
