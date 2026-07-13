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

        mappings = (Map<UrlMethod, Mapping>)
                getServletContext().getAttribute("globalMappings");

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

            // Exécution
            Object result = method.invoke(controller);

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
                String view =
                        PREFIX
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