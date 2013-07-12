package experiment;

public interface IExperimentLogger {
	
	public void log(IExperimentPhase phase);
	
	public void log(String command);
	
}
