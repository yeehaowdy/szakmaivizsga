import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Plakat> plakatok = new ArrayList<>();
        try {
            // 1. Fájl beolvasása Stream-mel (fejléc kihagyásával)
            plakatok = Files.lines(Paths.get("propaganda.csv"))
                    .skip(1)
                    .map(Plakat::new)
                    .collect(Collectors.toList());
            System.out.println("1) Beolvasott rekordok száma: " + plakatok.size());

            // 2. Legnagyobb méretű plakát(ok)
            double maxTerulet = plakatok.stream()
                    .mapToDouble(Plakat::getTerulet)
                    .max().orElse(0);
            System.out.println("2) Legnagyobb méretű plakát (ok) (terület alapján):");
            plakatok.stream()
                    .filter(p -> p.getTerulet() == maxTerulet)
                    .forEach(System.out::println);

            // 3. Fideszes plakátok dátum szerint csökkenőben
            System.out.println("3) Fideszes plakátok (megjelenés dátuma szerint csökkenő sorrend):");
            plakatok.stream()
                    .filter(p -> p.getPart().equalsIgnoreCase("FIDESZ"))
                    .sorted(Comparator.comparing(Plakat::getDatum).reversed())
                    .forEach(p -> System.out.printf("sorszám: %d; dátum: %s; méret: %.1fx%.1f; szöveg: %s\n",
                            p.getSorszam(), p.getDatum(), p.getSzelesseg(), p.getHosszusag(), p.getSzoveg()));

            // 4. Egyedi pártok és véletlen választás
            List<String> partok = plakatok.stream()
                    .map(Plakat::getPart)
                    .distinct()
                    .collect(Collectors.toList());
            System.out.println("4) Egyedi kihelyező pártok száma: " + partok.size());

            String randomPart = partok.get(new Random().nextInt(partok.size()));
            long plakatSzam = plakatok.stream().filter(p -> p.getPart().equals(randomPart)).count();
            System.out.println("Véletlenszerűen kiválasztott párt: " + randomPart);
            System.out.println(randomPart + " által kihelyezett plakátok száma: " + plakatSzam);

            // 5. Legtöbb szóból álló plakát
            System.out.println("5) Plakátszöveg szavainak számláló függvénye:");
            plakatok.stream()
                    .max(Comparator.comparingInt(Plakat::getSzoSzam))
                    .ifPresent(p -> {
                        System.out.println("A legtöbb szóból álló plakát:");
                        System.out.println(p);
                        System.out.println("szavak száma: " + p.getSzoSzam());
                    });

            // 6. Statisztika pártonként (évek növekvő sorrendben)
            System.out.println("6) Statisztika pártonként:");
            plakatok.stream()
                    .collect(Collectors.groupingBy(Plakat::getPart,
                            Collectors.mapping(p -> String.valueOf(p.getDatum().getYear()),
                                    Collectors.collectingAndThen(Collectors.toSet(),
                                            set -> set.stream().sorted().collect(Collectors.joining(","))))))
                    .forEach((part, evek) -> System.out.println(part + ": " + evek));

            // 7. Mentés: csorok.txt (pontosan egy plakáttal rendelkező pártok)
            List<String> csoroPartok = plakatok.stream()
                    .collect(Collectors.groupingBy(Plakat::getPart, Collectors.counting()))
                    .entrySet().stream()
                    .filter(e -> e.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            List<String> kimenet = new ArrayList<>();
            kimenet.add("sorszám;kihelyezés_dátuma;párt;plakát_szélesség_m;plakát_hosszúság_m;plakát_szöveg");
            plakatok.stream()
                    .filter(p -> csoroPartok.contains(p.getPart()))
                    .map(p -> String.format("%d;%s;%s;%.1f;%.1f;%s",
                            p.getSorszam(), p.getDatum(), p.getPart(), p.getSzelesseg(), p.getHosszusag(), p.getSzoveg()))
                    .forEach(kimenet::add);

            Files.write(Paths.get("csorok.txt"), kimenet);
            System.out.println("7) A pontosan egy plakátot kihelyező pártok adatai elmentve: csorok.txt");

        } catch (IOException e) {
            System.err.println("Hiba a fájlkezelés során: " + e.getMessage());
        }
    }
}