import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class Booking {
    String email, firstName, lastName, hotelName, hotelDescription, startDate, endDate;
    int duration;

    public Booking(String[] line) {
        this.email = line[0];
        this.firstName = line[1];
        this.lastName = line[2];
        this.hotelName = line[8];
        this.hotelDescription = line[9];
        this.startDate = line[13];
        this.endDate = line[14];
        this.duration = (int) ChronoUnit.DAYS.between(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    public int getYear() { return LocalDate.parse(startDate).getYear(); }
}

public class Main {
    public static void main(String[] args) throws IOException {
        List<Booking> bookings = Files.lines(Paths.get("bookings.csv"))
                .skip(1)
                .map(line -> line.split(","))
                .map(Booking::new)
                .collect(Collectors.toList());

        Booking longest = bookings.stream().max(Comparator.comparingInt(b -> b.duration)).get();
        System.out.printf("Leghosszabb: %s %s, %s (%d nap)\n", longest.firstName, longest.lastName, longest.hotelName, longest.duration);

        bookings.stream()
                .filter(b -> b.getYear() == 2024)
                .sorted((b1, b2) -> Integer.compare(b2.duration, b1.duration))
                .forEach(b -> System.out.println(b.hotelName + ": " + b.duration + " nap"));

        long uniqueGuests = bookings.stream().map(b -> b.email).distinct().count();
        System.out.println("Vendégek száma: " + uniqueGuests);
        String randomEmail = bookings.get(new Random().nextInt(bookings.size())).email;
        System.out.println("Foglalások " + randomEmail + " részére:");
        bookings.stream().filter(b -> b.email.equals(randomEmail)).forEach(b -> System.out.println("- " + b.hotelName));

        Booking maxWords = bookings.stream().max(Comparator.comparingInt(b -> b.hotelDescription.split("\\s+").length)).get();
        System.out.println("Leghosszabb leírású hotel: " + maxWords.hotelName);

        bookings.stream()
                .collect(Collectors.groupingBy(Booking::getYear, Collectors.counting()))
                .entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue() + " foglalás"));

        List<String> shortBookings = bookings.stream()
                .filter(b -> b.duration <= 1)
                .map(b -> b.firstName + " " + b.lastName + ";" + b.hotelName)
                .collect(Collectors.toList());
        Files.write(Paths.get("rovidek.txt"), shortBookings);
    }
}