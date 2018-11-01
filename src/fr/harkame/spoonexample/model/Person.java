package fr.harkame.spoonexample.model;

public class Person
{
	private int age;

	private String name;

	public Person(int age, String name)
	{
		super();
		this.age = age;
		this.name = name;
	}

	@Override
	public String toString()
	{
		return "Person : " + System.getProperty("line.separator") + 
			"Name : " + name + System.getProperty("line.separator")
			+ "Age : " + age + System.getProperty("line.separator");
	}
}
