package test;

import experiment.elasticity.ScaleoutExperiment;

public class DefaultScalingExperiment {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScaleoutExperiment experiment = new ScaleoutExperiment("test-experiment-1","experiments/scaleout-experiment.properties");
		experiment.runExperiment();
	}
}
