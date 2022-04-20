package com.example.rare_finds

import android.os.StrictMode
import android.util.Log
import edu.practice.utils.shared.Keys
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ConnectionHelper{
    private val ip = Keys.ip() // your database server ip
    private val db = Keys.db() // your database name
    private val username = Keys.user()  // your database username
    private val password = Keys.pass()  // your database password

    fun dbConn() : Connection? {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var conn : Connection? = null
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            val connString = "jdbc:jtds:sqlserver://$ip;databaseName=$db;user=$username;password=$password;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=100;ssl=request;"
            conn = DriverManager.getConnection(connString)
        }catch (ex: SQLException){
            Log.e("Error 1: ", ex.message.toString())
        }catch (ex1: ClassNotFoundException){
            Log.e("Error 2: ", ex1.message.toString())
        }catch (ex2: Exception){
            Log.e("Error 3: ", ex2.message.toString())
        }

        return conn
    }
}
