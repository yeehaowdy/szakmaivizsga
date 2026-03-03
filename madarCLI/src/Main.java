//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

class Madar {
    private String magyarNev;
    private String latinNev;
    private int sulyGramm;
    private int magassagCm;
    private int tavolsagKm;

    public Madar(String magyarNev, String latinNev, int sulyGramm, int magassagCm, int tavolsagKm) {
        this.magyarNev = magyarNev;
        this.latinNev = latinNev;
        this.sulyGramm = sulyGramm;
        this.magassagCm = magassagCm;
        this.tavolsagKm = tavolsagKm;
    }

    public String getMagyarNev() {
        return magyarNev;
    }

    public String getLatinNev() {
        return latinNev;
    }

    public int getSulyGramm() {
        return sulyGramm;
    }

    public int getMagassagCm() {
        return magassagCm;
    }

    public int getTavolsagKm() {
        return tavolsagKm;
    }

    @Override
    public String toString() {
        return magyarNev + " " + latinNev + " " + sulyGramm + " g " + magassagCm + " cm " + tavolsagKm + " km";
    }
}

public class Main {

    public static void main(String[] args) {
        String fajlNev = "madarak.csv";
        List<Madar> madarak = new ArrayList<>();


        try (BufferedReader br = Files.newBufferedReader(Paths.get(fajlNev), StandardCharsets.UTF_8)) {
            String sor = br.readLine();
            while ((sor = br.readLine()) != null) {
                if (sor.trim().isEmpty()) continue;
                String[] mezok = sor.split(";");
                String magyar = mezok[0].trim();
                String latin = mezok[1].trim();
                int suly = Integer.parseInt(mezok[2].trim());
                int magassag = Integer.parseInt(mezok[3].trim());
                int tav = Integer.parseInt(mezok[4].trim());
                madarak.add(new Madar(magyar, latin, suly, magassag, tav));
            }
        } catch (IOException e) {
            System.out.println("Hiba a fajl beolvasasa soran: " + e.getMessage());
            return;
        }


        System.out.println("1 A " + fajlNev + " fajlbol " + madarak.size() + " madar adata beolvasva");


        Madar legmesszebb = madarak.stream()
                .max(Comparator.comparingInt(Madar::getTavolsagKm))
                .orElse(null);
        if (legmesszebb != null) {
            System.out.println("2 Legmesszebb " + legmesszebb.getTavolsagKm()
                    + " km repulo " + legmesszebb.getMagyarNev());
        }

        List<Madar> kicsik = madarak.stream()
                .filter(m -> m.getSulyGramm() < 100)
                .collect(Collectors.toList());
        long dbKicsi = kicsik.size();
        double atlagTav = 0;
        if (!kicsik.isEmpty()) {
            atlagTav = kicsik.stream()
                    .mapToInt(Madar::getTavolsagKm)
                    .average()
                    .orElse(0);
        }
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println("3 A 100g alatti madarak " + dbKicsi
                + " darab atlagos repulesi tavolsaga "
                + df.format(atlagTav).replace('.', ',') + " km");


        List<Madar> egySzavas = madarak.stream()
                .filter(m -> m.getMagyarNev().trim().split("\\s+").length == 1)
                .collect(Collectors.toList());
        if (!egySzavas.isEmpty()) {
            Random rand = new Random();
            Madar veletlen = egySzavas.get(rand.nextInt(egySzavas.size()));
            System.out.println("4 Veletlen valasztott egyetlen szobol allo magyar nevu madar "
                    + veletlen.getMagyarNev());
        } else {
            System.out.println("4 Nincs egyetlen szobol allo magyar nevu madar az adatokban.");
        }

        System.out.println("5 A latin nev ket egyforma szobol all");
        for (Madar m : madarak) {
            String[] szavak = m.getLatinNev().trim().split("\\s+");
            if (szavak.length == 2) {
                String s1 = szavak[0].toLowerCase(Locale.ROOT);
                String s2 = szavak[1].toLowerCase(Locale.ROOT);
                if (s1.equals(s2)) {
                    System.out.println("- " + m.getMagyarNev() + " " + m.getLatinNev());
                }
            }
        }
        Map<Integer, Long> magassagCsoport = madarak.stream()
                .collect(Collectors.groupingBy(Madar::getMagassagCm, TreeMap::new, Collectors.counting()));

        System.out.print("6 Magassagok ");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Long> e : magassagCsoport.entrySet()) {
            if (e.getValue() > 1) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(e.getKey()).append(" cm ").append(e.getValue());
            }
        }
        System.out.println(sb.toString());

        long fecskek = madarak.stream()
                .filter(m -> m.getMagyarNev().toLowerCase(Locale.ROOT).contains("fecske"))
                .count();
        System.out.println("7 Osszesen " + fecskek + " fele fecske talalhato az adatok kozott");

        String kimenetiFajl = "nagyok.txt";
        try (FileWriter fw = new FileWriter(kimenetiFajl, StandardCharsets.UTF_8)) {
            for (Madar m : madarak) {
                if (m.getSulyGramm() > 500) {
                    fw.write(m.getMagyarNev() + ";" + m.getLatinNev() + ";"
                            + m.getSulyGramm() + ";" + m.getMagassagCm() + ";"
                            + m.getTavolsagKm() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println("Hiba a nagyok.txt irasanal: " + e.getMessage());
        }
        System.out.println("8 A nagy madarak adatai a " + kimenetiFajl + " fajlba elmentve");
    }
}
