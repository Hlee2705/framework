package com.framework.model;

import java.io.InvalidObjectException;
import java.lang.reflect.Method;

// permet de lier une classe et une méthode spécifique pour une url donnée
public class Mapping {
    private Class<?> controllerClass;
    private Method method;

    public Mapping(Class<?> controllerClass, Method method) {
        this.controllerClass = controllerClass;
        this.method = method;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getMethod() {
        return method;
    }

}
