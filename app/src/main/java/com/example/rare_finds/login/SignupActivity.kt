package com.example.rare_finds.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.activities.MainActivity
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper

class SignupActivity : AppCompatActivity() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val editTextName = findViewById<EditText>(R.id.signUserName2)
        val editTextEmail = findViewById<EditText>(R.id.signUserMail2)
        val editTextPass = findViewById<EditText>(R.id.signUserPass2)

        //Cancel button
        val btnCancel: View = findViewById(R.id.signUpCancelBtn)
        btnCancel.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Signup button
        val btnAdd: View = findViewById(R.id.signUpBtn)
        btnAdd.setOnClickListener{
            val name = editTextName.text
            val email = editTextEmail.text
            val pass = editTextPass.text

            if (con != null) {
                db?.insertTable("[dbo].[User]", "UserName, Email, EncryptedPass", "'${name}', '${email}', CONVERT(VARBINARY(160),'${pass}')")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}