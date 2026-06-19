package com.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ModuleLayer.Controller;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.framework.annotation.JsonSerializable;

public class FrontControllerServlet extends HttpServlet {

    private List<Class<?>> classAnnote = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        System.out.println("=== INIT FRAMEWORK ===");

        try {
            String rootPath = getServletContext().getRealPath("/WEB-INF/classes");
            if (rootPath == null)
                throw new ServletException("Chemin WEB-INF/classes introuvable");

            List<Class<?>> toutesLesClasses = scanClasses(new java.io.File(rootPath), "");

            // Utilisation directe de isAnnotationPresent pour plus de robustesse
            for (Class<?> clazz : toutesLesClasses) {
                if (clazz.isAnnotationPresent(org.springframework.stereotype.Controller.class)) {
                    classAnnote.add(clazz);
                    System.out.println("Classe annotée : " + clazz.getName());
                }
            }

            System.out.println("Total annotées = " + classAnnote.size());

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
                    // Utilisation du ClassLoader du thread actuel pour éviter les échecs de
                    // chargement latents
                    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                    classes.add(Class.forName(className, true, contextClassLoader));
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    System.out.println("Ignorée : " + className);
                }
            }
        }
        return classes;
    }

    // protected void processRequest(HttpServletRequest request,
    // HttpServletResponse response)
    // throws ServletException, IOException {

    // response.setContentType("text/html");

    // PrintWriter out = response.getWriter();

    // out.println("<h1>Mini Spring</h1>");
    // out.println("<p>URL : " + request.getRequestURI() + "</p>");
    // out.println("<p>Methode : " + request.getMethod() + "</p>");

    // out.println("<h2>Classes annotées :</h2>");

    // for (Class<?> c : classAnnote) {
    // out.println("<p>" + c.getName() + "</p>");
    // }
    // }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<h1>Mini Spring</h1>");
        out.println("<p>URL : " + request.getRequestURI() + "</p>");
        out.println("<p>Methode : " + request.getMethod() + "</p>");

        // 1. Log de contrôle pour voir si cette ligne s'exécute
        out.println("<h2>Classes annotées (Taille de la liste : " + classAnnote.size() + ") :</h2>");

        if (classAnnote.isEmpty()) {
            out.println(
                    "<p style='color:red;'>Aucune classe n'a été trouvée dans la liste lors de l'initialisation.</p>");
        }

        for (Class<?> c : classAnnote) {
            out.println("<p>Trouvée : " + c.getName() + "</p>");
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