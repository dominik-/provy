package ssh;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

/**
 * Implementation of a SSH connection to a machine via an IP address (or resolvable DNS).
 * @author Dominik
 *
 */
public class SSHConnector {
	private static final Logger logger = Logger.getLogger(SSHConnector.class
			.getSimpleName());
	private File privateKeyFile;
	int retryCount;
	private String address;
	private long firstConnection;

	public SSHConnector(String address, String privateKeyFilePath) {
		retryCount = 0;
		this.address = address;
		this.privateKeyFile = new File(privateKeyFilePath);
		
		try {
			if (!privateKeyFile.exists()) {
				throw new IOException("Private Key not found");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		final SSHClient client = new SSHClient();
		try {
			// TODO automate adding public key from location; known_hosts file
			// or KeyVerifier
			// client.loadKnownHosts(getKnownHostsSetup());
			client.addHostKeyVerifier(new PromiscuousVerifier());
			// try to connect once for ssh-able time measurement
			ensureConnected(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void ensureConnected(SSHClient client) throws IOException {

		try {
			Thread.sleep(250);
			client.connect(address);
			firstConnection = System.currentTimeMillis();
			logger.log(Level.INFO, "Connection successfully established.");
		} catch (ConnectException ce) {
			retryCount++;
			if (retryCount == 8) {
				retryCount = 0;
				logger.log(Level.INFO, "Connection refused, retrying...");
			}
			ensureConnected(client);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			client.disconnect();
		}

	}

	public String issueSSHCommand(String command, String user, int timeout) {
		String response = "";
		if (command.isEmpty()){
			return "";
		}
		try {
			final SSHClient client = new SSHClient();
			client.addHostKeyVerifier(new PromiscuousVerifier());
			client.connect(address);
			KeyProvider keypair = client.loadKeys(privateKeyFile.toString());
			client.authPublickey(user, keypair);
			final Session session = client.startSession();
			try {
				final Command cmd = session.exec(command);
				System.out.println(IOUtils.readFully(cmd.getInputStream())
						.toString());
				cmd.join(timeout, TimeUnit.SECONDS);
				response = cmd.getExitErrorMessage();
				// cmd.getOutputStream();
				// System.out.println("\n** exit status: " +
				// cmd.getExitStatus());
			} finally {
				session.close();
				client.disconnect();
				client.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	public long getFirstConnectionTime() {
		return firstConnection;
	}

}
