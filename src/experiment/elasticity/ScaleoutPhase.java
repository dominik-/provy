package experiment.elasticity;

import cluster.INode;
import experiment.IExperimentPhase;
import experiment.IExperimentSetup;

public class ScaleoutPhase implements IExperimentPhase {

	private long duration;
	private long relativeStart;
	private IExperimentSetup setup;
	private String name;
	private int number;
	private boolean finished;
	
	public ScaleoutPhase(String name, IExperimentSetup setup, int number){
		this.name = name;
		this.setup = setup;
		this.number = number;
		this.relativeStart = 0;
		this.finished=false;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(relativeStart);
			setup.scaleOut(number);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.finished=true;
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
		this.relativeStart= startTime;
	}

	@Override
	public boolean dependsOnPrevious() {
		return false;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

}
