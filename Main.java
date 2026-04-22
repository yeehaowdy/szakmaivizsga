import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

// Adatosztály a látogatások tárolására
class Latogatas {
    public String nev;
    public String leiras;
    public double idotartam;
    public String muzeum;
    public String varos;
    public String tipus;

    public Latogatas(String sor) {
        // A CSV fájlban az adatok idézojelek között is lehetnek, 
        // egy egyszerű split("," , -1) helyett érdemes a felesleges karaktereket letisztítani
        String[] s = sor.split(",");
        this.nev = s[0].replace("\"", "");
        this.leiras = s[1].replace("\"", "");
        this.idotartam = Double.parseDouble(s[2]);
        this.muzeum = s[3].replace("\"", "");
        this.varos = s[4].replace("\"", "");
        this.tipus = s[5].replace("\"", "");
    }
}

public class Main {

    public static void main(String[] args) {
        List<Latogatas> latogatasok = new ArrayList<>();

        // 1. Feladat: Beolvasás [cite: 3]
        try {
            List<String> sorok = Files.readAllLines(Paths.get("exhibitions.csv"), StandardCharsets.UTF_8);
            // A fejlécet (elso sor) kihagyjuk
            for (int i = 1; i < sorok.size(); i++) {
                latogatasok.add(new Latogatas(sorok.get(i)));
            }
            System.out.println("Az exhibitions.csv fájlból " + latogatasok.size() + " látogatás adata beolvasva"); // [cite: 13]
        } catch (IOException e) {
            System.err.println("Hiba a fájl beolvasásakor: " + e.getMessage());
            return;
        }

        // 2. Feladat: Leghosszabb látogatás [cite: 4]
        Latogatas maxLatogatas = latogatasok.get(0);
        for (Latogatas l : latogatasok) {
            if (l.idotartam > maxLatogatas.idotartam) {
                maxLatogatas = l;
            }
        }
        System.out.println("A leghosszabb látogatás: " + maxLatogatas.nev + " - " + maxLatogatas.muzeum + ": " + maxLatogatas.idotartam + " óra");

        // 3. Feladat: Guided tour típusúak, csökkeno sorrendben [cite: 5]
        List<Latogatas> guidedTours = new ArrayList<>();
        for (Latogatas l : latogatasok) {
            if (l.tipus.equalsIgnoreCase("guided tour")) {
                guidedTours.add(l);
            }
        }
        // Rendezés csökkeno sorrendbe idotartam szerint
        guidedTours.sort((a, b) -> Double.compare(b.idotartam, a.idotartam));

        System.out.println("A guided tour típusú látogatások (idotartam szerint csökkenoben):");
        for (Latogatas gt : guidedTours) {
            System.out.println(gt.nev + " – " + gt.muzeum + " (" + gt.idotartam + " óra)");
        }

        // 4. Feladat: Különbözo múzeumok száma és véletlenszerű választás [cite: 6, 7]
        Set<String> muzeumok = new HashSet<>();
        for (Latogatas l : latogatasok) {
            muzeumok.add(l.muzeum);
        }
        System.out.println("Összesen " + muzeumok.size() + " különbözo múzeum található a fájlban");

        List<String> muzeumLista = new ArrayList<>(muzeumok);
        Random r = new Random();
        String veletlenMuzeum = muzeumLista.get(r.nextInt(muzeumLista.size()));

        System.out.println("Közülük egy véletlen kiválasztott: " + veletlenMuzeum + ", látogatói:");
        for (Latogatas l : latogatasok) {
            if (l.muzeum.equals(veletlenMuzeum)) {
                System.out.println(l.nev);
            }
        }

        // 5. Feladat: Legtöbb szóból álló múzeumnév [cite: 8]
        String leghosszabbNevuMuzeum = "";
        int maxSzo = 0;
        for (String mNev : muzeumok) {
            int aktualisSzoSzam = szavakSzama(mNev);
            if (aktualisSzoSzam > maxSzo) {
                maxSzo = aktualisSzoSzam;
                leghosszabbNevuMuzeum = mNev;
            }
        }
        System.out.println("Legtöbb szóból álló múzeumnév: " + leghosszabbNevuMuzeum);

        // 6. Feladat: Városonkénti statisztika (ABC sorrend) [cite: 9, 10]
        Map<String, Integer> varosStat = new TreeMap<>(); // A TreeMap automatikusan ABC sorrendbe teszi a kulcsokat
        for (Latogatas l : latogatasok) {
            varosStat.put(l.varos, varosStat.getOrDefault(l.varos, 0) + 1);
        }

        System.out.print("Látogatások száma városonként: ");
        int szamlalo = 0;
        for (Map.Entry<String, Integer> entry : varosStat.entrySet()) {
            System.out.print(entry.getKey() + " (" + entry.getValue() + ")");
            szamlalo++;
            if (szamlalo < varosStat.size()) {
                System.out.print(", "); // Az utolsó után nem teszünk vesszot [cite: 10]
            }
        }
        System.out.println();

        // 7. Feladat: 2 óránál rövidebbek mentése [cite: 11]
        try (PrintWriter iro = new PrintWriter(new FileWriter("rovid_latogatasok.txt", StandardCharsets.UTF_8))) {
            for (Latogatas l : latogatasok) {
                if (l.idotartam < 2.0) {
                    iro.println(l.nev + " – " + l.muzeum + ": " + l.idotartam + " óra");
                }
            }
            System.out.println("A 2 óránál rövidebb látogatások adatai kiírva a rovid_latogatasok.txt fájlba");
        } catch (IOException e) {
            System.err.println("Hiba a fájlba íráskor: " + e.getMessage());
        }
    }

    // Segédfüggvény a szavak számának meghatározásához [cite: 8]
    public static int szavakSzama(String muzeumNev) {
        if (muzeumNev == null || muzeumNev.isEmpty()) return 0;
        return muzeumNev.trim().split("\\s+").length;
    }
}