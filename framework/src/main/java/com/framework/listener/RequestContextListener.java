package com.framework.listener;

import java.util.Map;

import com.framework.model.Mapping;
import com.framework.model.UrlMethod;
import com.framework.util.ParamScanUtil;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class RequestContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {

            String packageName =
                    sce.getServletContext()
                            .getInitParameter("controllerPackage");

            ParamScanUtil scanner = new ParamScanUtil();

            scanner.scan(packageName);

            Map<UrlMethod, Mapping> mappings =
                    scanner.getMappings();

            sce.getServletContext()
                    .setAttribute("globalMappings", mappings);

            System.out.println("================================");
            System.out.println("Scan terminé");
            System.out.println("Mappings trouvés : " + mappings.size());
            System.out.println("================================");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}