const express = require('express');
const mysql = require('mysql2');
const app = express();
const PORT = 3333;

const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'books_db' 
});

db.connect(err => {
    if (err) {
        console.error('Hiba az adatbázis csatlakozáskor:', err);
        return;
    }
    console.log('Sikeresen csatlakozva az adatbázishoz.');
});

app.get('/books/most-pages', (req, res) => {
    const query = 'SELECT title, author, pages FROM books ORDER BY pages DESC LIMIT 1';
    db.query(query, (err, results) => {
        if (err) return res.status(500).send(err);
        res.json({ "1. Book with the most pages": results[0] });
    });
});

app.get('/books/count-by-genre', (req, res) => {
    const query = 'SELECT genre, COUNT(*) as count FROM books GROUP BY genre';
    db.query(query, (err, results) => {
        if (err) return res.status(500).send(err);
        res.json({ "2. Number of books in each genre": results });
    });
});

app.get('/books/oldest', (req, res) => {
    const query = 'SELECT title, author, year_published FROM books ORDER BY year_published ASC LIMIT 1';
    db.query(query, (err, results) => {
        if (err) return res.status(500).send(err);
        res.json({ "3. Oldest book": results[0] });
    });
});

app.get('/books/newest', (req, res) => {
    const query = 'SELECT title, author, year_published FROM books ORDER BY year_published DESC LIMIT 1';
    db.query(query, (err, results) => {
        if (err) return res.status(500).send(err);
        res.json({ "4. Newest book": results[0] });
    });
});

app.get('/books/average-pages', (req, res) => {
    const query = 'SELECT genre, AVG(pages) as average_pages FROM books GROUP BY genre';
    db.query(query, (err, results) => {
        if (err) return res.status(500).send(err);
        
        const formatted = results.map(r => ({
            genre: r.genre,
            average_pages: parseFloat(r.average_pages).toFixed(2) + " pages"
        }));
        res.json({ "5. Average number of pages for books in each genre": formatted });
    });
});

app.listen(PORT, () => {
    console.log(`A szerver fut a http://localhost:${PORT} címen`);
});