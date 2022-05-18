package edu.practice.utils.shared.com.example.rare_finds.sqlconnection

import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import edu.practice.utils.shared.com.example.rare_finds.models.Library
import edu.practice.utils.shared.com.example.rare_finds.models.User
import java.io.Serializable
import java.sql.Connection
import java.sql.SQLException

class DatabaseHelper(con: Connection){

    private val conn = con
    private val col = arrayListOf<Collection>()
    private val lib = arrayListOf<Library>()

    fun fillCollectionList(userId : Int): ArrayList<Collection>{

        val sql = "SELECT * FROM [dbo].[Collection] WHERE UserId = $userId"
        try {
            val rs = conn.createStatement()?.executeQuery(sql)
            if (rs != null) {
                while (rs.next()) {
                    col.add(
                        Collection(
                            rs.getInt("CollId"),
                            rs.getString("CollName"),
                            rs.getString("CollDesc"),
                            rs.getString("CollGenre"),
                            rs.getInt("UserId")
                        )
                    )
                }
            }
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        }
        return col
    }

    fun fillLibraryList(colId : Int): ArrayList<Library>{

        val sql = "SELECT * FROM [dbo].[Library] WHERE CollId = $colId"
        try {
            val rs = conn.createStatement()?.executeQuery(sql)
            if (rs != null) {
                while (rs.next()) {
                    lib.add(
                        Library(
                            rs.getInt("LibId"),
                            rs.getString("LibName"),
                            rs.getString("LibDesc"),
                            rs.getString("LibGenre"),
                            rs.getInt("CollId")
                        )
                    )
                }
            }
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        }
        return lib
    }

    fun insertTable(table: String, col: String, values: String):Boolean {
        val sqlQuery = "INSERT INTO $table ($col) VALUES ($values)"
        with(conn) {
            try{
                createStatement().execute(sqlQuery)
            } catch (ex: SQLException){
                println("--------------------------------------------------")
                println(ex.message)
                println("--------------------------------------------------")
             return false
            }
        }
        return true
    }

    fun checkUser(userName: String, pass: String): Serializable{
        val sqlQuery = "SELECT * FROM [dbo].[User] WHERE EncryptedPass = '${pass}' AND UserName = '${userName}'"
        try {
            val rs = conn.createStatement()?.executeQuery(sqlQuery)
            if (rs != null) {
                while (rs.next()) {
                    return User(
                        rs.getInt("UserId"),
                        rs.getString("UserName"),
                        rs.getString("Email"),
                        rs.getString("imageUrl")
                    )
                }
            }
        } catch (ex: SQLException) {
            println("--------------------------------------------------")
            println(ex.message)
            println("--------------------------------------------------")
        }
        return false
    }

    fun checkCount(id: String, table: String): Int{
        val sqlQuery = "SELECT COUNT($id)AS 'sum' FROM [dbo].[$table]"
        try {
            val rs = conn.createStatement()?.executeQuery(sqlQuery)
            if (rs != null) {
                while (rs.next()) {
                    return rs.getInt("sum")
                }
            }
        } catch (ex: SQLException) {
            println("--------------------------------------------------")
            println(ex.message)
            println("--------------------------------------------------")
        }
        return 0
    }
}