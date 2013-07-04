package configuration;

import cluster.IResourceType;

public interface IResourceTypeParser<T extends IResourceType> {
	
	public T readResourceType(String fileName);
	
	public boolean writeResourceType(T nodeType, String fileName);
}
