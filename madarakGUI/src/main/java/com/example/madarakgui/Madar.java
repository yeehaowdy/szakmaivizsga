package com.example.madarakgui;

public class Madar {
    private String magyarNev;
    private String latinNev;
    private int suly;

    public Madar(String magyarNev, String latinNev, int suly) {
        this.magyarNev = magyarNev;
        this.latinNev = latinNev;
        this.suly = suly;
    }

    public String getMagyarNev() { return magyarNev; }
    public String getLatinNev() { return latinNev; }
    public int getSuly() { return suly; }

    @Override
    public String toString() {
        return magyarNev + " (" + latinNev + "): " + suly + " g";
    }
}

