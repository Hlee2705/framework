# Sprint 7 : gestion automatique des parametres des methodes d'actions

## test : afficher les parametres detectes dans FrontControllerServlet

## test dans DeptController

## Ajouter maven-compiler-plugin dans pom.xml de testFramework
Pourquoi <parameters>true</parameters> ?
Cette option demande à Maven de compiler Java en conservant les noms réels des paramètres dans les fichiers .class.

## recuperer les valeurs avec request.getParameter()
dans FrontControllerServlet

## preparer les arguments : construire un tableau d'argument et l'envoyer à methode.invoke()

## Modifier la construction de arguments: convertir automatiquement selon le type

### test dans DeptController: testAge(int age)