import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class Main {

    // --- Adatmodell ---
    static class Attempt {
        int attemptId;
        int personId;
        String firstName;
        String lastName;
        String birthday;
        int certificationId;
        String certificationName;
        String providerName;
        String reputationStars;
        String minPercentageToPass;
        String costInUsd;
        String startDatetime;
        String endDatetime;
        String percentage;   // lehet üres
        String isPassed;     // "True", "False", vagy üres

        public Attempt(String[] parts) {
            attemptId           = Integer.parseInt(parts[0].trim());
            personId            = Integer.parseInt(parts[1].trim());
            firstName           = parts[2].trim();
            lastName            = parts[3].trim();
            birthday            = parts[4].trim();
            certificationId     = Integer.parseInt(parts[5].trim());
            certificationName   = parts[6].trim();
            providerName        = parts[7].trim();
            reputationStars     = parts[8].trim();
            minPercentageToPass = parts[9].trim();
            costInUsd           = parts[10].trim();
            startDatetime       = parts[11].trim();
            endDatetime         = parts[12].trim();
            percentage          = parts[13].trim();
            isPassed            = parts[14].trim();
        }

        String fullName() {
            return firstName + " " + lastName;
        }

        boolean passed() {
            return "True".equalsIgnoreCase(isPassed);
        }

        boolean failed() {
            return "False".equalsIgnoreCase(isPassed);
        }
    }

    // --- Beolvasás ---
    static List<Attempt> readCsv(String path) throws IOException {
        List<Attempt> list = new ArrayList<>();
        List<String> lines = Files.readAllLines(Path.of(path));
        boolean firstLine = true;
        for (String line : lines) {
            if (firstLine) { firstLine = false; continue; } // fejléc kihagyása
            if (line.isBlank()) continue;
            String[] parts = line.split(",", -1);
            if (parts.length >= 15) {
                list.add(new Attempt(parts));
            }
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        String csvPath = "get-certified.csv";
        List<Attempt> attempts = readCsv(csvPath);

        // ============================
        // 1. feladat – Beolvasott sorok
        // ============================
        System.out.println("=== 1. feladat ===");
        System.out.println("Beolvasott sorok száma: " + attempts.size());
        System.out.println();

        // ============================
        // 2. feladat – Leghosszabb nevű tanúsítvány
        // ============================
        System.out.println("=== 2. feladat ===");
        Attempt longest = null;
        for (Attempt a : attempts) {
            if (longest == null || a.certificationName.length() > longest.certificationName.length()) {
                longest = a;
            }
        }
        System.out.println("Leghosszabb nevű tanúsítvány:");
        System.out.println("Tanúsítvány: " + longest.certificationName);
        System.out.println("Szolgáltató: " + longest.providerName);
        System.out.println();

        // ============================
        // 3. feladat – Sikeresek, start_datetime szerint csökkenő
        // ============================
        System.out.println("=== 3. feladat ===");
        List<Attempt> passed = new ArrayList<>();
        for (Attempt a : attempts) {
            if (a.passed()) passed.add(a);
        }
        passed.sort((a, b) -> b.startDatetime.compareTo(a.startDatetime));

        System.out.println("Sikeres kísérletek (start_datetime szerint csökkenő):");
        for (Attempt a : passed) {
            System.out.println(a.fullName() + " -- " + a.certificationName + " (" + a.startDatetime + ")");
        }
        System.out.println();

        // ============================
        // 4. feladat – Sikertelen kísérletek, véletlenszerű kiválasztás
        // ============================
        System.out.println("=== 4. feladat ===");
        List<Attempt> failed = new ArrayList<>();
        for (Attempt a : attempts) {
            if (a.failed()) failed.add(a);
        }
        System.out.println("Sikertelen kísérletek száma: " + failed.size());

        Random rnd = new Random();
        Attempt randomFailed = failed.get(rnd.nextInt(failed.size()));
        System.out.println("Véletlenszerűen kiválasztott sikertelen kísérlet:");
        System.out.println(randomFailed.fullName() + " -- " + randomFailed.certificationName);
        System.out.println();

        // ============================
        // 5. feladat – Százalékos átlag, legközelebb eső kísérlet
        // ============================
        System.out.println("=== 5. feladat ===");
        List<Attempt> withPercent = new ArrayList<>();
        for (Attempt a : attempts) {
            if (!a.percentage.isEmpty()) withPercent.add(a);
        }

        double sum = 0;
        for (Attempt a : withPercent) {
            sum += Double.parseDouble(a.percentage);
        }
        double avg = sum / withPercent.size();
        System.out.printf("Százalékeredmények átlaga: %.2f%%%n", avg);

        Attempt closest = null;
        double minDiff = Double.MAX_VALUE;
        for (Attempt a : withPercent) {
            double diff = Math.abs(Double.parseDouble(a.percentage) - avg);
            if (diff < minDiff) {
                minDiff = diff;
                closest = a;
            }
        }
        System.out.println("Legközelebb eső kísérlet:");
        System.out.println("#" + closest.attemptId + " -- " + closest.fullName()
                + " -- " + closest.certificationName + " -- " + (int) Double.parseDouble(closest.percentage) + "%");
        System.out.println();

        // ============================
        // 6. feladat – Szolgáltatónként kísérletek száma, növekvő sorrend, tab elválasztással
        // ============================
        System.out.println("=== 6. feladat ===");
        Map<String, Integer> providerCount = new LinkedHashMap<>();
        for (Attempt a : attempts) {
            providerCount.merge(a.providerName, 1, Integer::sum);
        }

        // Rendezés növekvő darabszám szerint
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(providerCount.entrySet());
        sorted.sort(Map.Entry.comparingByValue());

        for (int i = 0; i < sorted.size(); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            if (i < sorted.size() - 1) {
                System.out.print(entry.getKey() + "\t" + entry.getValue() + "\t");
            } else {
                System.out.print(entry.getKey() + "\t" + entry.getValue()); // utolsó után nincs tab
            }
            System.out.println();
        }
        System.out.println();

        // ============================
        // 7. feladat – Egyedi tanúsítványnevek mentése certek.txt-be
        // ============================
        System.out.println("=== 7. feladat ===");
        Set<String> certNames = new LinkedHashSet<>();
        for (Attempt a : attempts) {
            certNames.add(a.certificationName);
        }

        try (PrintWriter pw = new PrintWriter("certek.txt")) {
            for (String name : certNames) {
                pw.println(name);
            }
        }
        System.out.println("Egyedi tanúsítványnevek mentve → certek.txt (" + certNames.size() + " bejegyzés)");
    }
}