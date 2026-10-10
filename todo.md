# Sprint6

## Créer @ApiRest
- créer l'annotation @ApiRest

    @Retention(RetentionPolicy.RUNTIME)

    - pour que java puisse encore voir 

    @Target(ElementType.METHOD)

    - peut être placée uniquement sur une méthode

## Tester l'annotation coté développeur
- ajouter une méthode testerApi dans la classe DeptController

## Modifier FrontControllerServlet : 
### String → écrire directement le String
- tester apirest avant le traitement de ModelView

    /api/test
         ↓
    FrontControllerServlet
         ↓
    cherche @UrlMapping("api/test", "GET")
         ↓
    trouve testApi()
         ↓
    method.invoke(controller)
         ↓
    résultat = "{\"message\":\"Bonjour depuis mon API\"}"
         ↓
    méthode possède @ApiRest ?
         ↓
    OUI
         ↓
    Content-Type = application/json
         ↓
    écrit directement le String

### Object → Gson.toJson(Object)
#### - Ajouter une librairie JSON au pom.xml du framework
- ajouter la dependance gson dans le pom.xml de framework

#### - Utiliser Gson dans FrontControllerServlet
- creer un objet Gson

    ```Java
    private Gson gson = new Gson();
    ```
- modifier le bloc api rest
    - transformer l'objet java en json

#### Créer un objet java à retourner 
- dans testFramework/src/main/java/ : Employe.java

#### ajouter une api qui retourne API
- dans DeptController :
    - creer une methode testEmploye()

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

## Verifier si le parametre existe : modifier la boucle 

## Gestion correcte des types primitifs lorsqu'un paramètre est absent.
### Ajouter une verification

## Gestion des erreurs de conversion : valeur reçue mais impossible à convertir 
try and catch

## traiter correctement les parametres boolean 

## Permettre aux contrôleurs de recevoir des paramètres dans leurs méthodes métier

### Creer une annotation : RequestParam.java

## Recuperer le nom HTTP depuis RequestParam dans FrontControllerServlet

## tester dans deptController

# Sprint7bis : binding automatique des objets et des parametres 

## ajouter un constructeur sans argument dans les objets 

## dans FrontControllerServlet : distinguer les types simples des objets 
- si le type est simple ou s'il s'agit d'un objet à construire
### Ajouter une methode à la fin de FrontControllerServlet : estTipeSimple(type)
- pour reconnaitre les types simples 
### Adapter la preparation des arguments : integrer estTypeSimple() dans la boucle qui prepare les arguments 
