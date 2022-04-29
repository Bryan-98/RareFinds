package com.example.rare_finds.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.activities.CollectionActivity
import edu.practice.utils.shared.com.example.rare_finds.activities.MainActivity
import edu.practice.utils.shared.com.example.rare_finds.models.SqlInfo
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

        val btnAdd: View = findViewById(R.id.btnLogin)
        btnAdd.setOnClickListener{
            val name = editTextName.text
            val pass = editTextDescription.text
            if (con != null) {
                val isUser = db?.checkUser(name.toString(), pass.toString())
                if(isUser != false) {
                    val intent = Intent(this, MainActivity::class.java)
                    val user = Bundle()
                    user.putSerializable("userInfo", isUser)
                    intent.putExtras(user)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}