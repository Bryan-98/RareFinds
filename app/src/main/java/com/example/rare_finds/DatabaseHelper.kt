package com.example.rare_finds

import java.sql.Connection
import java.sql.SQLException

class DatabaseHelper(con: Connection){

    private val conn = con

    fun fillCategoryList(): ArrayList<Category>{
        var cat = arrayListOf<Category>()
        val sql = "SELECT * FROM dbo.Category"
        try {
            val rs = conn.createStatement()?.executeQuery(sql)
            if (rs != null) {
                while (rs.next()) {
                    cat.add(
                        Category(
                            rs.getInt("CategoryId"),
                            rs.getString("CategoryName"),
                            rs.getString("CategoryDescription")
                        )
                    )
                }
            }
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        }
        return cat
    }

    fun insertTable(table: String, col: String, values: String) {
        val sqlQuery = "INSERT INTO $table ($col) VALUES ($values)"
        with(conn) {
            createStatement().execute(sqlQuery)
        }
    }
}