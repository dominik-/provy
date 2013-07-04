package cluster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public abstract class AbstractResourceManager implements IResourceManager {

	ArrayList<IResource> resources;

	public AbstractResourceManager() {
		resources = new ArrayList<>();
	}

	@Override
	public ArrayList<IResource> getResources() {
		return resources;
	}

	public String getLocalExternalIp() {
		URL ipcheck;
		String ip = "0.0.0.0";
		try {
			ipcheck = new URL("http://checkip.amazonaws.com/");
			BufferedReader in = new BufferedReader(new InputStreamReader(
					ipcheck.openStream()));
			ip = in.readLine();
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ip;
	}

	public String toString() {
		StringBuilder resourceDescriptions = new StringBuilder();
		resourceDescriptions.append("Currently managed resources:\n");
		for (IResource resource : resources) {
			resourceDescriptions.append(resource.toString() + "\n");
		}
		return resourceDescriptions.toString();
	}

}
