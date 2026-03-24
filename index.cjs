const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');

const app = express();
app.use(express.json()); // JSON kérések törzsének feldolgozásához [cite: 293]
app.use(cors()); // A frontend és backend közötti kommunikáció engedélyezéséhez

// Adatbázis kapcsolat konfigurálása a feladat szerint 
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'felvonulasok'
});

// 1. Összes menet lekérdezése (GET /api/demonstrations) [cite: 283]
// Összekapcsoljuk a demonstrations és attendants táblát a vezető adataiért [cite: 285]
app.get('/api/demonstrations', (req, res) => {
    const sql = `
        SELECT d.*, a.name AS leader_name, a.image AS leader_image 
        FROM demonstrations d
        JOIN attendants a ON d.leader_id = a.id
        ORDER BY d.id ASC`;
    db.query(sql, (err, results) => {
        if (err) return res.status(500).json(err);
        res.json(results);
    });
});

// 2. Egy bizonyos menet résztvevőinek lekérdezése [cite: 286]
app.get('/api/attendants/by-demonstration/:demonstration_id', (req, res) => {
    const { demonstration_id } = req.params;
    const sql = `
        SELECT a.id, a.name, a.image 
        FROM attendants a
        JOIN marching m ON a.id = m.attendant_id
        WHERE m.demonstration_id = ?`;
    db.query(sql, [demonstration_id], (err, results) => {
        if (err) return res.status(500).json(err);
        res.json(results);
    });
});

// 3. Keresés kulcsszó alapján (menet, résztvevő vagy párt neve) [cite: 289, 290]
app.get('/api/marching/by-search/:search_word', (req, res) => {
    const search = `%${req.params.search_word}%`;
    const sql = `
        SELECT d.id AS demonstration_id, d.name AS demonstration_name, d.date, d.count, 
               d.image AS demonstration_image, d.party, a.id AS attendant_id, 
               a.name AS attendant_name, a.image AS attendant_image
        FROM demonstrations d
        JOIN marching m ON d.id = m.demonstration_id
        JOIN attendants a ON m.attendant_id = a.id
        WHERE d.name LIKE ? OR a.name LIKE ? OR d.party LIKE ?`;
    db.query(sql, [search, search, search], (err, results) => {
        if (err) return res.status(500).json(err);
        res.json(results);
    });
});

// 4. Egy felvonulás adatainak módosítása [cite: 292]
app.put('/api/demonstration', (req, res) => {
    const { id, ...updateData } = req.body;
    if (!id || Object.keys(updateData).length === 0) return res.json({ message: "Nincs frissítendő adat" });

    // Dinamikus SQL építése: csak azokat a mezőket frissítjük, amik érkeztek 
    const sql = "UPDATE demonstrations SET ? WHERE id = ?";
    db.query(sql, [updateData, id], (err, result) => {
        if (err) return res.status(500).json(err);
        res.json({ message: "Sikeres módosítás", affectedRows: result.affectedRows });
    });
});

// 5. Új menet létrehozása [cite: 297]
app.post('/api/demonstration', (req, res) => {
    const sql = "INSERT INTO demonstrations SET ?";
    db.query(sql, req.body, (err, result) => {
        if (err) return res.status(500).json(err);
        res.status(201).json({ id: result.insertId, ...req.body });
    });
});

// 6. Új résztvevő hozzáadása egy menethez [cite: 300]
// Először megkeressük vagy létrehozzuk a személyt, majd rögzítjük a részvételt a marching táblában [cite: 302]
app.post('/api/marching', (req, res) => {
    const { name, demonstration_id } = req.body;
    
    // Egyszerűsített logika: feltételezzük, hogy a személy már létezik vagy itt szúrjuk be
    const findPersonSql = "SELECT id FROM attendants WHERE name = ?";
    db.query(findPersonSql, [name], (err, results) => {
        if (err) return res.status(500).json(err);
        
        const attendantId = results.length > 0 ? results[0].id : null;
        if (attendantId) {
            const insertMarching = "INSERT INTO marching (attendant_id, demonstration_id) VALUES (?, ?)";
            db.query(insertMarching, [attendantId, demonstration_id], (err2) => {
                if (err2) return res.status(500).json(err2);
                res.json({ message: "Részvétel rögzítve" });
            });
        } else {
            res.status(404).json({ message: "Személy nem található" });
        }
    });
});

// 7. Párt felvonulói és menetei [cite: 303, 304]
app.get('/api/marching/by-party/:party_name', (req, res) => {
    const sql = `
        SELECT d.id AS demonstration_id, d.name AS demonstration_name, d.date, d.count, d.image, d.party,
               a.id AS attendant_id, a.name AS attendant_name, a.image AS attendant_image
        FROM demonstrations d
        LEFT JOIN marching m ON d.id = m.demonstration_id
        LEFT JOIN attendants a ON m.attendant_id = a.id
        WHERE d.party = ?`;
    
    db.query(sql, [req.params.party_name], (err, results) => {
        if (err) return res.status(500).json(err);
        
        // Az adatokat csoportosítani kell menetek szerint a minta válasz alapján 
        const grouped = results.reduce((acc, curr) => {
            let demo = acc.find(d => d.demonstration_id === curr.demonstration_id);
            if (!demo) {
                demo = { 
                    demonstration_id: curr.demonstration_id, 
                    demonstration_name: curr.demonstration_name,
                    date: curr.date, count: curr.count, image: curr.image, party: curr.party,
                    attendants: [] 
                };
                acc.push(demo);
            }
            if (curr.attendant_id) {
                demo.attendants.push({ id: curr.attendant_id, name: curr.attendant_name, image: curr.attendant_image });
            }
            return acc;
        }, []);
        res.json(grouped);
    });
});

app.listen(3000, () => console.log('Szerver fut a 3000-es porton'));