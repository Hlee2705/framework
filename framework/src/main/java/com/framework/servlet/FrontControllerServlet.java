package com.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

import com.framework.model.Mapping;
import com.framework.model.UrlMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {

    private Map<UrlMethod, Mapping> mappings;

    @Override
    public void init() throws ServletException {

        mappings =
                (Map<UrlMethod, Mapping>)
                        getServletContext()
                                .getAttribute("globalMappings");

        if (mappings == null) {
            throw new ServletException(
                    "Le Listener n'a pas chargé les mappings.");
        }

        System.out.println("FrontController initialisé.");
    }

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp)
            throws ServletException, IOException {

        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        String uri = request.getRequestURI();

        String context = request.getContextPath();

        String url = uri.substring(context.length());

        if (url.startsWith("/")) {
            url = url.substring(1);
        }

        String httpMethod =
                request.getMethod().toUpperCase();

        UrlMethod key =
                new UrlMethod(url, httpMethod);

        out.println("<h1>Mini Spring</h1>");
        out.println("<p>URL : " + url + "</p>");
        out.println("<p>Méthode HTTP : " + httpMethod + "</p>");

        Mapping mapping = mappings.get(key);

        if (mapping == null) {

            out.println("<h2 style='color:red'>Route inconnue</h2>");

            out.println("<h3>Routes disponibles :</h3>");

            for (Map.Entry<UrlMethod, Mapping> entry :
                    mappings.entrySet()) {

                out.println("<p>");

                out.println(entry.getKey());

                out.println(" -> ");

                out.println(entry.getValue()
                        .getControllerClass()
                        .getSimpleName());

                out.println(".");

                out.println(entry.getValue()
                        .getMethod()
                        .getName());

                out.println("()");

                out.println("</p>");
            }

            return;
        }

        try {

            Object controller =
                    mapping.getControllerClass()
                            .getDeclaredConstructor()
                            .newInstance();

            Method method = mapping.getMethod();

            Object result =
                    method.invoke(controller);

            out.println("<h2 style='color:green'>Route trouvée</h2>");

            out.println("<p>");

            out.println(mapping.getControllerClass().getName());

            out.println("<br>");

            out.println(method.getName());

            out.println("</p>");

            out.println("<p>Résultat : " + result + "</p>");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}