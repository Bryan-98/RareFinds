package edu.practice.utils.shared.com.example.rare_finds.sqlconnection

import edu.practice.utils.shared.com.example.rare_finds.models.Category
import edu.practice.utils.shared.com.example.rare_finds.models.User
import java.io.Serializable
import java.sql.Connection
import java.sql.SQLException

class DatabaseHelper(con: Connection){

    private val conn = con

    fun fillCategoryList(userId : Int): ArrayList<Category>{
        val cat = arrayListOf<Category>()
        val sql = "SELECT * FROM [dbo].[Collection] WHERE UserId = $userId"
        try {
            val rs = conn.createStatement()?.executeQuery(sql)
            if (rs != null) {
                while (rs.next()) {
                    cat.add(
                        Category(
                            rs.getInt("CollId"),
                            rs.getString("CollName"),
                            rs.getString("CollDesc")
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

    fun checkUser(userName: String, pass: String): Serializable{
        val sqlQuery = "SELECT * FROM [dbo].[User] WHERE UserName = '${userName}'"

        try {
            val rs = conn.createStatement()?.executeQuery(sqlQuery)
            if (rs != null) {
                while (rs.next()) {
                    return User(
                        rs.getInt("UserId"),
                        rs.getString("UserName"),
                        rs.getString("Email")
                    )
                }
            }
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        }
        return false
    }
}