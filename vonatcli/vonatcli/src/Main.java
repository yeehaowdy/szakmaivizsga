import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

class Cleaner {
    private String name;
    private int age;
    private String category;
    private String train;

    public Cleaner(String line) {
        String[] parts = line.split(";");
        this.name = parts[0];
        this.age = Integer.parseInt(parts[1]);
        this.category = parts[2];
        this.train = parts[3];
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCategory() { return category; }
    public String getTrain() { return train; }

    @Override
    public String toString() {
        return String.format("%s (%d év, %s kategória) - %s", name, age, category, train);
    }
}

public class Main {
    public static void main(String[] args) {
        List<Cleaner> cleaners = new ArrayList<>();


        try {
            cleaners = Files.lines(Paths.get("train-cleaners.csv"))
                    .skip(1)
                    .map(Cleaner::new)
                    .collect(Collectors.toList());
            System.out.println("1. feladat: Beolvasott adatok száma: " + cleaners.size());
        } catch (IOException e) {
            System.err.println("Hiba a fájl beolvasásakor: " + e.getMessage());
        }

        cleaners.stream()
                .max(Comparator.comparingInt(c -> c.getName().length()))
                .ifPresent(c -> System.out.println("2. feladat: Leghosszabb nevű: " + c.getName() + " (" + c.getCategory() + ")"));

        System.out.println("3. feladat: 40 évnél fiatalabbak (csökkenő életkor):");
        cleaners.stream()
                .filter(c -> c.getAge() < 40)
                .sorted(Comparator.comparingInt(Cleaner::getAge).reversed())
                .forEach(c -> System.out.println("   " + c.getName() + " - " + c.getAge() + " év"));

        List<Cleaner> cCategory = cleaners.stream()
                .filter(c -> "C".equals(c.getCategory()))
                .collect(Collectors.toList());

        Set<String> uniqueCNames = cCategory.stream().map(Cleaner::getName).collect(Collectors.toSet());
        System.out.println("4. feladat: C kategóriás takarítók száma: " + uniqueCNames.size());

        if (!uniqueCNames.isEmpty()) {
            String randomName = new ArrayList<>(uniqueCNames).get(new Random().nextInt(uniqueCNames.size()));
            String trains = cleaners.stream()
                    .filter(c -> c.getName().equals(randomName))
                    .map(Cleaner::getTrain)
                    .filter(t -> !t.equals("-"))
                    .collect(Collectors.joining(", "));
            System.out.println("   Véletlenszerű választott: " + randomName + ". Vonatai: " + (trains.isEmpty() ? "Nincs beosztva" : trains));
        }

        double avgAge = calculateAverageAge(cleaners);
        System.out.printf("5. feladat: Átlagéletkor: %.2f év\n", avgAge);

        cleaners.stream()
                .min(Comparator.comparingDouble(c -> Math.abs(c.getAge() - avgAge)))
                .ifPresent(c -> System.out.println("   Hozzá legközelebb áll: " + c.getName() + " (" + c.getAge() + " év)"));

        System.out.print("6. feladat: Vonatonkénti létszám: ");
        Map<String, Long> trainCounts = cleaners.stream()
                .filter(c -> !c.getTrain().equals("-"))
                .collect(Collectors.groupingBy(Cleaner::getTrain, Collectors.counting()));

        String trainStats = trainCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\t"));
        System.out.println(trainStats);

        try (PrintWriter pw = new PrintWriter("knevek.txt")) {
            cleaners.stream()
                    .map(c -> c.getName().split(" ")[1])
                    .distinct()
                    .forEach(pw::println);
            System.out.println("7. feladat: Egyedi keresztnevek elmentve a knevek.txt fájlba.");
        } catch (FileNotFoundException e) {
            System.err.println("Hiba a mentéskor: " + e.getMessage());
        }
    }

    public static double calculateAverageAge(List<Cleaner> list) {
        return list.stream().mapToInt(Cleaner::getAge).average().orElse(0.0);
    }
}