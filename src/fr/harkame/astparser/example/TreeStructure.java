package fr.harkame.astparser.example;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TreeStructure
{
	public String className;
	
	public Map<String, Set<String>> declarationInvocations;
	
	public TreeStructure(String className)
	{
		this.className = className;
		
		declarationInvocations = new TreeMap<String, Set<String>>();
	}
}
