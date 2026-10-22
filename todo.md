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

## Creer automatiquement un objet 
- getDeclaredConstructor(): recherche le constructor sans argument
- newInstance() : appelle ce constructeur sans argument

## Preparer le remplissage automatique des objets 
### ajouter une methode remplirObjet() dans FrontControllerServlet

## Remplir l'objet avant d'appeler la methode du controleur 
- remplir l'objet dans la boucle ava,t d'appeler la methode du controleur

## test : ajouter une methode qui reçoit directement un objet dans DeptController

# Sprint8 : upload de fichier 
- client -> serveur 
- par exemple le cas d'un formulaire:
     - misy binaire/file
     - type de l'input : file
     - tonga any am serveur ao anaty request : mijery raha misy type file
          - cas données tsotra : binding
          - cas champ formulaire misy type file 
               - le developpeur decide : 
                    - stocker cote serveur ou
                    - dans une base de données 
                    - ny ahatongavany ao am lay methode d'action le type file??
                    - map??
                    - misy anarana le fichier rehefa alefa avy any amin ny formulaire : tableau de bytes 
                    - objectif : rendre ces fichiers disponibles dans les methodes d'action: controller
                    - parametre fileMap : cle String, valeur : tableau de bytes 
                    - ex: objet employe de raha mandray cin de bindena automatique
                    - ex : classe Employe : nom, prenom
                         - mandefa scan cin recto verso, dans attribut : + cin recto verso pas de type String mais un tableau de bytes 
                              - verifier si type tableau de byte on recupere
                         - sady misy map na ao anatin le objet

                    - conclusion : creer une classe , de type an le objet le classe : 
                         - MapFile : 
                              - attribut : nom an le file(fileName), extension, tableau de bytes  
                              - instancier dans le framework
                                   ex : recto cin type file, mijery ao anatin le field an le objet ana parametre an amin le action
                                   de mijery hoe misy type MapFile de instancier le mapFile de alefa ao 

- rehefa tonga ao am le methode d'action le bytes de :
     - soit sauvegarder dans la base de données 
     - soit dans un fichier 

- mila manana chemin hi stocker azy 
     - tsy atao anaty request, tsy atao anaty variable
     - atao anaty fichier properties de avy eo reecuperer dans le controller 

## fonctionnement général du framework:
- Le client envoie le formulaire contenant des champs classiques et un fichier.
- Le FrontControllerServlet reçoit la requête.
- Le framework identifie les paramètres de la méthode d'action.
- Pour un objet classique comme Employe, il applique le binding existant.
- Pour un paramètre de type MapFile, il récupère le fichier envoyé et construit un objet MapFile.
- Le framework transmet les objets préparés à la méthode du contrôleur.
- Le fichier peut ensuite être enregistré dans le répertoire défini dans le fichier .properties.

1. Créer la classe MapFile dans model
- avec comme attributs:
     - nom : nom du fichier reçu
     - extension du fichier : jpeg, pdf, ...
     - bytes : contenu du fichier sous forme de tableau d'octets pour recuperer les données reelles du fichier 

2. Modifier FrontControllerServlet pour qu'il puisse reconnaitre un objet de type MapFile et preparer l'objet à transmettre au controleur
- recupération de l'objet envoyé par le formulaire et creation de l'objet MapFile
- Actuellement, nous avons 3 cas: 
     - type simple
     - objet classique
     - `Fichier : `
          - ajouter les deux imports :
          ```Java
          import jakarta.servlet.http.Part;
          import com.framework.model.MapFile;
          ```
          - La classe Part permet de recuperer le nom du fichier et son contenu
          - Autoriser la reception des fichiers avec l'annotation @MultipartConfig
               - Cette annotation permet au conteneur de servlets de prendre en charge les requêtes multipart et d'exposer leurs parties via request.getPart().
          - Creer une methode pour construire `MapFile`
               - `recupererFichier(request, nomParametre)`

          - Modifier la boucle de preparation des arguments dans `ProcessResquest()`
               - ajouter la condition pour le cas ou le parametre est un fichier
               - la valeur est `recupererFichier(request, nomParametre)`

3. Creer le formulaire d'upload
- ajouter un formulaire permettant d'envoyer un fichier 

4. ajouter une methode d'action dans le controller 
- Verifier les informations de Mapfile dans la methode upload()

5. Configurer le répertoire dans lequel les fichiers seront stockés
- Creer le fichier framework.properties 
     - `framework/src/main/resources/framework.properties`
     - ajouter le chemin du dossier pour stocker les fichiers 

