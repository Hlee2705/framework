package com.framework.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import com.framework.model.Mapping;
import com.framework.model.ModelView;
import com.framework.model.UrlMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontControllerServlet extends HttpServlet {

    private Map<UrlMethod, Mapping> mappings;

    // Préfixe et suffixe des vues
    private static final String PREFIX = "/WEB-INF/views/";
    private static final String SUFFIX = ".jsp";

    @Override
    public void init() throws ServletException {

        mappings = (Map<UrlMethod, Mapping>) getServletContext().getAttribute("globalMappings");

        if (mappings == null) {
            throw new ServletException(
                    "Le Listener n'a pas chargé les mappings.");
        }

        System.out.println("================================");
        System.out.println("FrontController initialisé");
        System.out.println("Nombre de routes : " + mappings.size());
        System.out.println("================================");
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
            throw new ServletException(
                    "Aucun mapping trouvé pour "
                            + httpMethod
                            + " "
                            + url);
        }

        try {

            // Création du contrôleur
            Object controller = mapping.getControllerClass()
                    .getDeclaredConstructor()
                    .newInstance();

            // Méthode à appeler
            Method method = mapping.getMethod();

            // afficher les parametres detectes : recuperation des parametres
            java.lang.reflect.Parameter[] parameters = method.getParameters();

            Object[] arguments = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                java.lang.reflect.Parameter parameter = parameters[i];

                String nomParametre = parameter.getName();

                String valeur = request.getParameter(nomParametre);

                Class<?> type = parameter.getType();

                Object valeurConvertie = valeur;

                // String
                if (type == String.class) {
                    valeurConvertie = valeur;
                }

                // int / Integer
                else if (type == int.class || type == Integer.class) {
                    valeurConvertie = Integer.parseInt(valeur);
                }

                // long / Long
                else if (type == long.class || type == Long.class) {
                    valeurConvertie = Long.parseLong(valeur);
                }

                // double / Double
                else if (type == double.class || type == Double.class) {
                    valeurConvertie = Double.parseDouble(valeur);
                }

                // float / Float
                else if (type == float.class || type == Float.class) {
                    valeurConvertie = Float.parseFloat(valeur);
                }

                // boolean / Boolean
                else if (type == boolean.class || type == Boolean.class) {
                    valeurConvertie = Boolean.parseBoolean(valeur);
                }

                // short / Short
                else if (type == short.class || type == Short.class) {
                    valeurConvertie = Short.parseShort(valeur);
                }

                // byte / Byte
                else if (type == byte.class || type == Byte.class) {
                    valeurConvertie = Byte.parseByte(valeur);
                }

                // char / Character
                else if (type == char.class || type == Character.class) {
                    valeurConvertie = valeur.charAt(0);
                }

                arguments[i] = valeurConvertie;

                System.out.println("Parametre : " + parameter.getName());
                System.out.println("Type : " + parameter.getType().getSimpleName());
                System.out.println("Valeur reçue : " + valeur);
                System.out.println("Valeur convertie : " + valeurConvertie);
            }

            // Exécution
            Object result = method.invoke(controller, arguments);

            // ----------- Cas 1 : la méthode retourne un ModelView ----------
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

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}