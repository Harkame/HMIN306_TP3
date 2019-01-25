package fr.harkame.astparser.astparser.example.visitor;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import fr.harkame.astparser.structure.SetType;
import fr.harkame.astparser.structure.tree.ClassTree;

public class CustomASTVisitor extends ASTVisitor
{
	public final static int PERCENT = 20;

	public final static int X = 2;

	// METRICS

	public static int classCounter = 0;
	public static int lineCounter = 0;
	public static int methodCounter = 0;
	public static int packageCounter = 0;

	public static List<String> percentClassWithManyMethods = new ArrayList<String>();
	public static List<String> percentClassWithManyAttributes = new ArrayList<String>();

	public static Collection<String> classWithManyMethodsAndAttributes = new ArrayList<String>();

	public static Collection<String> classWithMoreThanXMethods = new ArrayList<String>();
	public static Collection<String> percentMethodsWithLargestCode = new ArrayList<String>();

	public static int maximumMethodParameter = 0;
	public static String maximumMethodParameterName = "";

	public static Map<String, Collection<String>> classMethods = new TreeMap<String, Collection<String>>();
	public static Map<String, Collection<String>> methodMethods = new TreeMap<String, Collection<String>>();

	public static ClassTree classTree = new ClassTree();

	public static TreeSet<SetType> classWithManyMethods = new TreeSet<SetType>();
	public static TreeSet<SetType> classWithManyAttributes = new TreeSet<SetType>();
	public static TreeSet<SetType> methodsWithLargestCode = new TreeSet<SetType>();

	public static int attributeCounter = 0;
	public static int methodLineCounter = 0;
	
	// END METRICS

	public boolean visit(TypeDeclaration node)
	{
		String className = getQualifiedName(node.getName().toString());
		
		classTree.addClassDeclaration(className);
		
		classMethods.put(className.toString(), new ArrayList<String>());

		int localLineCounter = node.toString().length()
				- node.toString().replace(System.getProperty("line.separator"), "").length();

		if (localLineCounter == 0)
			localLineCounter += node.toString().length() - node.toString().replace("\n", "").length();

		lineCounter += localLineCounter;
		classCounter++;

		attributeCounter += node.getFields().length;

		classWithManyAttributes.add(new SetType(className.toString(), node.getFields().length));

		for (MethodDeclaration methodDeclaration : node.getMethods())
		{	
			classTree.addMethodDeclaration(className, methodDeclaration.getName().toString());
			if (methodDeclaration.parameters().size() > maximumMethodParameter)
			{
				maximumMethodParameter = methodDeclaration.parameters().size();
				maximumMethodParameterName = methodDeclaration.getName().toString() + " (" + className + ")";
			}

			localLineCounter = methodDeclaration.getBody().toString().length()
					- methodDeclaration.getBody().toString().replace(System.getProperty("line.separator"), "").length();

			if (localLineCounter == 0)
				localLineCounter += methodDeclaration.getBody().toString().length()
						- methodDeclaration.getBody().toString().replace("\n", "").length();

			methodLineCounter += localLineCounter;

			classMethods.get(className.toString()).add(methodDeclaration.getName().toString());

			methodsWithLargestCode.add(new SetType(
					(methodDeclaration.getName() + " - " + methodDeclaration.getReturnType2() + " - "
							+ methodDeclaration.parameters()),
					localLineCounter, methodDeclaration.getName().toString()));
		}

		if (node.getMethods().length > X)
			classWithMoreThanXMethods.add(className.toString());

		classWithManyMethods.add(new SetType(className.toString(), node.getMethods().length));

		methodCounter += node.getMethods().length;

		return true;
	}

	public boolean visit(MethodInvocation methodInvocation)
	{
		Expression expression = methodInvocation.getExpression();

		if (expression == null)
			return true;

		ITypeBinding typeBinding = expression.resolveTypeBinding();

		IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
		
		//Static call
		if (methodBinding != null && (methodBinding.getModifiers() & Modifier.STATIC) > 0)
		{
		}
		else if (typeBinding != null)
		{
			ASTNode parent = methodInvocation.getParent();
			
			if (parent == null)
				return true;
	
			while (parent.getNodeType() != 31)
			{
				parent = parent.getParent();
	
				if (parent == null)
					return true;
			}
	
			MethodDeclaration methodDeclaration = (MethodDeclaration) parent;
			
			parent = methodInvocation.getParent();
	
			if (parent == null)
				return true;
	
			while (parent.getNodeType() != 55)
				parent = parent.getParent();
	
			TypeDeclaration typeDeclaration = (TypeDeclaration) parent;
			
			String qualifiedClassName = getQualifiedName(typeDeclaration.getName().toString());
			
			if(isProjectClass(getQualifiedName(typeBinding.getName())))
				classTree.addMethodInvocation(qualifiedClassName, methodDeclaration.getName().toString(), getQualifiedName(typeBinding.getName()), methodInvocation.getName().toString());
		}
		
		return true;
	}

	public static void percentOfClassWithManyMethods()
	{
		int numberToSelect = (classCounter * PERCENT) / 100;
		int counter = 0;

		for (SetType it : classWithManyMethods)
			if (counter != numberToSelect)
			{
				percentClassWithManyMethods.add(it.toString());
				counter++;
			} else
				return;
	}

	public static void percentOfClassWithManyAttributs()
	{
		int numberToSelect = (classCounter * PERCENT) / 100;
		int counter = 0;

		for (SetType setType : classWithManyAttributes)
			if (counter != numberToSelect)
			{
				percentClassWithManyAttributes.add(setType.toString());
				counter++;
			} else
				return;
	}

	public static void percentOfMethodsWithLargestCode()
	{
		int numberToSelect = (methodCounter * PERCENT) / 100;
		int counter = 0;

		for (SetType setType : methodsWithLargestCode)
			if (counter != numberToSelect)
			{
				percentMethodsWithLargestCode.add(setType.toString());
				counter++;
			} else
				return;
	}

	public static void mergeBetweenClassWithManyAttributesAndMethods()
	{
		for (String cMethods : percentClassWithManyMethods)
			for (String cAttributes : percentClassWithManyAttributes)
				if (cMethods.equals(cAttributes))
					classWithManyMethodsAndAttributes.add(cMethods.toString());
	}

	public static int getMethodsAverage()
	{
		return methodCounter / classCounter;
	}

	public static int getCodeLineMethodAverage()
	{
		return methodLineCounter / methodCounter;
	}

	public static int getAttributeAverage()
	{
		return attributeCounter / classCounter;
	}
	
	public boolean isProjectClass(String className)
	{
		return ClassVisitor.projectClass.contains(className);
	}
	
	public String getQualifiedName(String classNameToConvert)
	{
		for(String qualifiedClassName : ClassVisitor.projectClass)
		{
			StringBuilder a = new StringBuilder(qualifiedClassName);
			StringBuilder b = a.reverse();
			String c = a.substring(0, b.indexOf("."));
			String d = new StringBuilder(c).reverse().toString();
			
			if(d.equals(classNameToConvert))
				return qualifiedClassName;
		}
		
		return "";
	}
}
