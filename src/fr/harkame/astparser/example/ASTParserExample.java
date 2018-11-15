package fr.harkame.astparser.example;

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

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import fr.harkame.astparser.example.structure.SetType;
import fr.harkame.astparser.example.structure.TreeNode;
import fr.harkame.astparser.example.structure.TreeStructure;

public class ASTParserExample
{
	private final static int PERCENT = 20;

	private final static int X = 2;

	// Attributes

	private ASTParser			astParser;
	private Collection<String>	sourceFiles;
	private String				sourcePath;

	// DATA

	private int	classCounter	= 0;
	private int	lineCounter	= 0;
	private int	methodCounter	= 0;
	private int	packageCounter	= 0;

	private List<String>	percentClassWithManyMethods		= new ArrayList<String>();
	private List<String>	percentClassWithManyAttributes	= new ArrayList<String>();

	private Collection<String> classWithManyMethodsAndAttributes = new ArrayList<String>();

	private Collection<String>	classWithMoreThanXMethods	= new ArrayList<String>();
	private Collection<String>	percentMethodsWithLargestCode	= new ArrayList<String>();

	private int maximumMethodParameter = 0;

	private Map<String, Collection<String>>	classMethods	= new TreeMap<String, Collection<String>>();
	private Map<String, Collection<String>>	methodMethods	= new TreeMap<String, Collection<String>>();

	public Map<String, TreeStructure> treeStructures = new TreeMap<String, TreeStructure>();

	private TreeSet<SetType>	classWithManyMethods	= new TreeSet<SetType>();
	private TreeSet<SetType>	classWithManyAttributes	= new TreeSet<SetType>();
	private TreeSet<SetType>	methodsWithLargestCode	= new TreeSet<SetType>();

	private int			attributeCounter	= 0;
	private TreeSet<String>	packages			= new TreeSet<String>();
	private int			methodLineCounter	= 0;

	public ASTParserExample(String sourcePath)
	{
		astParser = ASTParser.newParser(AST.JLS9);

		astParser.setResolveBindings(true);
		astParser.setBindingsRecovery(true);

		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);

