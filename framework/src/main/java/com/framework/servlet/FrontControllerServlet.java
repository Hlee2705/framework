package com.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.framework.annotation.UrlMapping;
import com.framework.model.Mapping;

public class FrontControllerServlet extends HttpServlet {

    // 🗺️ Notre table de routage URL -> Mapping(Classe, Méthode)
    private Map<String, Mapping> urlMappingMap = new HashMap<>();
    private List<Class<?>> classAnnote = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        System.out.println("=== INIT FRAMEWORK : SCAN DES ROUTES ===");

        try {
            String rootPath = getServletContext().getRealPath("/WEB-INF/classes");
            if (rootPath == null)
                throw new ServletException("Chemin WEB-INF/classes introuvable");

            List<Class<?>> toutesLesClasses = scanClasses(new java.io.File(rootPath), "");

            // Parcourir toutes les classes pour trouver les @Controller
            for (Class<?> clazz : toutesLesClasses) {
                if (clazz.isAnnotationPresent(org.springframework.stereotype.Controller.class)) {
                    classAnnote.add(clazz);
                    System.out.println("Contrôleur détecté : " + clazz.getName());
                    
                    // 🔍 Pour chaque contrôleur, on cherche ses méthodes annotées @UrlMapping
                    Method[] methodes = clazz.getDeclaredMethods();
                    for (Method methode : methodes) {
                        if (methode.isAnnotationPresent(UrlMapping.class)) {
                            UrlMapping annotation = methode.getAnnotation(UrlMapping.class);
                            String url = annotation.value(); // Récupère par ex "dept/new"
                            
                            // Enregistrement dans la Map
                            urlMappingMap.put(url, new Mapping(clazz, methode));
                            System.out.println("  -> Route enregistrée : " + url + " -> " + clazz.getSimpleName() + "." + methode.getName() + "()");
                        }
                    }
                }
            }

            System.out.println("Total contrôleurs = " + classAnnote.size());
            System.out.println("Total routes enregistrées = " + urlMappingMap.size());

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // ===== SCANNER LES CLASSES =====
    private List<Class<?>> scanClasses(java.io.File dir, String packageName)
            throws ClassNotFoundException {

        List<Class<?>> classes = new ArrayList<>();
        java.io.File[] files = dir.listFiles();
        if (files == null)
            return classes;

        for (java.io.File file : files) {
            if (file.isDirectory()) {
                String newPackage = packageName.isEmpty()
                        ? file.getName()
                        : packageName + "." + file.getName();
                classes.addAll(scanClasses(file, newPackage));

            } else if (file.getName().endsWith(".class")) {
                String className = packageName.isEmpty()
                        ? file.getName().replace(".class", "")
                        : packageName + "." + file.getName().replace(".class", "");
                try {
                    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                    classes.add(Class.forName(className, true, contextClassLoader));
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    System.out.println("Ignorée : " + className);
                }
            }
        }
        return classes;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        //  Extraction propre de l'URL relative entrée
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String pathInfo = requestURI.substring(contextPath.length());
        
        // Retirer le premier '/' si présent pour matcher avec "dept/new"
        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }

        out.println("<h1>Mini Spring - Routeur</h1>");
        out.println("<p>URL demandée : <strong>" + pathInfo + "</strong></p>");

        //  Vérification si l'URL est supportée
        if (urlMappingMap.containsKey(pathInfo)) {
            Mapping mapping = urlMappingMap.get(pathInfo);
            out.println("<h3 style='color:green;'> URL Supportée !</h3>");
            out.println("<p><strong>" + mapping.getControllerClass().getSimpleName() + "</strong> -> " + mapping.getMethod().getName() + "()</p>");
        } else {
            //  L'URL n'existe pas : Affichage de toutes les méthodes supportées avant l'exception
            out.println("<h3 style='color:red;'> URL non supportée</h3>");
            out.println("<h4>Voici les méthodes annotées par @UrlMapping disponibles :</h4>");
            
            for (Map.Entry<String, Mapping> entry : urlMappingMap.entrySet()) {
                Mapping m = entry.getValue();
                out.println("<p>" + m.getControllerClass().getSimpleName() + " -> " + m.getMethod().getName() + "() [Route : /" + entry.getKey() + "]</p>");
            }

            // Lever l'exception demandée par ton TP
            throw new ServletException("L'url " + pathInfo + " n'est pas supportée");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}