package test;

import experiment.elasticity.ScalingExperiment;

public class DefaultScalingExperiment {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ScalingExperiment experiment = new ScalingExperiment("simple-experiment");
		experiment.runExperiment();
	}

}
