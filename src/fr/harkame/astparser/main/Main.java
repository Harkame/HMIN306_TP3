package fr.harkame.astparser.main;

import java.io.IOException;
import java.util.Map;

import javax.swing.JFrame;

import fr.harkame.astparser.example.ASTParserExample;
import fr.harkame.astparser.example.structure.TreeStructure;
import fr.harkame.astparser.util.GraphAST;

public class Main
{
	private final static String PROJECT_SOURCE_FOLDER = "D:\\workspace\\JapScanDownloader\\src";

	private final static int PERCENT = 20;

	private final static int X = 2;

	public static void main(String[] args) throws IOException
	{
		ASTParserExample astParserExample = new ASTParserExample(PROJECT_SOURCE_FOLDER);

		astParserExample.initialize();

		System.out.println(System.getProperty("line.separator"));
		System.out.println("--- Result --- ");
		System.out.println(System.getProperty("line.separator"));

		System.out.println("classCounter : " + astParserExample.getClassCounter());
		System.out.println("lineCounter : " + astParserExample.getLineCounter());
		System.out.println("methodCounter : " + astParserExample.getMethodCounter());
		System.out.println("packageCounter : " + astParserExample.getPackageCounter());

		System.out.println("methodAverage : " + astParserExample.getMethodsAverage());
		System.out.println("codeLineMethodAverage : " + astParserExample.getCodeLineMethodAverage());
		System.out.println("attributeAverage : " + astParserExample.getAttributeAverage());

		System.out.println(PERCENT + "% of class with many Methods : "
			+ astParserExample.getPercentClassWithManyMethods().toString());
		System.out.println(PERCENT + "% of class with many Attributes : "
			+ astParserExample.getPercentClassWithManyAttributes().toString());

		System.out.println("classWithManyMethodsAndAttributes : "
			+ astParserExample.getClassWithManyMethodsAndAttributes().toString());

		System.out.println("class With More Than " + X + " Methods : "
			+ astParserExample.getClassWithMoreThanXMethods().toString());
		System.out.println(PERCENT + "% of Methods with largest code (by number of line) : "
			+ astParserExample.getPercentMethodsWithLargestCode().toString());

		System.out.println("maximumMethodParameter : " + astParserExample.getMaximumMethodParameter());

		String className = "Main";

		for(Map.Entry<String, TreeStructure> entry : astParserExample.getTreeStructures().entrySet())
			if(entry.getKey().equals(className))
			{
				GraphAST frame = new GraphAST(entry.getValue());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 740);
				frame.setVisible(true);
			}
	}
}