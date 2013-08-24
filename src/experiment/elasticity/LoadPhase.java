package experiment.elasticity;

import java.util.ArrayList;

import cluster.INode;
import experiment.IExperimentPhase;

public class LoadPhase implements IExperimentPhase {

	private INode target;
	private String name;
	private String experimentName;
	private long loadfactor;
	private final static long replicationFactor = 3;
	private final static long recordSizeBytes = 1000;
	private long memorySize;
	int clusterSize;
	boolean finished;

	public LoadPhase(String name, String experimentName, int memsizeGB,
			int loadFactor, INode target, ArrayList<INode> cluster) {
		this.name = name;
		this.experimentName = experimentName;
		this.target = target;
		this.memorySize = memsizeGB;
		this.loadfactor = loadFactor;
		clusterSize = cluster.size();
		this.finished = false;
	}

	@Override
	public void run() {
		long recordcount = (memorySize * recordSizeBytes * 1000) * loadfactor
				* clusterSize / replicationFactor;
		target.issueCommand("./load.sh " + experimentName + " " + recordcount,
				5);
		this.finished = true;
	}

	@Override
	public void setDuration(long millis) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public INode getTarget() {
		return target;
	}

	@Override
	public boolean setTarget(INode target) {
		if (this.target == null) {
			this.target = target;
			return true;
		}
		return false;
	}

	@Override
	public long getRelativeStart() {
		return 0;
	}

	@Override
	public void setRelativeStart(long startTime) {

	}

	@Override
	public boolean dependsOnPrevious() {
		return true;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

}
