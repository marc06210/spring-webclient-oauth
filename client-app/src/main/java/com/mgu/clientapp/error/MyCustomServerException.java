package com.mgu.clientapp.error;

public class MyCustomServerException extends Throwable {

    public MyCustomServerException() {
        super(">>> error no data");
    }

    public MyCustomServerException(String message) {
        super(">>> " + message);
    }
}