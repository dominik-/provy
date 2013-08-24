package experiment.elasticity;

import cluster.INode;
import experiment.IExperimentPhase;

public class WarmupPhase implements IExperimentPhase {

	private long duration;
	private long relativeStart;
	private String name;
	private INode target;
	private boolean finished;
	private String experimentName;
	private String workloadFileName;
	private int threads;

	public WarmupPhase(String name, String experimentName, INode target,
			long duration, String workloadFileName, int threads) {
		this.name = name;
		this.relativeStart = 0;
		this.target = target;
		this.duration = duration;
		this.finished = false;
		this.experimentName = experimentName;
		this.workloadFileName = workloadFileName;
		this.threads = threads;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(relativeStart);
			target.issueCommand("./max-throughput.sh " + experimentName + " "
					+ workloadFileName + " " + threads + " " + duration, 1);
			Thread.sleep((duration*1000)+10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.finished = true;
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
		return null;
	}

	@Override
	public boolean setTarget(INode target) {
		return false;
	}

	@Override
	public long getRelativeStart() {
		return relativeStart;
	}

	@Override
	public void setRelativeStart(long startTime) {
		this.relativeStart = startTime;
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
