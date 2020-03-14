package com.example.quantrlogin.data.dbmodels;

import java.io.Serializable;

public class Experiment implements Serializable {
    private String id;

    public Experiment(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
