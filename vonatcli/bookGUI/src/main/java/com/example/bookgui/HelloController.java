package com.example.bookgui;


import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class HelloController {
    @FXML
    private TextArea resultArea;

    @FXML
    protected void onAnalyzeButtonClick() {
        List<Book> books = loadBooksFromCSV("books.csv");
        if (books.isEmpty()) {
            resultArea.setText("No data found or error reading file.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        Book maxPagesBook = books.stream().max(Comparator.comparingInt(Book::getPages)).get();
        sb.append("1. Book with the most pages:\n")
                .append("Title: ").append(maxPagesBook.getTitle()).append("\n")
                .append("Author: ").append(maxPagesBook.getAuthor()).append("\n")
                .append("Pages: ").append(maxPagesBook.getPages()).append("\n\n");

        sb.append("2. Number of books in each genre:\n");
        books.stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.counting()))
                .forEach((genre, count) -> sb.append(genre).append(": ").append(count).append("\n"));
        sb.append("\n");

        Book oldest = books.stream().min(Comparator.comparingInt(Book::getYearPublished)).get();
        sb.append("3. Oldest book:\nTitle: ").append(oldest.getTitle())
                .append("\nYear: ").append(oldest.getYearPublished()).append("\n\n");

        Book newest = books.stream().max(Comparator.comparingInt(Book::getYearPublished)).get();
        sb.append("4. Newest book:\nTitle: ").append(newest.getTitle())
                .append("\nYear: ").append(newest.getYearPublished()).append("\n\n");

        sb.append("5. Average pages per genre:\n");
        books.stream()
                .collect(Collectors.groupingBy(Book::getGenre, Collectors.averagingInt(Book::getPages)))
                .forEach((genre, avg) -> sb.append(String.format("%s: %.2f pages\n", genre, avg)));

        resultArea.setText(sb.toString());
    }

    private List<Book> loadBooksFromCSV(String fileName) {
        List<Book> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine(); // Header
            String line;
            while ((line = br.readLine()) != null) {
                String[] v = line.split(",");
                if (v.length >= 5) {
                    list.add(new Book(v[0].trim(), v[1].trim(), v[2].trim(),
                            Integer.parseInt(v[3].trim()), Integer.parseInt(v[4].trim())));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
