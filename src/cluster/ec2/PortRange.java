package cluster.ec2;

public class PortRange {
	private int lower;
	private int upper;
	private String protocol;

	public PortRange(int lower, int upper) {
		this(lower,upper,"tcp");
	}
	
	public PortRange(int lower, int upper, String protocol) {
		this.lower = lower;
		this.upper = upper;
		this.setProtocol(protocol);
	}

	public int getLower() {
		return lower;
	}

	public void setLower(int lower) {
		this.lower = lower;
	}

	public int getUpper() {
		return upper;
	}

	public void setUpper(int upper) {
		this.upper = upper;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
}
