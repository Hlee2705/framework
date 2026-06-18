package com.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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

            for (Class<?> clazz : toutesLesClasses) {
                for (java.lang.annotation.Annotation annotation : clazz.getAnnotations()) {
                    if (annotation.annotationType().getName()
                            .equals("com.framework.annotation.JsonSerializable")) {

                        classAnnote.add(clazz);
                        System.out.println("Classe annotée : " + clazz.getName());
                        break;
                    }
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
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    System.out.println("Ignorée : " + className);
                }
            }
        }
        return classes;
    }

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        out.println("<h1>Mini Spring</h1>");
        out.println("<p>URL : " + request.getRequestURI() + "</p>");
        out.println("<p>Methode : " + request.getMethod() + "</p>");

        out.println("<h2>Classes annotées :</h2>");

        for (Class<?> c : classAnnote) {
            out.println("<p>" + c.getName() + "</p>");
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