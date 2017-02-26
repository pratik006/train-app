package com.prapps.app.model;

/**
 * Created by pratik on 24/2/17.
 */

public class KeyValue<T> {
    private String key;
    private T value;

    public KeyValue(){}

    public KeyValue(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
