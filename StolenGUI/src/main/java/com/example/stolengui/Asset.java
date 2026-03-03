package com.example.stolengui;

import java.time.LocalDate;

public class Asset {
    public int id;
    public String name;
    public Integer price;
    public boolean isStolen;
    public String ownerName;
    public String thiefName;

    public Asset(String[] v) {
        this.id = Integer.parseInt(v[0]);
        this.name = v[1];
        this.price = v[2].isEmpty() ? null : Integer.parseInt(v[2]);
        this.isStolen = v[3].equals("1");
        this.ownerName = v[5] + " " + v[6];
        this.thiefName = v[8].isEmpty() ? "Ismeretlen" : v[9] + " " + v[10];
    }

    @Override
    public String toString() { return name; }
}