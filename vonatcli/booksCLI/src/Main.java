import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String fileName = "books.csv";
        List<Book> books = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5) {
                    books.add(new Book(
                            values[0].trim(),
                            values[1].trim(),
                            values[2].trim(),
                            Integer.parseInt(values[3].trim()),
                            Integer.parseInt(values[4].trim())
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Hiba a fájl beolvasásakor: " + e.getMessage());
            return;
        }

        Book maxPagesBook = books.stream()
                .max(Comparator.comparingInt(Book::getPages))
                .orElse(null);

        System.out.println("1. Book with the most pages:");
        if (maxPagesBook != null) {
            System.out.println("Title: " + maxPagesBook.getTitle());
            System.out.println("Author: " + maxPagesBook.getAuthor());
            System.out.println("Pages: " + maxPagesBook.getPages());
        }

        System.out.println("\n2. Number of books in each genre:");
        Map<String, Long> countByGenre = books.stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()));
        countByGenre.forEach((genre, count) -> System.out.println(genre + ": " + count));

        Book oldestBook = books.stream()
                .min(Comparator.comparingInt(Book::getYearPublished))
                .orElse(null);

        System.out.println("\n3. Oldest book:");
        if (oldestBook != null) {
            System.out.println("Title: " + oldestBook.getTitle());
            System.out.println("Author: " + oldestBook.getAuthor());
            System.out.println("Year Published: " + oldestBook.getYearPublished());
        }

        Book newestBook = books.stream()
                .max(Comparator.comparingInt(Book::getYearPublished))
                .orElse(null);

        System.out.println("\n4. Newest book:");
        if (newestBook != null) {
            System.out.println("Title: " + newestBook.getTitle());
            System.out.println("Author: " + newestBook.getAuthor());
            System.out.println("Year Published: " + newestBook.getYearPublished());
        }

        System.out.println("\n5. Average number of pages for books in each genre:");
        Map<String, Double> avgPagesByGenre = books.stream()
                .collect(Collectors.groupingBy(Book::getGenre,
                        Collectors.averagingInt(Book::getPages)));

        avgPagesByGenre.forEach((genre, avg) ->
                System.out.printf("%s: %.2f pages\n", genre, avg));
    }
}