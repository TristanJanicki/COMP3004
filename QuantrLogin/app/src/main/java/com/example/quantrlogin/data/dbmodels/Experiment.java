package com.example.quantrlogin.data.dbmodels;

public class Experiment implements java.io.Serializable{
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
