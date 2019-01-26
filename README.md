# Auteurs
+ Anthony Chaillot
+ Louis Daviaud

# Installation

Sous-eclipse (ou autre), ajouter TOUS les fichiers jar (lib/) au build path

# Utilisation

## ASTParser

Executer fr.harkame.astparser.main.ASTParserMain.java

## Spoon

Executer fr.harkame.spoon.modification.main.SpoonMain.java

## Fonctionnement

Par défault, l'analyse porte sur le projet lui même, pour analyser un projet il suffit de mettre le chemin du code source (src/) dans les mains au lieu de "./src"

## Metriques (ASTParser)

On relève quelques métriques :
+ Nombre de classes
+ Nombre de méthodes
+ ... (Voir ASTParserMain)

## Graphe d'appels (ASTPArser)

Affichage l'ensemble des classes internes an projet (en bleu), pour chaque classe on vois la liste des méthodes de cette dernière.
Une flèche relie les méthodes lorsqu'elles s'appellent entre elles. (Dans le sens de la flèche, methode1 -> méthodeB).

## Exemple avec Spoon (Modification)

Dans ce projet nous avons tester la modification de code avec Spoon (Un code qui modifie du code).

Dans cet exemple nous allons modifier une classe "Person"

+ Ajout d'un attribut "city"
	+ Ajout de l'attribut dans la liste des attributs
	+ Ajout de l'attribut dans le constructeur
	+ Ajout de l'attribut dans la méthode toString
+ Ajout d'une méthode
+ Enregistrement des modification dans une classe "Person" dans un package "modified"

Avant modification

``` java
package fr.harkame.spoon.modification.model;

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

```

Après modification

``` java

package fr.harkame.spoon.modification.model.modified;

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

```

# Code (packages)

+ astparser : La partie avec ASTParser
	+ astparser : La partie analyse avec ASTParser (récupération des données, etc)
		+ example :
			+ visitor : Nos Visiteurs pour analyser un projet
		+ main : Le Main de la partie ASTParser
		+ util : Des méthodes utiles pour certaine étapes de l'analyse
	+ structure : Nos différentes structure pour afficher le graphe/calculer certaine métrique
	+ graph : La partie affichage des graphes
+ spoon : La partie avec Spoon

# Librairie

+ ASTParser
+ [Spoon](http://spoon.gforge.inria.fr)
+ [mxGraph](https://github.com/jgraph/mxgraph) : Pour l'affichage des graphes d'appel/de couplage
