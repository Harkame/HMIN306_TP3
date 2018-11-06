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

public class SpoonExample
{
	private CoreFactory coreFactory;
	private CodeFactory codeFactory;
	private TypeFactory typeFactory;
	
	private CtClass<?> ctClass;
	
	public SpoonExample(String classFilePath, String className)
	{	
		Launcher launcher = new Launcher();
		launcher.addInputResource(classFilePath);
		launcher.buildModel();

		coreFactory = launcher.getFactory().Core();
		codeFactory = launcher.getFactory().Code();
		typeFactory = launcher.getFactory().Type();
		
		ctClass = (CtClass<?>) launcher.getFactory().Type().get(className);
	}
	
	public void addField(String fieldName, Class fieldClass)
	{
		CtField<String> ctFieldCity = codeFactory.createCtField("city", typeFactory.createReference(String.class),
			"\"\"", ModifierKind.PRIVATE);

		ctClass.addFieldAtTop(ctFieldCity);
	}
	
	public void createFile(String classFilePath) throws IOException
	{
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(classFilePath));
		
		bufferedWriter.write("package " + ctClass.getPackage().toString() + ";");
		bufferedWriter.write("");
		bufferedWriter.write(ctClass.toString());
	     
		bufferedWriter.close();
	}
	
	public static void main(String[] Args) throws IOException
	{


		System.out.println(ctClass.toString());

		CtConstructor<?> ctConstructor = (CtConstructor<?>) ctClass.getConstructors().toArray()[0];

		System.out.println(ctConstructor.toString());

		CtParameter<String> ctParameterCity = coreFactory.createParameter();
		ctParameterCity.setType(typeFactory.createReference(String.class));
		ctParameterCity.setSimpleName("city");

		ctConstructor.addParameter(ctParameterCity);

		System.out.println(ctConstructor.toString());
		CtBlock<?> ctBlockConstructorBody = ctConstructor.getBody();

		ctBlockConstructorBody.addStatement(codeFactory.createCodeSnippetStatement("this.city = city;"));
		ctConstructor.setBody(ctBlockConstructorBody);

		System.out.println(ctConstructor.toString());

		CtMethod<?> ctMethodToString = ctClass.getMethod("toString");

		System.out.println(ctMethodToString);
		
		StringBuilder toString = new StringBuilder();
		
		toString.append("return ");
		toString.append("\"Person : \"");
		toString.append(System.getProperty("line.separator"));

		for(CtFieldReference<?> ctFieldReference : ctClass.getAllFields())
		{
			toString.append("+ \"");
			toString.append(ctFieldReference.getSimpleName());
			toString.append(" : \" + ");
			toString.append(ctFieldReference.getSimpleName());
			toString.append(System.getProperty("line.separator"));
		}
		
		ctBlockConstructorBody = ctConstructor.getBody();
		
		List<CtStatement> statements = new ArrayList<CtStatement>();
		statements.add(codeFactory.createCodeSnippetStatement(toString.toString()));
		
		ctMethodToString.getBody().setStatements(statements);
		
		ctConstructor.setBody(ctBlockConstructorBody);
		
		System.out.println(ctMethodToString.toString());
		
		CtMethod<Void> ctMethod = coreFactory.createMethod();
		
		ctMethod.setSimpleName("newMethod");
		ctMethod.setVisibility(ModifierKind.PUBLIC);
		ctMethod.setType(typeFactory.createReference(void.class));

		ctMethod.setBody(codeFactory.createCodeSnippetStatement("System.out.println(\"New method\")"));
		
		ctClass.addMethod(ctMethod);
		
		ctClass.setSimpleName("ModifiedPerson");
		
		SpoonExample spoonExample = new SpoonExample("./src/fr/harkame/spoonexample/model/Person.java", "fr.harkame.spoonexample.model.Person");
		
		spoonExample.createFile("./src/fr/harkame/spoonexample/model/ModifiedPerson.java");
		
		
		System.out.println(ctClass.toString());
	
	}
	
	/*
	public static void main(String[] Args)
	{
    		ModifiedPerson modifiedPerson = new ModifiedPerson(42, "Toto", "Montpellier");
    		System.out.println(modifiedPerson);
	}
	*/
}
