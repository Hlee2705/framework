package com.framework.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.converter.ObjectToStringHttpMessageConverter;

public class ModelView {
    private String view;

    private Map<String, Object> data;

    public ModelView() {
        this.data = new HashMap<>();
    }

    public ModelView(String view) {
        this.view = view;
        this.data = new HashMap<>();
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    // Ajoute une donnée à transmettre à la vue
    public void addObject(String key, Object value){
        data.put(key, value);
    }
}
