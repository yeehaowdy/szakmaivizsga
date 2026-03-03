package com.example.propgui;

import java.time.LocalDate;

public class Plakat {
    private int sorszam;
    private LocalDate datum;
    private String part;
    private double szelesseg;
    private double hosszusag;
    private String szoveg;

    public Plakat(String sor) {
        String[] adatok = sor.split(";");
        this.sorszam = Integer.parseInt(adatok[0]);
        this.datum = LocalDate.parse(adatok[1].replace(".", "-"));
        this.part = adatok[2];
        this.szelesseg = Double.parseDouble(adatok[3].replace(",", "."));
        this.hosszusag = Double.parseDouble(adatok[4].replace(",", "."));
        this.szoveg = adatok[5];
    }

    // Getters
    public LocalDate getDatum() { return datum; }
    public String getPart() { return part; }

    @Override
    public String toString() {
        return String.format("%d | %s | %s | %s", sorszam, datum, part, szoveg);
    }
}