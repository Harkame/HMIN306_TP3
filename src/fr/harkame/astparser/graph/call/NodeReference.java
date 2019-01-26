package fr.harkame.astparser.graph.call;

public class NodeReference
{
	public String className;
	public String methodName;
	
	public NodeReference(String className, String methodName)
	{
		this.className = className;
		this.methodName = methodName;
	}
}