package experiment.elasticity;

import cluster.INode;
import experiment.IExperimentPhase;

public class RunWorkloadPhase implements IExperimentPhase {

	private INode target;
	private String name;
	private String experimentName;
	private int throughput;
	private String workloadFileName;
	private int threads;
	private long runtime;
	boolean finished;
	private long duration;

	public RunWorkloadPhase(String name, String experimentName, int throughput,
			int threads, long runtime, INode target, String workloadFileName) {
		this.name = name;
		this.experimentName = experimentName;
		this.target = target;
		this.throughput = throughput;
		this.threads = threads;
		this.workloadFileName = workloadFileName;
		this.runtime = runtime;
		setDuration(runtime*1000);
		this.finished = true;
	}

	@Override
	public void run() {
		target.issueCommand("./latency-timeseries.sh " + experimentName + " "
				+ name + " " + workloadFileName + " " + runtime + " "
				+ throughput + " " + threads, 1);
	}

	@Override
	public void setDuration(long millis) {
		this.duration = millis;
	}

	@Override
	public long getDuration() {
		return duration;
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
