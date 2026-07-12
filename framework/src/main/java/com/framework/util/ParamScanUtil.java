package com.framework.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.framework.annotation.UrlMapping;
import com.framework.model.Mapping;
import com.framework.model.UrlMethod;

public class ParamScanUtil {

    private Map<UrlMethod, Mapping> mappings =
            new HashMap<>();

    public Map<UrlMethod, Mapping> getMappings() {
        return mappings;
    }

    public void scan(String packageName) throws Exception {

        String path =
                packageName.replace(".", "/");

        ClassLoader loader =
                Thread.currentThread().getContextClassLoader();

        File directory =
                new File(loader.getResource(path).toURI());

        List<Class<?>> classes =
                scan(directory, packageName);

        for (Class<?> clazz : classes) {

            if (!clazz.isAnnotationPresent(Controller.class))
                continue;

            for (Method method :
                    clazz.getDeclaredMethods()) {

                if (method.isAnnotationPresent(UrlMapping.class)) {

                    UrlMapping annotation =
                            method.getAnnotation(UrlMapping.class);

                    UrlMethod key =
                            new UrlMethod(
                                    annotation.value(),
                                    annotation.method());

                    mappings.put(
                            key,
                            new Mapping(clazz, method));

                    System.out.println(key + " -> "
                            + clazz.getSimpleName()
                            + "."
                            + method.getName());
                }
            }
        }
    }

    private List<Class<?>> scan(File dir,
                                String packageName)
            throws Exception {

        List<Class<?>> classes =
                new ArrayList<>();

        File[] files = dir.listFiles();

        if (files == null)
            return classes;

        for (File file : files) {

            if (file.isDirectory()) {

                classes.addAll(
                        scan(
                                file,
                                packageName + "."
                                        + file.getName()));

            } else if (file.getName().endsWith(".class")) {

                String className =
                        packageName + "."
                                + file.getName()
                                .replace(".class", "");

                classes.add(Class.forName(className));
            }
        }

        return classes;
    }
}