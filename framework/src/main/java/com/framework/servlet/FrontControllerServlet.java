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
import com.framework.model.UrlMethod;

public class FrontControllerServlet extends HttpServlet {

    // Table de routage : (URL + méthode HTTP) -> Mapping
    private Map<UrlMethod, Mapping> urlMappingMap = new HashMap<>();

    // Liste des contrôleurs détectés
    private List<Class<?>> classAnnote = new ArrayList<>();

    @Override
    public void init() throws ServletException {

        System.out.println("========== INITIALISATION DU FRAMEWORK ==========");

        try {

            String rootPath = getServletContext().getRealPath("/WEB-INF/classes");

            if (rootPath == null) {
                throw new ServletException("Impossible de trouver WEB-INF/classes");
            }

            List<Class<?>> toutesLesClasses =
                    scanClasses(new java.io.File(rootPath), "");

            // Recherche des contrôleurs
            for (Class<?> clazz : toutesLesClasses) {

                if (clazz.isAnnotationPresent(org.springframework.stereotype.Controller.class)) {

                    classAnnote.add(clazz);

                    System.out.println("Contrôleur : " + clazz.getName());

                    Method[] methodes = clazz.getDeclaredMethods();

                    for (Method methode : methodes) {

                        if (methode.isAnnotationPresent(UrlMapping.class)) {

                            UrlMapping annotation =
                                    methode.getAnnotation(UrlMapping.class);

                            String url = annotation.value();
                            String httpMethod = annotation.method().toUpperCase();

                            UrlMethod key =
                                    new UrlMethod(url, httpMethod);

                            // Vérification des doublons
                            if (urlMappingMap.containsKey(key)) {

                                throw new ServletException(
                                        "Route déjà déclarée : "
                                                + httpMethod
                                                + " "
                                                + url);
                            }

                            urlMappingMap.put(
                                    key,
                                    new Mapping(clazz, methode));

                            System.out.println(
                                    "Route enregistrée : "
                                            + httpMethod
                                            + " "
                                            + url
                                            + " -> "
                                            + clazz.getSimpleName()
                                            + "."
                                            + methode.getName()
                                            + "()");

                        }

                    }

                }

            }

            System.out.println("-------------------------------------");
            System.out.println("Nombre de contrôleurs : " + classAnnote.size());
            System.out.println("Nombre de routes : " + urlMappingMap.size());
            System.out.println("-------------------------------------");

        } catch (Exception e) {

            throw new ServletException(e);

        }

    }

    // ============================================================
    // Scanner récursivement toutes les classes du projet
    // ============================================================

    private List<Class<?>> scanClasses(java.io.File dir,
                                       String packageName)
            throws ClassNotFoundException {

        List<Class<?>> classes = new ArrayList<>();

        java.io.File[] files = dir.listFiles();

        if (files == null) {
            return classes;
        }

        for (java.io.File file : files) {

            if (file.isDirectory()) {

                String newPackage =
                        packageName.isEmpty()
                                ? file.getName()
                                : packageName + "." + file.getName();

                classes.addAll(scanClasses(file, newPackage));

            }

            else if (file.getName().endsWith(".class")) {

                String className =
                        packageName.isEmpty()
                                ? file.getName().replace(".class", "")
                                : packageName + "."
                                        + file.getName().replace(".class", "");

                try {

                    ClassLoader loader =
                            Thread.currentThread().getContextClassLoader();

                    Class<?> clazz =
                            Class.forName(className, true, loader);

                    classes.add(clazz);

                }

                catch (ClassNotFoundException | NoClassDefFoundError e) {

                    System.out.println("Classe ignorée : " + className);

                }

            }

        }

        return classes;

    }

    // ============================================================
    // Traitement des requêtes
    // ============================================================

    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();

        String pathInfo =
                requestURI.substring(contextPath.length());

        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }

        String httpMethod =
                request.getMethod().toUpperCase();

        UrlMethod key =
                new UrlMethod(pathInfo, httpMethod);

        out.println("<h1>Mini Spring</h1>");

        out.println("<p>URL : " + pathInfo + "</p>");

        out.println("<p>Méthode HTTP : " + httpMethod + "</p>");

        if (urlMappingMap.containsKey(key)) {

            Mapping mapping =
                    urlMappingMap.get(key);

            out.println("<h2 style='color:green'>Route trouvée</h2>");

            out.println("<p>");

            out.println(mapping.getControllerClass().getName());

            out.println("<br>");

            out.println(mapping.getMethod().getName());

            out.println("</p>");

        }

        else {

            out.println("<h2 style='color:red'>Route inconnue</h2>");

            out.println("<h3>Routes disponibles :</h3>");

            for (Map.Entry<UrlMethod, Mapping> entry : urlMappingMap.entrySet()) {

                UrlMethod route = entry.getKey();
                Mapping mapping = entry.getValue();

                out.println("<p>");

                out.println(route.getMethod());

                out.println(" /");

                out.println(route.getUrl());

                out.println(" -> ");

                out.println(mapping.getControllerClass().getSimpleName());

                out.println(".");

                out.println(mapping.getMethod().getName());

                out.println("()");

                out.println("</p>");

            }

            throw new ServletException(
                    "L'URL "
                            + pathInfo
                            + " ("
                            + httpMethod
                            + ") n'est pas supportée.");

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

}