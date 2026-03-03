public class Book {
    private String title;
    private String author;
    private String genre;
    private int yearPublished;
    private int pages;

    public Book(String title, String author, String genre, int yearPublished, int pages) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.yearPublished = yearPublished;
        this.pages = pages;
    }

    // Gettermódszerek az elemzéshez
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getYearPublished() { return yearPublished; }
    public int getPages() { return pages; }
}