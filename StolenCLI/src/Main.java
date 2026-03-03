import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static List<Stolen> records = new ArrayList<>();

    public static void main(String[] args) {
        readCsv("stolen.csv");
        System.out.println("1) Beolvasott rekordok száma: " + records.size());

        System.out.println("\n2) Legértékesebb lopás:");
        records.stream()
                .filter(r -> r.isStolen && r.price != null)
                .max(Comparator.comparingInt(r -> r.price))
                .ifPresent(r -> System.out.printf("[%d] %s | Ár: %d | Lopás: igen\nTulajdonos: %s (%s)\nTolvaj: %s\n",
                        r.assetId, r.assetName, r.price, r.getOwnerFullName(), r.ownerBirthday, r.getThiefFullName()));

        // 3. Feladat
        System.out.println("\n3) Ismert tolvajjal megadott lopások (tulajdonos születésnap szerint csökkenő):");
        records.stream()
                .filter(r -> r.isStolen && r.thiefId != null)
                .sorted((r1, r2) -> r2.ownerBirthday.compareTo(r1.ownerBirthday))
                .forEach(r -> System.out.printf("[%d] %s | Ár: %s Lopás: igen\nTulajdonos: %s (%s)\nTolvaj: %s\n",
                        r.assetId, r.assetName, (r.price == null ? "N/A" : r.price),
                        r.getOwnerFullName(), r.ownerBirthday, r.getThiefFullName()));

        // 4. Feladat
        Set<String> allNames = new HashSet<>();
        records.forEach(r -> {
            allNames.add(r.getOwnerFullName());
            if (r.getThiefFullName() != null) allNames.add(r.getThiefFullName());
        });
        System.out.println("\n4) Egyedi nevek száma: " + allNames.size());

        List<String> nameList = new ArrayList<>(allNames);
        String randomName = nameList.get(new Random().nextInt(nameList.size()));
        System.out.println("Kiválasztott név (véletlenszerű): " + randomName);

        boolean wasThief = records.stream().anyMatch(r -> randomName.equals(r.getThiefFullName()));
        boolean wasVictim = records.stream().anyMatch(r -> randomName.equals(r.getOwnerFullName()) && r.isStolen);
        System.out.println("-> Lopott-e: " + (wasThief ? "Igen" : "Nem"));
        System.out.println("-> Loptak-e tőle: " + (wasVictim ? "Igen" : "Nem"));

        // 5. Feladat
        System.out.println("\n5) A legtöbb szóból álló terméknév:");
        records.stream()
                .max(Comparator.comparingInt(r -> countWords(r.assetName)))
                .ifPresent(r -> System.out.printf("Név: \"%s\" Szavak száma: %d\n", r.assetName, countWords(r.assetName)));

        // 6. Feladat
        System.out.println("\n6) Lopások személyenként (születési év szerint növekvő):");
        Map<Integer, List<String>> stats = new TreeMap<>(); // Év szerinti rendezéshez

        Map<String, Long> thiefCounts = records.stream()
                .filter(r -> r.getThiefFullName() != null)
                .collect(Collectors.groupingBy(Stolen::getThiefFullName, Collectors.counting()));

        records.stream()
                .filter(r -> r.getThiefFullName() != null)
                .distinct()
                .forEach(r -> {
                    int year = (r.thiefBirthday != null) ? r.thiefBirthday.getYear() : 0;
                    if(year > 0) {
                        stats.computeIfAbsent(year, k -> new ArrayList<>())
                                .add(r.getThiefFullName() + " (elkövetett lopások: " + thiefCounts.get(r.getThiefFullName()) + ")");
                    }
                });

        stats.forEach((year, people) -> {
            System.out.println(year);
            System.out.println(String.join(", ", people));
        });

        // 7. Feladat
        saveShorts("rovidek.txt", thiefCounts);
    }

    // 5. Feladat
    public static int countWords(String text) {
        if (text == null || text.isEmpty()) return 0;
        return text.trim().split("\\s+").length;
    }

    private static void readCsv(String path) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(path))) {
            String line = br.readLine(); // Fejléc átugrása
            while ((line = br.readLine()) != null) {
                String[] v = line.split(",", -1);
                Stolen r = new Stolen();
                r.assetId = Integer.parseInt(v[0]);
                r.assetName = v[1];
                r.price = v[2].isEmpty() ? null : Integer.parseInt(v[2]);
                r.isStolen = v[3].equals("1");
                r.ownerId = Integer.parseInt(v[4]);
                r.ownerFirstName = v[5];
                r.ownerLastName = v[6];
                r.ownerBirthday = LocalDate.parse(v[7]);
                if (!v[8].isEmpty()) {
                    r.thiefId = Integer.parseInt(v[8]);
                    r.thiefFirstName = v[9];
                    r.thiefLastName = v[10];
                    if (!v[11].isEmpty()) r.thiefBirthday = LocalDate.parse(v[11]);
                }
                records.add(r);
            }
        } catch (IOException e) {
            System.err.println("Hiba a fájl beolvasásakor!");
        }
    }

    private static void saveShorts(String path, Map<String, Long> counts) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            int lines = 0;
            for (Stolen r : records) {
                if (r.getThiefFullName() != null && counts.get(r.getThiefFullName()) == 1) {
                    pw.println(r.thiefId + ", " + r.thiefFirstName + ", " + r.thiefLastName + (r.thiefBirthday != null ? ", " + r.thiefBirthday : ""));
                    lines++;
                }
            }
            System.out.println("\n7) A " + path + " fájl elkészült, benne " + lines + " sorral.");
        } catch (IOException e) {
            System.err.println("Hiba a mentéskor!");
        }
    }
}