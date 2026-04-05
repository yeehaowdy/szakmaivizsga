const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');

const app = express();

// Middleware beállítások
app.use(cors());
app.use(express.json()); // Body-parser helyett az Express beépített JSON értelmezője

// Adatbázis kapcsolat létrehozása a get_certified sémához
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'get_certified'
});

db.connect(err => {
    if (err) {
        console.error('Hiba az adatbázis csatlakozáskor:', err);
        return;
    }
    console.log('Sikeresen csatlakozva a MySQL adatbázishoz.');
});

// 1. Személyek lekérdezése (ABC sorrendben) [cite: 354, 356]
app.get('/api/people', (req, res) => {
    const sql = "SELECT * FROM people ORDER BY last_name ASC, first_name ASC";
    db.query(sql, (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(results);
    });
});

// 2. Tanúsítványok lekérdezése (Név szerinti ABC sorrendben) [cite: 357, 359]
app.get('/api/certifications', (req, res) => {
    const sql = "SELECT * FROM certifications ORDER BY name ASC";
    db.query(sql, (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(results);
    });
});

// 3. Egy személy vizsgakísérleteinek lekérdezése [cite: 360, 362]
// Tartalmazza a tanúsítvány és a szolgáltató nevét is
app.get('/api/attempts/person/:person_id', (req, res) => {
    const sql = `
        SELECT a.id AS attempt_id, a.person_id, p.first_name, p.last_name, 
               a.certification_id, c.name AS certification_name, pr.name AS provider_name,
               a.start_datetime, a.end_datetime, a.percentage, a.is_passed, 
               a.previous_attempt_id, a.next_attempt_id
        FROM attempts a
        JOIN people p ON a.person_id = p.id
        JOIN certifications c ON a.certification_id = c.id
        JOIN providers pr ON c.provider_id = pr.id
        WHERE a.person_id = ?`;
    db.query(sql, [req.params.person_id], (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(results);
    });
});

// 4. Egy tanúsítvány kísérleteinek lekérdezése [cite: 363, 364]
app.get('/api/attempts/certification/:certification_id', (req, res) => {
    const sql = `
        SELECT a.id AS attempt_id, a.person_id, CONCAT(p.first_name, ' ', p.last_name) AS full_name,
               a.certification_id, c.name AS certification_name, pr.name AS provider_name,
               a.start_datetime, a.end_datetime, a.percentage, a.is_passed
        FROM attempts a
        JOIN people p ON a.person_id = p.id
        JOIN certifications c ON a.certification_id = c.id
        JOIN providers pr ON c.provider_id = pr.id
        WHERE a.certification_id = ?`;
    db.query(sql, [req.params.certification_id], (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(results);
    });
});

// 5. Keresés tanúsítvány vagy szolgáltató neve alapján [cite: 365, 366]
app.get('/api/search/:searchedWord', (req, res) => {
    const search = `%${req.params.searchedWord}%`;
    const sql = `
        SELECT a.id AS attempt_id, CONCAT(p.first_name, ' ', p.last_name) AS person_name,
               a.certification_id, c.name AS certification_name, pr.name AS provider_name,
               a.start_datetime, a.end_datetime, a.percentage, a.is_passed
        FROM attempts a
        JOIN people p ON a.person_id = p.id
        JOIN certifications c ON a.certification_id = c.id
        JOIN providers pr ON c.provider_id = pr.id
        WHERE c.name LIKE ? OR pr.name LIKE ?`;
    db.query(sql, [search, search], (err, results) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json(results);
    });
});

// 6. Egy vizsgakísérlet módosítása azonosító alapján [cite: 367, 369]
app.patch('/api/update-attempt', (req, res) => {
    const { id, end_datetime, percentage, is_passed } = req.body;
    const sql = "UPDATE attempts SET end_datetime = ?, percentage = ?, is_passed = ? WHERE id = ?";
    db.query(sql, [end_datetime, percentage, is_passed, id], (err, result) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json({ message: "Sikeresen módosítva.", affectedRows: result.affectedRows });
    });
});

// 7. Új vizsgakísérlet felvétele [cite: 372, 373]
app.post('/api/attempts', (req, res) => {
    const sql = "INSERT INTO attempts SET ?";
    db.query(sql, req.body, (err, result) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json({ message: "Vizsgakísérlet sikeresen felvéve.", id: result.insertId });
    });
});

// 8. Vizsgakísérlet törlése azonosító alapján [cite: 376, 377]
app.delete('/api/attempts', (req, res) => {
    const { id } = req.body;
    const sql = "DELETE FROM attempts WHERE id = ?";
    db.query(sql, [id], (err, result) => {
        if (err) return res.status(500).json({ error: err.message });
        res.json({ message: "Vizsgakísérlet sikeresen törölve.", affectedRows: result.affectedRows });
    });
});

// Szerver indítása
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`A szerver fut a http://localhost:${PORT} címen.`);
});