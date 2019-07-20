package com.test;

import java.io.Serializable;
import java.util.Map;

public class Episode implements Serializable{
    
    //private static final long serialVersionUID = 6471755900005055152L;

    String name;

    Map<String,String> series;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getSeries() {
        return series;
    }

    public void setSeries(Map<String, String> series) {
        this.series = series;
    }

    
    
}