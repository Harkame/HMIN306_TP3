package fr.harkame.spoonexample.model.modified;

public class Person {
    private java.lang.String city = "";

    private int age;

    private java.lang.String name;

    public Person(int age, java.lang.String name, java.lang.String city) {
        super();
        this.age = age;
        this.name = name;
        this.city = city;;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Person : "
        + "city : " + city
        + "age : " + age
        + "name : " + name
        ;
    }

    public void newMethod() {
        System.out.println("New method");
    }
}