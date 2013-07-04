package configuration;

import cluster.INodeType;

public interface INodeTypeParser<T extends INodeType> {
	
	public T readNodeType(String fileName);
	
	public boolean writeNodeType(T nodeType, String fileName);
	
}
