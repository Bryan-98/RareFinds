package com.example.rare_finds.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.rare_finds.R
import com.google.android.material.textfield.TextInputEditText
import edu.practice.utils.shared.com.example.rare_finds.activities.MainActivity
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper

class SignupActivity : AppCompatActivity() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var currentImagePath: String? = null

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

        val cameraBtn = findViewById<ImageButton>(R.id.signup_image)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleCameraImage(result.data, cameraBtn)
                }
            }
        cameraBtn.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(cameraIntent)
        }
    }

    private fun handleCameraImage(intent: Intent?, ivPhoto: ImageButton) {
        val bitmap = intent?.extras?.get("data") as Bitmap
        ivPhoto.setImageBitmap(bitmap)
    }

}