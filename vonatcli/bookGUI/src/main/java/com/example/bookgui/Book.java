package com.example.bookgui;

public class Book {
    private String title, author, genre;
    private int yearPublished, pages;

    public Book(String title, String author, String genre, int yearPublished, int pages) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.yearPublished = yearPublished;
        this.pages = pages;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getYearPublished() { return yearPublished; }
    public int getPages() { return pages; }
}
