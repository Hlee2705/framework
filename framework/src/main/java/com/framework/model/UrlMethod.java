package com.framework.model;

import java.util.Objects;

public class UrlMethod {
    private String url;
    private String method;

    public UrlMethod() {
    }

    public UrlMethod(String url, String method){
        this.url = url:
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    // Surcharger equals() : deux UrlMethod sont égaux si leur URL et leur mméthode
    // HTTP sont identiques
    public boolean equals(Object obj){
        if(this == obj)
            return true;

        if(obj == null)
            return false;

        if(getClass() != obj.getClass())
            return false;

        UrlMethod other = (UrlMethod) obj;

        return Objects.equals(url, other.url) && Objects.equals(method, other.method)
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override
    public String toString() {
        return method + " " + url;
    }
}
