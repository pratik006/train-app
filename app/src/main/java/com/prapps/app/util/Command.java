package com.prapps.app.util;

/**
 * Created by pratik on 24/2/17.
 */

public interface Command<T> {
    void execute(T t);
}
