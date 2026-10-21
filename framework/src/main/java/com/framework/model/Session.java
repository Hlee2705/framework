package com.framework.model;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private Map<String, Object> valeurs = new HashMap<>();

    public Session(Map<String, Object> valeurs) {
        this.valeurs = valeurs;
    }

    public Map<String, Object> getValeurs() {
        return valeurs;
    }

    public void setValeurs(Map<String, Object> valeurs) {
        this.valeurs = valeurs;
    }

    public void setAttribute(String nom, Object valeur){
        valeurs.put(nom, valeur);
    }

    public Object getAttribute(String nom){
        return valeurs.get(nom);
    }

    public void removeAttribute(String nom){
        valeurs.remove(nom);
    }

    public boolean contientAttribute(String nom){
        return valeurs.containsKey(nom);
    }

}
