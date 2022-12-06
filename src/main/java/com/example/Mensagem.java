package com.example;

public class Mensagem {

    public String user;
    public String message;

    public Mensagem() {
    }

    @Override
    public String toString() {
        return "Mensagem{" +
               "user='" + user + '\'' +
               ", message='" + message + '\'' +
               '}';
    }
}
