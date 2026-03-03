const express = require('express')
const cors = require('cors')
const mysql = require('mysql2')
const app = express()
app.use(cors())
app.use(express.json)

const conn = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'restaurant'
})

app.get('/api/categories', (req,res)=>{
    const sql = 'SELECT id, name, photo FROM categories ORDER BY name ASC'

    conn.query(sql, (err, result)=>{
        if(err){
            console.warn.err()
        }
        else{
            return res.status(200).json(result)
        }
    })
})

app.get('/api/foodsbycateg/:categId', (req,res)=>{
    const sql = 'SELECT foods.title, foods.photo, foods.price, categories.name name FROM categories INNER JOIN foods ON categories.id = foods.categoryId'

    conn.query(sql, (err, result)=>{
        if(err){
            console.warn.err()
        }
        else{
            return res.status(200).json(result)
        }
    })
})

app.get('/api/foodsbysearch/:searchedWord', (req,res)=>{
    const sw = {searchedWord}
    const sql = 'SELECT foods.title, foods.photo, foods.price, categories.name name FROM categories INNER JOIN foods ON categories.id = foods.categoryId WHERE food.name LIKE ?'

    conn.query(sql, [searchedWord], (err, result)=>{
        if(err){
            return res.status(404)
        }
        else{
            return res.status(200).json(result)
        }
    })
})

app.put('', (req, res)=>{
    const sql = ''

    conn.query(sql, (err, result)=>{
        if(err){
            return res.status(400)
        }
        else{
            return res.status(200).json(result)
        }
    })
})

app.post('', (req, res)=>{
    const sql = ''

    conn.query(sql, (err, result)=>{
        if(err){
            return res.status(400)
        }
        else{
            return res.status(200).json(result)
        }
    })
})