package fr.harkame.astparser.structure.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ClassTree
{
	public Map<String, MethodTree> classTree = new TreeMap<String, MethodTree>();

	public void addClassDeclaration(String classDeclaration)
	{
		if(classTree.get(classDeclaration) == null)
			classTree.put(classDeclaration, new MethodTree());	
	}
	
	public void addMethodDeclaration(String classDeclaration, String methodDeclaration)
	{
		if(classTree.get(classDeclaration) == null)
			classTree.put(classDeclaration, new MethodTree());
		
		if(classTree.get(classDeclaration).methodTree.get(methodDeclaration) == null)
			classTree.get(classDeclaration).methodTree.put(methodDeclaration, new InvocationTree());
	}
	
	public void addMethodInvocation(String classDeclaration, String methodDeclaration, String classOfMethodInvocation, String methodInvocation)
	{
		if(classTree.get(classDeclaration) == null)
			classTree.put(classDeclaration, new MethodTree());
		
		if(classTree.get(classDeclaration).methodTree.get(methodDeclaration) == null)
			classTree.get(classDeclaration).methodTree.put(methodDeclaration, new InvocationTree());
		
		if(classTree.get(classDeclaration).methodTree.get(methodDeclaration).invocationTree.get(classOfMethodInvocation) == null)
			classTree.get(classDeclaration).methodTree.get(methodDeclaration).invocationTree.put(classOfMethodInvocation, new ArrayList<String>());
		
		classTree.get(classDeclaration).methodTree.get(methodDeclaration).invocationTree.get(classOfMethodInvocation).add(methodInvocation);
	}
	
	@Override
	public String toString()
	{
		StringBuilder toString = new StringBuilder();

		for(Entry<String, MethodTree> classEntry : classTree.entrySet())
		{
			toString.append(classEntry.getKey());
			toString.append(System.getProperty("line.separator"));
			
			for(Entry<String, InvocationTree> methodEntry : classEntry.getValue().methodTree.entrySet())
			{
				toString.append("\t" + methodEntry.getKey());
				toString.append(System.getProperty("line.separator"));
				
				for(Entry<String, Collection<String>> invocationEntry : methodEntry.getValue().invocationTree.entrySet())
				{
					toString.append("\t\t" + invocationEntry.getKey() + " (" + invocationEntry.getValue().size() + ")");
					toString.append(System.getProperty("line.separator"));
					
					for(String methodInvocation : invocationEntry.getValue())
					{
						toString.append("\t\t\t" + methodInvocation);
						toString.append(System.getProperty("line.separator"));
					}
				}
			}
		}
		
		return toString.toString();
	}
}
