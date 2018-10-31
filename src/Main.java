import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JFrame;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class Main
{
	private final static int PERCENT = 20;
	private final static String PATH = "D:\\workspace\\JapscanDownloader\\src";
	
	private final static int X = 2;
	
	private static int classCounter = 0;
	private static int lineCounter = 0;
	private static int methodCounter = 0;
	private static int packageCounter = 0;
	
	private static int methodAverage = 0;
	private static int codeLineMethodAverage = 0;
	private static int attributeAverage = 0;
	
	private static List<String> percentClassWithManyMethods = new ArrayList<String>();
	private static List<String> percentClassWithManyAttributes = new ArrayList<String>();

	private static Collection<String> classWithManyMethodsAndAttributes = new ArrayList<String>();
	
	private static Collection<String> classWithMoreThanXMethods = new ArrayList<String>();
	private static Collection<String> percentMethodsWithLargestCode = new ArrayList<String>();
	
	private static int maximumMethodParameter = 0;
	
	//Temp
	
	private static Map<String, Collection<String>> classMethods = new TreeMap<String, Collection<String>>();
	
	private static TreeSet<SetType> classWithManyMethods = new TreeSet<SetType>();
	private static TreeSet<SetType> classWithManyAttributes = new TreeSet<SetType>();
	private static TreeSet<SetType> methodsWithLargestCode = new TreeSet<SetType>();
	
	private static int attributeCounter = 0;
	private static TreeSet<String> packages = new TreeSet<String>();
	private static int methodLineCounter = 0;
			
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

	public static void directoryToString(String directoryPath) throws IOException
	{
		File root = new File(directoryPath);

		for(File file : root.listFiles())
			if(file.isDirectory())
				directoryToString(file.getAbsolutePath());
			else
				parse(fileToString(file.getAbsolutePath()));
		
	}
	
	public static void parse(String code) throws IOException
	{
		ASTParser astParser = ASTParser.newParser(AST.JLS3);


		astParser.setSource(code.toCharArray());

		astParser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
		compilationUnit.accept(new ASTVisitor()
		{
		
			public boolean visit (PackageDeclaration node){
				packages.add(node.getName().toString());
				packageCounter++;	
				return true;
			}
			
			public boolean visit (TypeDeclaration node)
			{
				SimpleName className = node.getName();
				
				classMethods.put(className.toString(), new ArrayList<String>());

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
				
				classWithManyAttributes.add(new  SetType(className.toString(), node.getFields().length));

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

					methodLineCounter += localLineCounter;
					
					classMethods.get(className.toString()).add(methodDeclaration.getName().toString());
					
					methodsWithLargestCode.add(new SetType((methodDeclaration.getName() + " - " + methodDeclaration.getReturnType2()+ " - " + methodDeclaration.parameters())
							, localLineCounter
							, methodDeclaration.getName().toString()));						
				}
				
				if(node.getMethods().length > X)
					classWithMoreThanXMethods.add(className.toString());
				
				classWithManyMethods.add(new SetType(className.toString(), node.getMethods().length));
				
				methodCounter += node.getMethods().length;

				return true;
			}
			
		});		
	}

	public static void percentOfClassWithManyMethods(){
		int numberToSelect = (classCounter * PERCENT) / 100;	
		int cpt = 0;
		
		for(SetType it : classWithManyMethods){
			if(cpt != numberToSelect){
				percentClassWithManyMethods.add(it.toString());	
				cpt++;
			}
			else{
				return;
			}
		}
	}
	
	public static void percentOfClassWithManyAttributs(){
		int numberToSelect = (classCounter * PERCENT) / 100;	
		int cpt = 0;
		
		for(SetType it : classWithManyAttributes){
			if(cpt != numberToSelect){
				percentClassWithManyAttributes.add(it.toString());	
				cpt++;
			}
			else{
				return;
			}
		}
	}
	
	public static void percentOfMethodsWithLargestCode(){
		int numberToSelect = (methodCounter * PERCENT) / 100;	
		int cpt = 0;
		
		for(SetType it : methodsWithLargestCode){
			if(cpt != numberToSelect){
				percentMethodsWithLargestCode.add(it.toString());	
				cpt++;
			}
			else{
				return;
			}
		}
	}
	
	public static void mergeBetweenClassWithManyAttributesAndMethods(){
		for(String cMethods: percentClassWithManyMethods){
			for(String cAttributes: percentClassWithManyAttributes){
				if(cMethods.equals(cAttributes)){
					classWithManyMethodsAndAttributes.add(cMethods.toString());
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		directoryToString(PATH);
		percentOfClassWithManyMethods();
		percentOfClassWithManyAttributs();
		percentOfMethodsWithLargestCode();
		mergeBetweenClassWithManyAttributesAndMethods();
		
		methodAverage = methodCounter / classCounter;
		codeLineMethodAverage = methodLineCounter / methodCounter;
		attributeAverage = attributeCounter / classCounter;
		
		System.out.println(System.getProperty("line.separator"));
		System.out.println("--- Result --- ");
		System.out.println(System.getProperty("line.separator"));
		
		System.out.println("classCounter : " + classCounter);
		System.out.println("lineCounter : " + lineCounter);
		System.out.println("methodCounter : " + methodCounter);
		System.out.println("packageCounter : " + packageCounter);
		
		System.out.println("methodAverage : " + methodAverage);
		System.out.println("codeLineMethodAverage : " + codeLineMethodAverage);
		System.out.println("attributeAverage : " + attributeAverage);
	
		System.out.println(PERCENT + "% of class with many Methods : " + percentClassWithManyMethods.toString());
		System.out.println(PERCENT + "% of class with many Attributes : " + percentClassWithManyAttributes.toString());
		
		System.out.println("classWithManyMethodsAndAttributes : " + classWithManyMethodsAndAttributes.toString());
		
		System.out.println("class With More Than " + X + " Methods : " + classWithMoreThanXMethods.toString());
		System.out.println(PERCENT + "% of Methods with largest code (by number of line) : " + percentMethodsWithLargestCode.toString());
		
		System.out.println("maximumMethodParameter : " + maximumMethodParameter);
		
		String className = "MyString";
		
		for(Map.Entry<String, Collection<String>> entry : classMethods.entrySet())
		{
			if(entry.getKey().equals(className))
			{
				MethodsList frame = new MethodsList(className, entry.getValue());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(800, 740);
				frame.setVisible(true);
			}
		}
	}
}