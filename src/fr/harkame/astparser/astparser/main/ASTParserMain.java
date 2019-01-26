package fr.harkame.astparser.astparser.main;

import java.io.IOException;

import javax.swing.JFrame;

import fr.harkame.astparser.astparser.example.ASTParserExample;
import fr.harkame.astparser.astparser.example.visitor.CustomASTVisitor;
import fr.harkame.astparser.graph.call.CallGraph;

public class ASTParserMain
{
	private final static String	WINDOWS_PROJECT_SELF_PROJECT	= ".\\src";
	private final static String	WINDOWS_ENVIRONMENT_CLASS_PATH	= "..";
	private final static String	WINDOWS_ENVIRONMENT_SOURCES	= "..";

	private final static String	LINUX_SELF_PROJECT	= "./src";
	private final static String	LINUX_ENVIRONMENT_CLASS_PATH	= "..";
	private final static String	LINUX_ENVIRONMENT_SOURCES	= "..";

	private final static int PERCENT = 20;

	private final static int X = 2;

	public static void main(String[] args) throws IOException
	{
		ASTParserExample astParserExample = new ASTParserExample(WINDOWS_PROJECT_SELF_PROJECT,
			WINDOWS_ENVIRONMENT_CLASS_PATH, WINDOWS_ENVIRONMENT_SOURCES);

		astParserExample.initialize();
		
		showMetrics();
		
		showCallGraph();
	}
	
	public static void showMetrics()
	{
		System.out.println("classCounter : " + CustomASTVisitor.classCounter);
		System.out.println("lineCounter : " + CustomASTVisitor.lineCounter);
		System.out.println("methodCounter : " + CustomASTVisitor.methodCounter);
		System.out.println("packageCounter : " + CustomASTVisitor.packageCounter);
		System.out.println("methodAverage : " + CustomASTVisitor.getMethodsAverage());
		System.out.println("codeLineMethodAverage : " + CustomASTVisitor.getCodeLineMethodAverage());
		System.out.println("attributeAverage : " + CustomASTVisitor.getAttributeAverage());

		System.out.println(PERCENT + "% of class with many Methods : "
			+ CustomASTVisitor.percentClassWithManyMethods.toString());
		System.out.println(PERCENT + "% of class with many Attributes : "
			+ CustomASTVisitor.percentClassWithManyAttributes.toString());

		System.out.println("classWithManyMethodsAndAttributes : "
			+ CustomASTVisitor.classWithManyMethodsAndAttributes.toString());

		System.out.println("class With More Than " + X + " Methods : "
			+ CustomASTVisitor.classWithMoreThanXMethods.toString());
		System.out.println(PERCENT + "% of Methods with largest code (by number of line) : "
			+ CustomASTVisitor.methodsWithLargestCode.toString());

		System.out.println("maximumMethodParameter : " + CustomASTVisitor.maximumMethodParameter + " : " + CustomASTVisitor.maximumMethodParameterName);
	}
	
	public static void showCallGraph()
	{
		CallGraph graph = new CallGraph("Spoon", CustomASTVisitor.classTree);
		graph.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		graph.setSize(800, 740);
		graph.setVisible(true);
	}
}