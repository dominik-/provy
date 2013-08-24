package experiment.elasticity;

import cluster.INode;
import experiment.IExperimentPhase;

public class SimplePhase implements IExperimentPhase {

	private long duration;
	private long relativeStart;
	private INode target;
	private String name;
	private String command;
	private boolean finished;
	
	public SimplePhase(String name, INode target, long duration, String command){
		this.name = name;
		this.target = target;
		this.duration = duration;
		this.command = command;
		this.relativeStart = 0;
		this.finished=false;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(relativeStart);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		target.issueCommand(command, 5);
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
		return target;
	}

	@Override
	public boolean setTarget(INode target) {
		this.target = target;
		return true;
	}

	@Override
	public long getRelativeStart() {
		return relativeStart;
	}

	@Override
	public void setRelativeStart(long startTime) {
		this.relativeStart= startTime;
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
