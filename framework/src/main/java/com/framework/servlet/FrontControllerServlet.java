package com.framework.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.framework.annotation.ApiRest;
import com.framework.annotation.Auth;
import com.framework.annotation.RequestParam;
import com.framework.model.MapFile;
import com.framework.model.Mapping;
import com.framework.model.ModelView;
import com.framework.model.Session;
import com.framework.model.UrlMethod;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@MultipartConfig
public class FrontControllerServlet extends HttpServlet {

    private Map<UrlMethod, Mapping> mappings;

    // creer un objte gson
    private Gson gson = new Gson();

    // attribut pour le repertoire
    private String uploadDirectory;

    // Préfixe et suffixe des vues
    private static final String PREFIX = "/WEB-INF/views/";
    private static final String SUFFIX = ".jsp";

    private Map<Class<?>, Object> controleurs = new ConcurrentHashMap<>();

    @Override
    public void init() throws ServletException {

        mappings = (Map<UrlMethod, Mapping>) getServletContext().getAttribute("globalMappings");

        String sessionAuthAttribute = getServletConfig().getInitParameter("sessionAuthAttribute");

        getServletContext().setAttribute("sessionAuthAttribute", sessionAuthAttribute);

        // recuperer le nom de l'attribut des roles
        String sessionRolesAttribute = getServletConfig().getInitParameter("sessionRolesAttribute");

        getServletContext().setAttribute("sessionRolesAttribute", sessionRolesAttribute);

        if (mappings == null) {
            throw new ServletException(
                    "Le Listener n'a pas chargé les mappings.");
        }

        System.out.println("================================");
        System.out.println("FrontController initialisé");
        System.out.println("Nombre de routes : " + mappings.size());
        System.out.println("================================");

        try {
            chargerConfiguration();
            System.out.println("Répertoire d'upload : " + uploadDirectory);
        } catch (IOException e) {
            throw new ServletException(
                    "Erreur lors du chargement de la configuration.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String context = request.getContextPath();

        String url = uri.substring(context.length());

        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        String httpMethod = request.getMethod().toUpperCase();

        UrlMethod key = new UrlMethod(url, httpMethod);

        Mapping mapping = mappings.get(key);

        if (mapping == null) {
            var dispatcher = request.getServletContext()
                    .getNamedDispatcher("default");

            if (dispatcher != null) {
                dispatcher.forward(request, response);
                return;
            }

            throw new ServletException(
                    "Le servlet par défaut de Tomcat est introuvable.");
        }

        try {

            // Récupérer la classe du contrôleur
            Class<?> controllerClass = mapping.getControllerClass();

            // Récupérer l'instance existante ou en créer une nouvelle
            Object controller = controleurs.computeIfAbsent(controllerClass, classe -> {
                try {
                    return classe.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Erreur lors de la création du contrôleur : "
                            + classe.getName(), e);
                }
            });

            // Vérifier l'identité de l'instance du contrôleur
            System.out.println(
                    "Contrôleur : " + controllerClass.getSimpleName()
                            + " | Instance : " + System.identityHashCode(controller));

            Method method = mapping.getMethod();

            // Méthode à appeler
            Auth auth = method.getAnnotation(Auth.class);

            if (auth != null) {

                // 1. Recuperer le nom de l'attribut d'authentification
                String nomAttributAuth = (String) getServletContext()
                        .getAttribute("sessionAuthAttribute");

                if (nomAttributAuth == null || nomAttributAuth.isEmpty()) {
                    throw new ServletException(
                            "Le nom de l'attribut d'authentification n'est pas configuré.");
                }

                // 2. Recuperer la session existante
                HttpSession sessionsExistante = request.getSession(false);

                // 3. Verifier si l'utilisateur est authentifié
                if (sessionsExistante == null
                        || sessionsExistante.getAttribute(nomAttributAuth) == null) {

                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("text/plain;charset=UTF-8");

                    response.getWriter().println(
                            "Accès refusé : vous devez être authentifié.");

                    return;
                }

                // 4. Recuperer les roles autorisés depuis l'annotation
                String[] rolesAutorises = auth.roles();

                // 5. Verifier si des roles sont exigés
                if (rolesAutorises.length > 0) {

                    String nomAttributRoles = (String) getServletContext()
                            .getAttribute("sessionRolesAttribute");

                    if (nomAttributRoles == null || nomAttributRoles.isEmpty()) {
                        throw new ServletException(
                                "Le nom de l'attribut des roles n'est pas configuré.");
                    }

                    // Recuperer les roles de l'utilisateur
                    Object rolesUtilisateur = sessionsExistante.getAttribute(nomAttributRoles);

                    // Verifier si les roles de l'utilisateur existent
                    if (rolesUtilisateur == null || !(rolesUtilisateur instanceof String[])) {

                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("text/plain;charset=UTF-8");

                        response.getWriter().println(
                                "Accès refusé : aucun rôle valide n'est associé à votre compte.");

                        return;
                    }

                    // Convertir les roles en tableau de String
                    String[] rolesUtilisateurArray = (String[]) rolesUtilisateur;

                    // verfier si l'utilisateur possede au moins un role autorisé
                    boolean roleAutorise = false;

                    for (String roleAutoriseActuel : rolesAutorises) {
                        for (String roleUtilisateur : rolesUtilisateurArray) {
                            if (roleAutoriseActuel.equals(roleUtilisateur)) {
                                roleAutorise = true;
                                break;
                            }
                        }
                        if (roleAutorise) {
                            break;
                        }
                    }

                    // refuser l'acces si aucun role ne correspond
                    if (!roleAutorise) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("text/plain;charset=UTF-8");

                        response.getWriter().println(
                                "Accès refusé : vous ne possédez pas le rôle nécessaire.");

                        return;
                    }
                }
            }

            // Récupération des paramètres
            java.lang.reflect.Parameter[] parameters = method.getParameters();

            Object[] arguments = new Object[parameters.length];

            Session sessionUtilisee = null;
            HttpSession httpSession = null;

            for (int i = 0; i < parameters.length; i++) {

                java.lang.reflect.Parameter parameter = parameters[i];

                String nomParametre = parameter.getName();

                if (parameter.isAnnotationPresent(RequestParam.class)) {
                    RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                    nomParametre = annotation.value();
                }

                Class<?> type = parameter.getType();

                Object valeurConvertie;

                if (type == Session.class) {
                    httpSession = request.getSession();
                    sessionUtilisee = new Session();

                    Enumeration<String> noms = httpSession.getAttributeNames();

                    while (noms.hasMoreElements()) {
                        String nom = noms.nextElement();
                        Object valeur = httpSession.getAttribute(nom);

                        sessionUtilisee.setAttribute(nom, valeur);
                    }

                    arguments[i] = sessionUtilisee;

                } else if (type == MapFile.class) {
                    // cas parametre fichier
                    MapFile fichier = recupererFichier(request, nomParametre);

                    if (fichier != null) {
                        String chemin = enregistrerFichier(fichier);

                        System.out.println("Fichier enregistré : " + chemin);

                    }

                    valeurConvertie = fichier;
                    arguments[i] = valeurConvertie;

                } else if (estTypeSimple(type)) {

                    // cas parametre simple
                    String valeur = request.getParameter(nomParametre);

                    valeurConvertie = convertirParametre(valeur, type, nomParametre);
                    arguments[i] = valeurConvertie;

                } else {

                    // cas parametre objet
                    System.out.println("Objet à construire : " + type.getSimpleName());

                    // getDeclaredConstructor(): recherche le constructor sans argument
                    // newInstance() : appelle ce constructeur sans argument
                    valeurConvertie = type.getDeclaredConstructor().newInstance();

                    // remplir automatiquement ses proprietes
                    remplirObjet(valeurConvertie, request);

                    System.out.println("Objet cree : " + valeurConvertie);

                    arguments[i] = valeurConvertie;

                }

                // System.out.println("Parametre : " + nomParametre);
                // System.out.println("Type : " + type.getSimpleName());
                // System.out.println("Valeur reçue : " + valeur);
                // System.out.println("Valeur convertie : " + valeurConvertie);
            }

            // Exécution
            Object result = method.invoke(controller, arguments);

            // cas session
            if (sessionUtilisee != null) {
                // rechercher les attributs à supprimer
                List<String> nomsASupprimer = new ArrayList<>();

                // parcourt tous les attributs actuellement enregistrés dans la session Http
                Enumeration<String> noms = httpSession.getAttributeNames();

                while (noms.hasMoreElements()) {
                    String nom = noms.nextElement();

                    if (!sessionUtilisee.getValeurs().containsKey(nom)) {
                        nomsASupprimer.add(nom);
                    }
                }

                // supprimer les attributs retirés dans session
                for (String nom : nomsASupprimer) {
                    httpSession.removeAttribute(nom);
                }

                // ajouter ou mettre a jour les attributs
                for (Map.Entry<String, Object> entree : sessionUtilisee.getValeurs().entrySet()) {
                    httpSession.setAttribute(entree.getKey(), entree.getValue());
                }
            }

            // Cas 1 : API REST
            if (method.isAnnotationPresent(ApiRest.class)) {

                response.setContentType("application/json;charset=UTF-8");

                if (result instanceof String) {

                    // Le développeur retourne déjà du JSON
                    response.getWriter().print(result);

                } else {

                    // Le framework transforme l'objet Java en JSON
                    String json = gson.toJson(result);

                    response.getWriter().print(json);
                }

                return;
            }

            // Cas 2 : ModelView
            if (result instanceof ModelView) {

                ModelView mv = (ModelView) result;

                // Envoi des données à la requête
                for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                    request.setAttribute(
                            entry.getKey(),
                            entry.getValue());
                }

                // Construction du chemin de la vue
                String view = PREFIX
                        + mv.getView()
                        + SUFFIX;

                // Redirection vers la JSP
                request.getRequestDispatcher(view)
                        .forward(request, response);

                return;
            }

            // ----------- Cas 2 : autre type de retour ----------
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h2>Méthode exécutée</h2>");
            response.getWriter().println("<p>Contrôleur : "
                    + mapping.getControllerClass().getSimpleName()
                    + "</p>");
            response.getWriter().println("<p>Méthode : "
                    + method.getName()
                    + "</p>");
            response.getWriter().println("<p>Résultat : "
                    + result
                    + "</p>");

        } catch (ParametreInvalideException e) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain;charset=UTF-8");