6. Créer une methode pour lire la configuration dans `FrontControllerServlet`
- ajouter les imports: 
     ```Java
     import java.io.IOException;
     import java.io.InputStream;
     import java.util.Properties;
     ```
     - Properties permet de lire les propriétés d'un fichier .properties.
     - InputStream permet de lire le fichier depuis les ressources du projet.

- ajouter un attribut pour le repertoire : 
     ```Java
     private String uploadDirectory; 
     ```
     - cette variable contiendra le chemin recupere depuis le fichier de configuration

- ajouter une methode de chargement : `chargerConfiguration()`

7. Creer une methode pour enregistrer le fichier dans `FrontControllerServlet`
- ajouter les imports 
     ```Java
     import java.io.File;
     import java.io.FileOutputStream;
     import java.io.IOException;
     ```
- ajouter la methode `enregistrerFichier()`
     - Vérifie que l'objet MapFile contient bien des données.
     - Crée le répertoire configuré si nécessaire.
     - Crée le fichier de destination.
     - Écrit les bytes reçus dans ce fichier.

- Integrer l'enrgistrement dans la boucle binding

# Sprint9 : gestion des sessions
- Creer classe Session :
     - map : String, Object 
          - String : nom,
          - Object : valeur
     - methode utilitaire : 
          - ex : pour recuperer variable, supprimer variable, recuperer variable dans session
- Utiliser cette classe dans une methode d'action(de type Session avec comme attribut HttpSession)
     - on peut faire des suppressions, ...

- cote framework : 
     - methode pour recuperer le nom de la variable dans la session => boucler le tableau, recuperer valeur
     - on ne crée pas d'instance : probleme memoire à eviter
     - au moment du traitement du parametre :
          - ajouter un bloc: 
               - si de type Session
                    - creer instance -> map
                    - map va remplir HttpSession
                    - recuperer tous les attributs lors de l'appel
     - apres invoke():
          - mis a jour de HttpSession
               - passage par reference
               - boucle

- cote developpeur 
     - injection si le parametre est de type Session

pas d'attribut dans FrontControllerServlet 
pas dans init mais dans le listener 

1. Creer la classe Session 
- Methodes utilitaires:
     - `setAttribute(String nom, Object valeur)`
          - ajoute ou met à jour un attribut
     - `getAttribute(String nom)`
          - recupere la valeur d'un attribut
     - `removeAttribute(String nom)`
          - supprimer un attribut
     - `contientAttribut(String nom)`
          - verifier l'existence d'un attribut

2. Ajouter l'injection de session dans FrontControllerServlet
- ajouter un cas pour Session
    - `request.getSession()` récupère la session HTTP associée à la requête, ou en crée une si nécessaire. 
    - `getAttributeNames()` permet de parcourir les noms des variables déjà stockées.
     - Chaque variable est copiée dans notre objet Session.
     - arguments[i] transmet cet objet à la méthode du contrôleur.

- Synchroniser la session apres method.invoke()
     - conserver les modifiactions que le controleur effectue sur la session
     - le probleme : 
          - le framework recupere la session Http
          - le framework copie cette donnée dans notre objet Session
          - le controleur modifie notre objet session
     - solution : 
          - Après l'exécution du contrôleur, le framework doit recopier les données de notre objet Session dans HttpSession.

          | Moment | `Session` | `HttpSession` |
          |---|---|---|
          | Avant l'exécution | Rakoto | Rakoto |
          | Après la modification par le contrôleur | Rabe | Rakoto |
          | Après la synchronisation | Rabe | Rabe |

3. Tester la gestion de session dans un controleur
- ajouter une methode dans DeptController 
     - testerSession(session)
          - Le framework récupérera la session HTTP.
          - Il copiera ses attributs dans l'objet Session.
          - La méthode ajoutera l'attribut utilisateur.
          - Après method.invoke(...), le framework synchronisera les attributs avec HttpSession.
          - La réponse affichera Utilisateur en session : Rakoto. 

- verifier la persistance de la session
     - ajouter une deuxieme dans DeptController 
          - lireSession(session)
               - recuperer la valeur deja enregistrée 
- tester removeAtrribute()
     - supprimerSession(session)

- tester la modification d'un attribut 
     - modifierSession(session)


- verifier la conservation de la session sans parametre Session
     - sansSession()

- Combiner Session et RequestParam
     - connexion(request param, session)

4. Ecrire le controleur d'authentification : LoginController
- creer deux methodes : 
     - connexion() : vérifie les identifiants et enregistre l'utilisateur dans la session si la connexion réussit.
     - deconnexion() : supprime l'utilisateur de la session 

# Sprint 10
- annotation vaovao
- verifier si authnetifier 