import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Main
{
	final static int PERCENT = 10;
	
	final static int X = 2;
	
	static int classCounter = 0;
	static int lineCounter = 0;
	static int methodCounter = 0;
	static int packageCounter = 0;
	
	static int methodAverage = 0;
	static int codeLineMethodAverage = 0;
	static int attributeAverage = 0;
	
	static Map<Integer, String> classWithManyMethods = new TreeMap<Integer, String>();
	static Map<Integer, String> classWithManyAttributes = new TreeMap<Integer, String>();

	static Collection<String> classWithManyMethodsAndAttributes = new ArrayList<String>();
	
	static Collection<String> classWithMoreThanXMethods = new ArrayList<String>();
	static Collection<String> classWithLargestCode = new ArrayList<String>();
	
	static int maximumMethodParameter = 0;
	
	//Temp
	
	static int attributeCounter = 0;
	
	static int methodLineCounter = 0;
	
	public static String fileToString(String filePath) throws IOException
	{
		StringBuilder fileCode = new StringBuilder(1000);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

		char[] buffer = new char[10];
		int numRead = 0;
		while((numRead = bufferedReader.read(buffer)) != -1)
		{
			String readData = String.valueOf(buffer, 0, numRead);
			fileCode.append(readData);
			buffer = new char[1024];
		}

		bufferedReader.close();

		return fileCode.toString();
	}

	public static String directoryToString(String directoryPath) throws IOException
	{
		File root = new File(directoryPath);

		StringBuilder projectCode = new StringBuilder();

		for(File file : root.listFiles())
			if(file.isDirectory())
				projectCode.append(directoryToString(file.getAbsolutePath()));
			else
				projectCode.append(fileToString(file.getAbsolutePath()));
		
		return projectCode.toString();
	}
	
	public static void main(String args[]) throws IOException
	{
		ASTParser astParser = ASTParser.newParser(AST.JLS3);


		astParser.setSource(directoryToString("D:\\workspace\\JapscanDownloader\\src").toCharArray());

		astParser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);

		compilationUnit.accept(new ASTVisitor()
		{
			public boolean visit(TypeDeclaration node)
			{
				SimpleName className = node.getName();

				System.out.println("Class :  " + className.toString() + " - " + node.modifiers());
				
				int localLineCounter = node.toString().length() - node.toString().replace(System.getProperty("line.separator"), "").length();
				
				if(localLineCounter == 0)
					localLineCounter += node.toString().length() - node.toString().replace("\n", "").length();

				lineCounter += localLineCounter;
				
				
				classCounter++;
				
				System.out.println("Superclass : ");
				for(Object object : node.superInterfaceTypes())
					System.out.println(object);

				System.out.println("---");

				for(FieldDeclaration fieldDeclaration : node.getFields())
					System.out.println(
						fieldDeclaration.fragments().get(0) + " - " + fieldDeclaration.modifiers().toString());
				
				attributeCounter += node.getFields().length;
				
				classWithManyAttributes.put(node.getFields().length, className.toString());

				System.out.println("---");
				
				for(MethodDeclaration methodDeclaration : node.getMethods())
				{
					System.out.println(methodDeclaration.getName() + " - " + methodDeclaration.getReturnType2()
						+ " - " + methodDeclaration.parameters());
					
					if(methodDeclaration.parameters().size() > maximumMethodParameter)
						maximumMethodParameter = methodDeclaration.parameters().size();
					
					localLineCounter = methodDeclaration.getBody().toString().length() - methodDeclaration.getBody().toString().replace(System.getProperty("line.separator"), "").length();
					
					if(localLineCounter == 0)
						localLineCounter += methodDeclaration.getBody().toString().length() - methodDeclaration.getBody().toString().replace("\n", "").length();

					lineCounter += localLineCounter;
				}
				
				if(node.getMethods().length > X)
					classWithMoreThanXMethods.add(className.toString());
				
				classWithManyMethods.put(node.getMethods().length, className.toString());
				
				methodCounter += node.getMethods().length;

				return false;
			}
		});
		
		System.out.println(System.getProperty("line.separator"));
		System.out.println("--- Result --- ");
		System.out.println(System.getProperty("line.separator"));
		
		System.out.println("classCounter : " + classCounter);
		System.out.println("lineCounter : " + lineCounter);
		System.out.println("methodCounter : " + methodCounter);
		System.out.println("packageCounter : " + packageCounter);
		
		System.out.println("codeLineMethodAverage : " + methodLineCounter / methodCounter); //codeLineMethodAverage
		System.out.println("attributeAverage : " + attributeCounter / classCounter); //attributeAverage
		
		System.out.println("classWithManyMethods : " + classWithManyMethods.toString());
		System.out.println("classWithManyAttributes : " + classWithManyAttributes.toString());
		
		Collection<String> inner = new ArrayList<String>();
		
		for (Map.Entry<Integer, String> methodEntry : classWithManyMethods.entrySet())
			for (Map.Entry<Integer, String> attributeEntry : classWithManyAttributes.entrySet())
				if(methodEntry.getValue().equals(attributeEntry.getValue()))
					inner.add(methodEntry.getValue());
		
		System.out.println("inner : " + inner.toString());
					
		System.out.println("classWithManyMethodsAndAttributes : " + classWithManyMethodsAndAttributes.toString());
		
		System.out.println("classWithMoreThanXMethods : " + classWithMoreThanXMethods.toString());
		System.out.println("classWithLargestCode : " + classWithLargestCode.toString());
		
		System.out.println("maximumMethodParameter : " + maximumMethodParameter);
	}
}