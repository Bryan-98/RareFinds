package com.example.rare_finds.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.activities.MainActivity
import edu.practice.utils.shared.com.example.rare_finds.models.User
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper

class LoginActivity : AppCompatActivity() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextName = findViewById<EditText>(R.id.userName)
        val editTextDescription = findViewById<EditText>(R.id.userPassword)

        //sign up activity button
        val btnSign: View = findViewById(R.id.btnSignup)
        btnSign.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        //login button
        val btnlogin: View = findViewById(R.id.btnLogin)
        btnlogin.setOnClickListener{
            val name = editTextName.text
            val pass = editTextDescription.text
            if (con != null) {
                val isUser = db?.checkUser(name.toString(), pass.toString())
                if(isUser != false) {
                    val intent = Intent(this, MainActivity::class.java)
                    val user = isUser as User
                    sharedUserPref(user.userId, user.userName, user.email)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun sharedUserPref(userId:Int, userName:String, email:String){
        val sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.apply{
            putInt("userId",userId)
            putString("userName",userName)
            putString("email",email)
        }.apply()
    }
}