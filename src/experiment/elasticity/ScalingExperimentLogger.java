package experiment.elasticity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import experiment.IExperiment;
import experiment.IExperimentLogger;
import experiment.IExperimentPhase;

public class ScalingExperimentLogger implements IExperimentLogger {

	private IExperiment experiment;
	private final static SimpleDateFormat FILENAME_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");
	private final static SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss Z");
	public final static String DEFAULT_LOGGING_DIRECTORY = "logs";
	private File logfile;
	private FileWriter appendFileWriter;
	private PrintWriter writer;

	public ScalingExperimentLogger(IExperiment experiment) {
		this.experiment = experiment;
		logfile = new File(DEFAULT_LOGGING_DIRECTORY
				+ experiment.getName()
				+ FILENAME_DATE_FORMAT.format(new Date(System
						.currentTimeMillis())) + ".log");
		try {
			logfile.createNewFile();
			appendFileWriter = new FileWriter(logfile, true);
			writer = new PrintWriter(appendFileWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void log(IExperimentPhase phase) {
		long timeval = System.currentTimeMillis();
		writer.println("[START-PHASE] "+phase.getName()+" at "+ LOG_DATE_FORMAT.format(new Date(timeval)) + " ("+timeval+")");
	}

	@Override
	public void log(String command) {
		long timeval = System.currentTimeMillis();
		writer.println("[COMMAND] "+command+" at "+ LOG_DATE_FORMAT.format(new Date(timeval)) + " ("+timeval+")");
	}

}
