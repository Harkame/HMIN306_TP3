package fr.harkame.astparser.graphe.call;

import java.util.ArrayList;
import java.util.Collection;

public class NodeContent
{
	public String className;
	public String methodName;

	public Object methodFigure;
	
	public Collection<NodeReference> nodeReferences = new ArrayList<NodeReference>();
	
	public NodeContent(String className, String methodName, Object methodFigure)
	{
		this.className = className;
		this.methodName = methodName;
		
		this.methodFigure = methodFigure;
	}
}
