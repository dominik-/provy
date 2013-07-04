package experiment;

import java.util.ArrayList;

import cluster.INodeManager;
import cluster.INodeType;
import cluster.IResourceManager;
import cluster.IResourceType;

public interface IExperimentSetup {
	
	public ArrayList<IResourceType> getResourceTypes();
	
	public ArrayList<INodeType> getNodeTypes();
	
	public INodeManager getNodeManager();
	
	public IResourceManager getResourceManager();
	
	public boolean createSetup();
	
	public boolean scaleOut(int number) throws InterruptedException;
	
	public boolean scaleIn(int number) throws InterruptedException;
	
}
