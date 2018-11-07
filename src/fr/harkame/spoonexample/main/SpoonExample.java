package fr.harkame.spoonexample.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtFieldReference;

public class SpoonExample <T>
{
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private CoreFactory coreFactory;
	private CodeFactory codeFactory;
	private TypeFactory typeFactory;
	
	private CtClass<T> ctClass;
	
	/**
	 * Constructor
	 * @param classFilePath Path where found .java file
	 * @param className Name of the search class into classFilePath
	 */
	@SuppressWarnings("unchecked")
	public SpoonExample(String classFilePath, String className)
	{	
		Launcher launcher = new Launcher();
		launcher.addInputResource(classFilePath);
		launcher.buildModel();

		coreFactory = launcher.getFactory().Core();
		codeFactory = launcher.getFactory().Code();
		typeFactory = launcher.getFactory().Type();
		
		ctClass = (CtClass<T>) launcher.getFactory().Type().get(className);
	}
	
	/**
	 * Add an new field into this.ctClass
	 * 
	 * @param fieldName Name of the new field
	 * @param fieldClass Type/Class of the new field
	 */
	public void addField(String fieldName, Class<?> fieldClass)
	{
		CtField<?> ctField = codeFactory.createCtField(fieldName, typeFactory.createReference(fieldClass),
			"\"\"", ModifierKind.PRIVATE);

		ctClass.addFieldAtTop(ctField);
		
		alterConstructor(fieldName, fieldClass);
		
		alterToString();
	}
	
	/**
	 * Add the new field in the constructor (parameter + affectation)
	 * 
	 * This method is called in addField
	 * 
	 * @param fieldName Name of the new field
	 * @param fieldClass Type/Class of the new field
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void alterConstructor(String fieldName, Class<?> fieldClass)
	{		
		CtConstructor<T> ctConstructor = (CtConstructor<T>) ctClass.getConstructors().toArray()[0];
		fieldClass.getTypeName();
		CtParameter ctParameterCity = coreFactory.createParameter();
		ctParameterCity.setType(typeFactory.createReference(fieldClass));
		ctParameterCity.setSimpleName(fieldName);

		ctConstructor.addParameter(ctParameterCity);

		CtBlock<?> ctBlockConstructorBody = ctConstructor.getBody();

		ctBlockConstructorBody.addStatement(codeFactory.createCodeSnippetStatement("this.city = city;"));
		ctConstructor.setBody(ctBlockConstructorBody);
		
		ctBlockConstructorBody = ctConstructor.getBody();
	}
	
	/**
	 * Add the new field in the method toString
	 * 
	 * This method is called in addField
	 * 
	 */
	private void alterToString()
	{
		CtMethod<?> ctMethod = ctClass.getMethod("toString");

		StringBuilder toString = new StringBuilder();
		
		toString.append("return ");
		toString.append("\"" + ctClass.getSimpleName() + " : \"");
		toString.append(LINE_SEPARATOR);

		for(CtFieldReference<?> ctFieldReference : ctClass.getAllFields())
		{
			toString.append("+ \"");
			toString.append(ctFieldReference.getSimpleName());
			toString.append(" : \" + ");
			toString.append(ctFieldReference.getSimpleName());
			toString.append(LINE_SEPARATOR);
		}
		
		List<CtStatement> statements = new ArrayList<CtStatement>();
		statements.add(codeFactory.createCodeSnippetStatement(toString.toString()));
		
		ctMethod.getBody().setStatements(statements);
	}
	
	/**
	 * Add an new method in this.ctClass
	 * 
	 * (Simplified, no parameter, no return type)
	 * 
	 * @param methodName Name of the method
	 * @param methodBody Body of the method
	 */
	public void addMethod(String methodName, String methodBody)
	{
		CtMethod<Void> ctMethod = coreFactory.createMethod();
		
		ctMethod.setSimpleName(methodName);
		ctMethod.setVisibility(ModifierKind.PUBLIC);
		ctMethod.setType(typeFactory.createReference(void.class));
		ctMethod.setBody(codeFactory.createCodeSnippetStatement(methodBody));
		
		ctClass.addMethod(ctMethod);
	}
	
	/**
	 * Saved the modified Class (this.ctClass) into an file
	 * 
	 * @param classFilePath File where save this.ctClass
	 * @param packageName Package name of the modified class
	 * 
	 * @throws IOException bufferedWriter.write, bufferedWriter.close
	 */
	public void createFile(String classFilePath, String packageName) throws IOException
	{
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(classFilePath));
		
		bufferedWriter.write("package " + packageName + ";");
		bufferedWriter.write(LINE_SEPARATOR + LINE_SEPARATOR);
		bufferedWriter.write(ctClass.toString());
	     
		bufferedWriter.close();
	}
	
	public static void main(String[] Args) throws IOException
	{		
		SpoonExample<Void> spoonExample = new SpoonExample<Void>("./src/fr/harkame/spoonexample/model/Person.java", "fr.harkame.spoonexample.model.Person");
		
		spoonExample.addField("city", String.class);
		
		spoonExample.addMethod("newMethod", "System.out.println(\"New method\")");
		
		spoonExample.createFile("./src/fr/harkame/spoonexample/model/modified/Person.java", "fr.harkame.spoonexample.model.modified");
	}
}
