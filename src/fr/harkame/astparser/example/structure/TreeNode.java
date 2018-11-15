package fr.harkame.astparser.example.structure;

public class TreeNode implements Comparable<TreeNode>
{
	public String	className;
	public String	methodName;

	public TreeNode(String className, String methodName)
	{
		this.className = className;

		this.methodName = methodName;
	}

	@Override
	public int compareTo(TreeNode treeNodeToCompare)
	{
		return className.compareTo(treeNodeToCompare.className) + methodName.compareTo(treeNodeToCompare.methodName);
	}
}
