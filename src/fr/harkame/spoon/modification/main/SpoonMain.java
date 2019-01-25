package fr.harkame.spoon.modification.main;

import java.io.IOException;

import fr.harkame.spoon.modification.example.SpoonExample;

public class SpoonMain
{
	public static void main(String[] Args) throws IOException
	{		
		SpoonExample<Void> spoonExample = new SpoonExample<Void>("./src/fr/harkame/spoon/modification/model/Person.java", "fr.harkame.spoon.modification.model.Person");
		
		spoonExample.addField("city", String.class);
		
		spoonExample.addMethod("newMethod", "System.out.println(\"New method\")");
		
		spoonExample.createFile("./src/fr/harkame/spoon/modification/model/modified/Person.java", "fr.harkame.spoon.modification.model.modified");
	}
}
