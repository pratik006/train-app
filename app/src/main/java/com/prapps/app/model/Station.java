package com.prapps.app.model;

/**
 * Created by pratik on 19/2/17.
 */

public class Station {
    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return code;
    }
}
