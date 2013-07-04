package experiment.elasticity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import experiment.IExperiment;
import experiment.IExperimentPhase;
import experiment.IExperimentSetup;

public class ScalingExperiment implements IExperiment {

	private final static Logger logger = Logger
			.getLogger(ScalingExperiment.class.getSimpleName());
	private ScalingExperimentSetup setup;
	private ArrayList<IExperimentPhase> phases;
	boolean setupFinished;

	public ScalingExperiment() {
		setupFinished = setupExperiment();
		logger.log(Level.INFO, "Setup finished.");
	}

	@Override
	public ArrayList<IExperimentPhase> getPhases() {
		return phases;
	}

	@Override
	public boolean addPhase(IExperimentPhase phase) {
		return phases.add(phase);
	}

	@Override
	public void runExperiment() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		String input = "";
		int phaseCounter = 0;
		System.out
				.println("Entering Management CLI. To leave, type 'exit'. Available commands: status, phases, start-next, start-all");
		try {
			while (!input.equalsIgnoreCase("exit")) {
				input = reader.readLine();
				if (input.equalsIgnoreCase("status")) {
					System.out.println(setup.getNodeManager());
				} else if (input.equalsIgnoreCase("start-all")) {
					for (IExperimentPhase phase : phases) {
						Thread t = new Thread(phase);
						t.start();
					}
				} else if (input.equalsIgnoreCase("phases")) {
					System.out.println(getPhases().toString());

				} else if (input.equalsIgnoreCase("start-next")) {
					if (phaseCounter < getPhases().size()) {
						Thread t = new Thread(getPhases().get(phaseCounter++));
						t.start();
					}

				} else if (input.startsWith("scaleout")) {
					int number = Integer.parseInt(input.split(" ")[1]);
					try {
						setup.scaleOut(number);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (input.startsWith("scalein")) {
					int number = Integer.parseInt(input.split(" ")[1]);
					setup.scaleIn(number);
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void storeResults() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setupExperiment() {
		setup = new ScalingExperimentSetup();
		return setup.createSetup();
	}

	@Override
	public IExperimentSetup getExperimentSetup() {
		return setup;
	}

}
