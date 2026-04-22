const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// Adatbázis konfiguráció 
const dbConfig = {
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'exhibitions'
};

// 1. Városok listázása [cite: 147, 148, 149, 150]
app.get('/api/cities', async (req, res) => {
    try {
        const connection = await mysql.createConnection(dbConfig);
        // Városok lekérése ABC sorrendben [cite: 148]
        const [cities] = await connection.execute('SELECT DISTINCT city FROM museums ORDER BY city ASC');
        
        const result = [];
        for (const row of cities) {
            // Múzeumok lekérése az adott városhoz [cite: 149]
            const [museums] = await connection.execute(
                'SELECT id, name, anchor, image FROM museums WHERE city = ?', 
                [row.city]
            );
            result.push({
                city: row.city,
                museums: museums
            });
        }
        
        await connection.end();
        res.status(200).json({ success: true, data: result });
    } catch (error) {
        res.status(500).json({ success: false, error: "Adatbázis lekérdezési hiba" });
    }
});

// 2. Egy város múzeumaiban zajlott látogatások [cite: 151, 152, 153, 154]
// Megjegyzés: A feladat szerint egy múzeum ID alapján keressük meg a várost, majd annak összes látogatását [cite: 177, 178]
app.get('/api/cities/:id', async (req, res) => {
    try {
        const connection = await mysql.createConnection(dbConfig);
        const museumId = req.params.id;

        // Előbb megkeressük melyik városról van szó az ID alapján
        const [museumRows] = await connection.execute('SELECT city FROM museums WHERE id = ?', [museumId]);
        
        if (museumRows.length === 0) {
            await connection.end();
            return res.status(404).json({ success: false, error: "A város nem található" });
        }

        const cityName = museumRows[0].city;

        // Lekérdezzük az adott város összes múzeumának összes látogatását [cite: 152]
        const query = `
            SELECT v.id AS visit_id, v.name AS visit_name, v.description, v.visit_time, 
                   m.name AS museum_name, m.anchor AS museum_anchor, m.image AS museum_image, 
                   t.name AS type_name
            FROM visits v
            JOIN museums m ON v.museum_id = m.id
            JOIN types t ON v.type_id = t.id
            WHERE m.city = ?`;
        
        const [visits] = await connection.execute(query, [cityName]);
        
        await connection.end();
        res.status(200).json({ 
            success: true, 
            city: cityName, 
            data: visits 
        });
    } catch (error) {
        res.status(500).json({ success: false, error: "Szerver hiba" });
    }
});

// 3. Keresés látogatások között [cite: 155, 156, 157]
app.get('/api/visits/search/:keyword', async (req, res) => {
    try {
        const connection = await mysql.createConnection(dbConfig);
        const keyword = `%${req.params.keyword}%`;

        const query = `
            SELECT v.id AS visit_id, v.name AS visit_name, v.description, v.visit_time, 
                   m.name AS museum_name, m.city, m.anchor AS museum_anchor, m.image AS museum_image, 
                   t.name AS type_name
            FROM visits v
            JOIN museums m ON v.museum_id = m.id
            JOIN types t ON v.type_id = t.id
            WHERE v.name LIKE ? OR v.description LIKE ? OR m.name LIKE ? OR t.name LIKE ?`;
        
        const [results] = await connection.execute(query, [keyword, keyword, keyword, keyword]);
        
        await connection.end();
        
        if (results.length === 0) {
            return res.status(200).json({ 
                success: false, 
                keyword: req.params.keyword, 
                message: "Nincs találat a keresésre" 
            });
        }

        res.status(200).json({ 
            success: true, 
            keyword: req.params.keyword, 
            count: results.length, 
            data: results 
        });
    } catch (error) {
        res.status(500).json({ success: false, error: "Keresési hiba" });
    }
});

// 4. Egy látogatás időtartamának módosítása [cite: 158, 159, 160]
app.put('/api/visits/:id', async (req, res) => {
    try {
        const { visit_time } = req.body;
        const visitId = req.params.id;

        if (visit_time === undefined || isNaN(visit_time)) {
            return res.status(400).json({ success: false, error: "Hiányzik a visit_time mező vagy az érték nem szám" });
        }

        const connection = await mysql.createConnection(dbConfig);
        
        const [updateResult] = await connection.execute(
            'UPDATE visits SET visit_time = ? WHERE id = ?', 
            [visit_time, visitId]
        );

        if (updateResult.affectedRows === 0) {
            await connection.end();
            return res.status(404).json({ success: false, error: "A látogatás nem található" });
        }

        // Visszaadjuk a módosított adatokat a minta szerint [cite: 160]
        const [updatedData] = await connection.execute(`
            SELECT v.*, m.name AS museum_name, m.anchor AS museum_anchor, m.image AS museum_image, t.name AS type_name 
            FROM visits v 
            JOIN museums m ON v.museum_id = m.id 
            JOIN types t ON v.type_id = t.id 
            WHERE v.id = ?`, [visitId]);

        await connection.end();
        res.status(200).json({ 
            success: true, 
            message: "A látogatás időtartama sikeresen módosítva", 
            data: updatedData[0] 
        });
    } catch (error) {
        res.status(500).json({ success: false, error: "Módosítási hiba" });
    }
});

// 5. Új látogatási típus bevezetése [cite: 161, 162, 163]
app.post('/api/types', async (req, res) => {
    try {
        const { name } = req.body;

        if (!name || typeof name !== 'string') {
            return res.status(400).json({ success: false, error: "Hiányzik a name mező vagy nem string" });
        }

        const connection = await mysql.createConnection(dbConfig);
        
        // Ellenőrizzük, létezik-e már [cite: 163]
        const [existing] = await connection.execute('SELECT id FROM types WHERE name = ?', [name]);
        if (existing.length > 0) {
            await connection.end();
            return res.status(409).json({ success: false, error: "Ez a típus már létezik" });
        }

        const [insertResult] = await connection.execute('INSERT INTO types (name) VALUES (?)', [name]);
        
        const newId = insertResult.insertId;
        await connection.end();

        res.status(201).json({ 
            success: true, 
            message: "Új típus sikeresen létrehozva", 
            data: { id: newId, name: name } 
        });
    } catch (error) {
        res.status(500).json({ success: false, error: "Hiba a létrehozás során" });
    }
});

// Szerver indítása
const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Backend szerver fut a http://localhost:${PORT} porton`);
});