		astParser.setCompilerOptions(options);
		astParser.setStatementsRecovery(true);
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);

		sourceFiles = new ArrayList<String>();

		this.sourcePath = sourcePath;
	}

	public void initialize() throws IOException
	{
		exploreProject(sourcePath);
		parseProject();

		percentOfClassWithManyMethods();
		percentOfClassWithManyAttributs();
		percentOfMethodsWithLargestCode();
		mergeBetweenClassWithManyAttributesAndMethods();
	}

	public void exploreProject(String directory) throws IOException
	{
		File root = new File(directory);

		for(File file : root.listFiles())
			if(file.isDirectory())
				exploreProject(file.getAbsolutePath());
			else
				sourceFiles.add(file.getAbsolutePath());

	}

	private char[] fileToString(String filePath) throws IOException
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

		return fileCode.toString().toCharArray();
	}

	public void parseProject() throws IOException
	{
		for(String sourceFile : sourceFiles)
			parseFile(sourceFile);
	}

	private void parseFile(String sourceFile) throws IOException
	{
		astParser.setSource(fileToString(sourceFile));
		// parse(fileToString(file.getAbsolutePath()));
		CompilationUnit compilationUnit = (CompilationUnit) astParser.createAST(null);
		compilationUnit.accept(new ASTVisitor()
		{
			public boolean visit(PackageDeclaration node)
			{
				packages.add(node.getName().toString());
				packageCounter++;
				return true;
			}

			public boolean visit(TypeDeclaration node)
			{
				SimpleName className = node.getName();

				if(treeStructures.get(node.getName().toString()) == null)
					treeStructures.put(node.getName().toString(), new TreeStructure(node.getName().toString()));

				classMethods.put(className.toString(), new ArrayList<String>());

				int localLineCounter = node.toString().length()
					- node.toString().replace(System.getProperty("line.separator"), "").length();

				if(localLineCounter == 0)
					localLineCounter += node.toString().length() - node.toString().replace("\n", "").length();

				lineCounter += localLineCounter;
				classCounter++;

				/*
				 * for(Object object : node.superInterfaceTypes())
				 * System.out.println(object);
				 */

				/*
				 * for(FieldDeclaration fieldDeclaration : node.getFields())
				 * System.out.println( fieldDeclaration.fragments().get(0) +
				 * " - " + fieldDeclaration.modifiers().toString());
				 */
				attributeCounter += node.getFields().length;

				classWithManyAttributes.add(new SetType(className.toString(), node.getFields().length));

				for(MethodDeclaration methodDeclaration : node.getMethods())
				{
					/*
					 * System.out.println(methodDeclaration.getName() +
					 * " - " + methodDeclaration.getReturnType2() + " - " +
					 * methodDeclaration.parameters());
					 */
					if(methodDeclaration.parameters().size() > maximumMethodParameter)
						maximumMethodParameter = methodDeclaration.parameters().size();

					localLineCounter = methodDeclaration.getBody().toString().length() - methodDeclaration
						.getBody().toString().replace(System.getProperty("line.separator"), "").length();

					if(localLineCounter == 0)
						localLineCounter += methodDeclaration.getBody().toString().length()
							- methodDeclaration.getBody().toString().replace("\n", "").length();

					methodLineCounter += localLineCounter;

					classMethods.get(className.toString()).add(methodDeclaration.getName().toString());

					if(treeStructures.get(node.getName().toString()).declarationInvocations
						.get(methodDeclaration.getName().toString()) == null)
						treeStructures.get(node.getName().toString()).declarationInvocations
							.put(methodDeclaration.getName().toString(), new TreeSet<TreeNode>());

					methodsWithLargestCode.add(new SetType(
						(methodDeclaration.getName() + " - " + methodDeclaration.getReturnType2() + " - "
							+ methodDeclaration.parameters()),
						localLineCounter, methodDeclaration.getName().toString()));
				}

				if(node.getMethods().length > X)
					classWithMoreThanXMethods.add(className.toString());

				classWithManyMethods.add(new SetType(className.toString(), node.getMethods().length));

				methodCounter += node.getMethods().length;

				return true;
			}

			public boolean visit(MethodInvocation methodInvocation)
			{

				try
				{
					ASTNode parent = methodInvocation.getParent();

					if(parent == null)
						return true;

					while(parent.getNodeType() != 31)
					{
						parent = parent.getParent();

						if(parent == null)
							return true;
					}

					MethodDeclaration methodDeclaration = (MethodDeclaration) parent;

					parent = methodInvocation.getParent();

					if(parent == null)
						return true;

					while(parent.getNodeType() != 55)
						parent = parent.getParent();

					TypeDeclaration typeDeclaration = (TypeDeclaration) parent;

					if(treeStructures.get(typeDeclaration.getName().toString()).declarationInvocations
						.get(methodDeclaration.getName().toString()) == null)
						treeStructures.get(typeDeclaration.getName().toString()).declarationInvocations
							.put(methodDeclaration.getName().toString(), new TreeSet<TreeNode>());

					// System.out.println("METHODINVOCATION : " +
					// methodInvocation.getName().toString());

					treeStructures.get(typeDeclaration.getName().toString()).declarationInvocations
						.get(methodDeclaration.getName().toString())
						.add(new TreeNode("", methodInvocation.getName().toString()));

					// methodMethods.get(methodDeclaration.getName().toString()).add(methodInvocation.getName().toString());

					Expression expression = methodInvocation.getExpression();

					if(expression != null)
					{
						// System.out.println(expression);

						ITypeBinding typeBinding = expression.resolveTypeBinding();
						// expression.resolveTypeBinding();

						if(typeBinding != null)
						{
							System.out.println(typeBinding.toString());
						}
						// System.out.println("Expression : " +
						// methodInvocation.getExpression());
						// System.out.println("TypeBinding: " +
						// typeBinding);
						// System.out.println("Type: " +
						// typeBinding.toString());
					}

				}
				catch(NullPointerException nullPointerException)
				{
					nullPointerException.printStackTrace();
				}

				return true;
			}

		});
	}

	private void percentOfClassWithManyMethods()
	{
		int numberToSelect = (classCounter * PERCENT) / 100;
		int cpt = 0;

		for(SetType it : classWithManyMethods)
			if(cpt != numberToSelect)
			{
				percentClassWithManyMethods.add(it.toString());
				cpt++;
			}
			else
			{
				return;
			}
	}

	private void percentOfClassWithManyAttributs()
	{
		int numberToSelect = (classCounter * PERCENT) / 100;
		int counter = 0;

		for(SetType setType : classWithManyAttributes)
			if(counter != numberToSelect)
			{
				percentClassWithManyAttributes.add(setType.toString());
				counter++;
			}
			else
				return;
	}

	private void percentOfMethodsWithLargestCode()
	{
		int numberToSelect = (methodCounter * PERCENT) / 100;
		int counter = 0;

		for(SetType setType : methodsWithLargestCode)
			if(counter != numberToSelect)
			{
				percentMethodsWithLargestCode.add(setType.toString());
				counter++;
			}
			else
				return;
	}

	private void mergeBetweenClassWithManyAttributesAndMethods()
	{
		for(String cMethods : percentClassWithManyMethods)
			for(String cAttributes : percentClassWithManyAttributes)
				if(cMethods.equals(cAttributes))
					classWithManyMethodsAndAttributes.add(cMethods.toString());
	}

	public int getMethodsAverage()
	{
		return methodCounter / classCounter;
	}

	public int getCodeLineMethodAverage()
	{
		return methodLineCounter / methodCounter;
	}

	public int getAttributeAverage()
	{
		return attributeCounter / classCounter;
	}

	public ASTParser getAstParser()
	{
		return astParser;
	}

	public Collection<String> getSourceFiles()
	{
		return sourceFiles;
	}

	public int getClassCounter()
	{
		return classCounter;
	}

	public int getLineCounter()
	{
		return lineCounter;
	}

	public int getMethodCounter()
	{
		return methodCounter;
	}

	public int getPackageCounter()
	{
		return packageCounter;
	}

	public List<String> getPercentClassWithManyMethods()
	{
		return percentClassWithManyMethods;
	}

	public List<String> getPercentClassWithManyAttributes()
	{
		return percentClassWithManyAttributes;
	}

	public Collection<String> getClassWithManyMethodsAndAttributes()
	{
		return classWithManyMethodsAndAttributes;
	}

	public Collection<String> getClassWithMoreThanXMethods()
	{
		return classWithMoreThanXMethods;
	}

	public Collection<String> getPercentMethodsWithLargestCode()
	{
		return percentMethodsWithLargestCode;
	}

	public int getMaximumMethodParameter()
	{
		return maximumMethodParameter;
	}

	public Map<String, Collection<String>> getClassMethods()
	{
		return classMethods;
	}

	public Map<String, Collection<String>> getMethodMethods()
	{
		return methodMethods;
	}

	public Map<String, TreeStructure> getTreeStructures()
	{
		return treeStructures;
	}

	public TreeSet<SetType> getClassWithManyMethods()
	{
		return classWithManyMethods;
	}

	public TreeSet<SetType> getClassWithManyAttributes()
	{
		return classWithManyAttributes;
	}

	public TreeSet<SetType> getMethodsWithLargestCode()
	{
		return methodsWithLargestCode;
	}

	public int getAttributeCounter()
	{
		return attributeCounter;
	}

	public TreeSet<String> getPackages()
	{
		return packages;
	}

	public int getMethodLineCounter()
	{
		return methodLineCounter;
	}
}