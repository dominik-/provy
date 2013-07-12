package experiment;

import java.util.ArrayList;

/**
 * The main class executing an experiment.
 * @author Dominik
 *
 */
public interface IExperiment {
	
	public ArrayList<IExperimentPhase> getPhases();
	
	public boolean addPhase(IExperimentPhase phase);
	
	public boolean setupExperiment();
	
	public IExperimentSetup getExperimentSetup();
	
	public void runExperiment();
	
	public String getName();
	
}