            response.getWriter().println(e.getMessage());

        } catch (Exception e) {

            throw new ServletException(e);
        }
    }

    private static class ParametreInvalideException extends Exception {

        public ParametreInvalideException(String message) {
            super(message);
        }
    }

    private Object convertirParametre(
            String valeur,
            Class<?> type,
            String nomParametre)
            throws ParametreInvalideException {

        // Paramètre absent
        if (valeur == null) {

            // Les types primitifs ne peuvent pas recevoir null
            if (type.isPrimitive()) {
                throw new ParametreInvalideException(
                        "Paramètre obligatoire manquant : "
                                + nomParametre);
            }

            // Pour les types objets, null est accepté
            return null;
        }

        try {

            // String
            if (type == String.class) {
                return valeur;
            }

            // int / Integer
            else if (type == int.class || type == Integer.class) {
                return Integer.parseInt(valeur);
            }

            // long / Long
            else if (type == long.class || type == Long.class) {
                return Long.parseLong(valeur);
            }

            // double / Double
            else if (type == double.class || type == Double.class) {
                return Double.parseDouble(valeur);
            }

            // float / Float
            else if (type == float.class || type == Float.class) {
                return Float.parseFloat(valeur);
            }

            // boolean / Boolean
            else if (type == boolean.class || type == Boolean.class) {

                if (!valeur.equalsIgnoreCase("true")
                        && !valeur.equalsIgnoreCase("false")) {

                    throw new ParametreInvalideException(
                            "Valeur invalide pour le paramètre : "
                                    + nomParametre
                                    + ". Valeurs attendues : true ou false");
                }

                return Boolean.parseBoolean(valeur);
            }

            // short / Short
            else if (type == short.class || type == Short.class) {
                return Short.parseShort(valeur);
            }

            // byte / Byte
            else if (type == byte.class || type == Byte.class) {
                return Byte.parseByte(valeur);
            }

            // char / Character
            else if (type == char.class || type == Character.class) {

                if (valeur.length() != 1) {

                    throw new ParametreInvalideException(
                            "Valeur invalide pour le paramètre : "
                                    + nomParametre
                                    + ". Un seul caractère est attendu.");
                }

                return valeur.charAt(0);
            }

            // Type non pris en charge
            return valeur;

        } catch (NumberFormatException e) {

            throw new ParametreInvalideException(
                    "Valeur invalide pour le paramètre : "
                            + nomParametre);
        }
    }

    // methode pour reconnaitre les types simples
    private boolean estTypeSimple(Class<?> type) {

        return type == String.class
                || type == int.class || type == Integer.class
                || type == long.class || type == Long.class
                || type == double.class || type == Double.class
                || type == float.class || type == Float.class
                || type == boolean.class || type == Boolean.class
                || type == short.class || type == Short.class
                || type == byte.class || type == Byte.class
                || type == char.class || type == Character.class;
    }

    private void remplirObjet(Object objet, HttpServletRequest request) throws Exception {
        // recuperer la classe de l'objet
        Class<?> type = objet.getClass();

        // parcourir les attributs de la classe
        Field[] attibuts = type.getDeclaredFields();

        for (Field attribut : attibuts) {

            // recuperer le nom et le type de l'attribut
            String nomAttribut = attribut.getName();
            Class<?> typeAttribut = attribut.getType();

            // recuperer la valeur envoyee par le formulaire
            String valeur = request.getParameter(nomAttribut);

            // ignorer les attributs sans valeur correspondante
            if (valeur == null) {
                continue;
            }

            // convertir la valeur vers le type de l'attribut
            Object valeurConvertie = convertirParametre(valeur, typeAttribut, nomAttribut);

            // construire le nom du setter
            String nomSetter = "set" + Character.toUpperCase(nomAttribut.charAt(0))
                    + nomAttribut.substring(1);

            // rechercher le setter correspondant
            Method setter = type.getMethod(nomSetter, typeAttribut);

            // appeler le setter pour affecter la valeur
            setter.invoke(objet, valeurConvertie);
        }
    }

    private MapFile recupererFichier(HttpServletRequest request, String nomParametre) throws Exception {
        // recuperation de la partie (Part) du fichier
        Part partie = request.getPart(nomParametre);

        // validation
        if (partie == null || partie.getSubmittedFileName() == null || partie.getSubmittedFileName().isEmpty()) {
            return null;
        }

        String nomFichier = partie.getSubmittedFileName();

        String extension = "";

        int positionPoint = nomFichier.lastIndexOf('.');

        if (positionPoint >= 0 && positionPoint < nomFichier.length() - 1) {
            extension = nomFichier.substring(positionPoint + 1);
        }

        // lit le contenu du fichier dans un tableau de bytes
        byte[] contenu = partie.getInputStream().readAllBytes();

        return new MapFile(nomFichier, extension, contenu);
    }

    private void chargerConfiguration() throws IOException {
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("framework.properties")) {
            if (input == null) {
                throw new IOException("Le fichier framework.properties est introuvable");
            }

            properties.load(input);

            uploadDirectory = properties.getProperty("upload.directory");

            if (uploadDirectory == null || uploadDirectory.trim().isEmpty()) {
                throw new IOException(
                        "La propriété upload.directory est absente ou vide.");
            }
        }
    }

    private String enregistrerFichier(MapFile fichier) throws IOException {
        // verifier si le fichier est bien reçu
        if (fichier == null || fichier.getBytes() == null) {
            throw new IOException("Aucun fichier reçu.");
        }

        // recuperer le repertoire configuré
        File repertoire = new File(uploadDirectory);

        // creer le repertoire s'il n'existe pas
        if (!repertoire.exists()) {
            repertoire.mkdirs();
        }

        // construire le chemin du fichier
        File destination = new File(repertoire, fichier.getNom());

        // ecrire le contenu du fichier sur le disque
        try (FileOutputStream output = new FileOutputStream(destination)) {
            output.write(fichier.getBytes());
        }

        return destination.getAbsolutePath();
    }

}