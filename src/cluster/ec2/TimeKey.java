package cluster.ec2;

public enum TimeKey {
	REQUEST_TIME("requestTime"), CONFIRMATION_TIME("confirmedTime"), BOOTUP_TIME(
			"bootedTime"), SSH_TIME("sshableTime"), CONFIGURATION_TIME(
			"configuredTime"), UPDATE_TIME("updateTime"), STOP_TIME("gracefulStopTime"), REQUEST_RELEASE_TIME("requestReleaseTime");
	private final String name;

	TimeKey(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
