package experiment;

import cluster.INode;

/**
 * Description of a single phase within an experiment. Each phase
 * has a name, a duration and a starting point.
 * Also, each phase is linked to a particular node.
 * @author Dominik
 *
 */
public interface IExperimentPhase extends Runnable{

	public void setDuration(long millis);
	
	public long getDuration();
	
	public String getName();
	
	public void setName(String name);
	
	public INode getTarget();
	
	public boolean setTarget(INode target);
	
	public long getRelativeStart();
	
	public void setRelativeStart(long startTime);
	
	public boolean dependsOnPrevious();
	
}
