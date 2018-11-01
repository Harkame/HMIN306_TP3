package fr.harkame.spoonexample.main;

import java.util.ArrayList;
import java.util.List;

import spoon.Launcher;
import spoon.reflect.code.CtBlock;
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
	public static void main(String[] Args)
	{
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/fr/harkame/spoonexample/example/Person.java");
		launcher.buildModel();

		CoreFactory coreFactory = launcher.getFactory().Core();
		CodeFactory codeFactory = launcher.getFactory().Code();
		TypeFactory typeFactory = launcher.getFactory().Type();

		CtClass<?> ctClass = (CtClass<?>) launcher.getFactory().Type().get("fr.harkame.spoonexample.example.Person");

		System.out.println(ctClass.toString());

		CtField<String> ctFieldCity = codeFactory.createCtField("city", typeFactory.createReference(String.class),
			"\"\"", ModifierKind.PRIVATE);

		ctClass.addFieldAtTop(ctFieldCity);

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
		toString.append("\"Person : \" + ");
		toString.append(System.getProperty("line.separator"));
		
		for(CtFieldReference<?> ctFieldReference : ctClass.getAllFields())
		{
			toString.append("\"");
			toString.append(ctFieldReference.getSimpleName());
			toString.append(" : \" + ");
			toString.append(ctFieldReference.getSimpleName());
			toString.append(" + ");
			toString.append(System.getProperty("line.separator"));
		}
		
		ctBlockConstructorBody = ctConstructor.getBody();
		
		List statements = new ArrayList();
		statements.add(codeFactory.createCodeSnippetStatement(toString.toString()));
		
		ctMethodToString.getBody().setStatements(statements);
		
		ctConstructor.setBody(ctBlockConstructorBody);
		
		System.out.println(ctMethodToString.toString());
		
		CtMethod ctMethod = coreFactory.createMethod();
		
		ctMethod.setSimpleName("newMethod");
		ctMethod.setVisibility(ModifierKind.PUBLIC);
		ctMethod.setType(typeFactory.createReference(void.class));

		ctMethod.setBody(codeFactory.createCodeSnippetStatement("System.out.println(\"New method\")"));
		
		ctClass.addMethod(ctMethod);
		
		System.out.println(ctClass.toString());
	}
}
