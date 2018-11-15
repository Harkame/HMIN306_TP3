package fr.harkame.astparser.example.structure;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TreeStructure
{
	public String className;
	
	public Map<String, Set<TreeNode>> declarationInvocations;
	
	public TreeStructure(String className)
	{
		this.className = className;
		
		declarationInvocations = new TreeMap<String, Set<TreeNode>>();
	}
}
