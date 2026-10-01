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

    
