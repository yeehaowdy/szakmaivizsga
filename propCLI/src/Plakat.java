import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        // A dátum formátuma a CSV-ben: yyyy.MM.dd
        this.datum = LocalDate.parse(adatok[1].replace(".", "-"));
        this.part = adatok[2];
        this.szelesseg = Double.parseDouble(adatok[3].replace(",", "."));
        this.hosszusag = Double.parseDouble(adatok[4].replace(",", "."));
        this.szoveg = adatok[5];
    }

    public double getTerulet() { return szelesseg * hosszusag; }
    public int getSzoSzam() { return szoveg.split("\\s+").length; }

    // Getters
    public int getSorszam() { return sorszam; }
    public LocalDate getDatum() { return datum; }
    public String getPart() { return part; }
    public double getSzelesseg() { return szelesseg; }
    public double getHosszusag() { return hosszusag; }
    public String getSzoveg() { return szoveg; }

    @Override
    public String toString() {
        return String.format("sorszám: %d\nkihelyezés dátuma: %s\npárt: %s\nszélesség m: %.1f\nhosszúság m: %.1f\nterület m2: %.1f\nszöveg: %s",
                sorszam, datum, part, szelesseg, hosszusag, getTerulet(), szoveg);
    }
}