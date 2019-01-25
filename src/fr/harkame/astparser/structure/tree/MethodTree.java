package fr.harkame.astparser.structure.tree;

import java.util.Map;
import java.util.TreeMap;

public class MethodTree
{
	public Map<String, InvocationTree> methodTree = new TreeMap<String, InvocationTree>();
}
