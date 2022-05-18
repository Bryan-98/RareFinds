package com.example.rare_finds.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.rare_finds.R
import com.google.android.material.textfield.TextInputLayout
import edu.practice.utils.shared.com.example.rare_finds.activities.MainActivity
import edu.practice.utils.shared.com.example.rare_finds.models.User
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper

class LoginActivity : AppCompatActivity() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private lateinit var til : TextInputLayout;
    private lateinit var name : Editable;
    private lateinit var pass : Editable;
    private lateinit var errorMsg : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextName = findViewById<EditText>(R.id.userName2)
        val editTextDescription = findViewById<EditText>(R.id.userPassword2)

        //sign up activity button
        val btnSign: View = findViewById(R.id.btnSignup)
        btnSign.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        //login button
        val btnLogin: View = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener{
            name = editTextName.text
            pass = editTextDescription.text
            errorMsg = findViewById(R.id.sql_error_msg_login)

            clearAllInputs()
            if(checkAllInputs()) {
                if (con != null) {
                    val isUser = db?.checkUser(name.toString(), pass.toString())
                    if (isUser != false) {
                        val intent = Intent(this, MainActivity::class.java)
                        val user = isUser as User
                        sharedUserPref(user.userId, user.userName, user.email, user.imageUrl)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        errorMsg.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun checkAllInputs():Boolean {
        var count = 0
        if (name.isBlank()) {
            til = findViewById(R.id.userName);
            til.isErrorEnabled = true
            til.error = "Enter a username";
            count++
        }
        if (pass.isBlank()) {
            til = findViewById(R.id.userPassword);
            til.isErrorEnabled = true
            til.error = "Enter a Password";
            count++
        }
        if(count > 0) return false

        return true
    }

    private fun clearAllInputs() {

        if(name.isNotBlank()){
            til = findViewById(R.id.userName);
            til.isErrorEnabled = false
        }
        if(pass.isNotBlank()){
            til = findViewById(R.id.userPassword);
            til.isErrorEnabled = false
        }
    }

    private fun sharedUserPref(userId:Int, userName:String, email:String, imageUrl:String){
        val sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.apply{
            putInt("userId",userId)
            putString("userName",userName)
            putString("email",email)
            putString("imageUrl", imageUrl)
        }.apply()
    }
}