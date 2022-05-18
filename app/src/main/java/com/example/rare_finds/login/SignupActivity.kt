package com.example.rare_finds.login

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.rare_finds.R
import com.google.android.material.textfield.TextInputLayout
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.BlobConnection
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private val storageCon = BlobConnection()
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var til :TextInputLayout;
    private lateinit var name :Editable;
    private lateinit var email :Editable;
    private lateinit var pass :Editable;
    private lateinit var imageUrl :String;
    private lateinit var errorMsg :TextView;
    private lateinit var imageUri: Uri
    private lateinit var cont: ContentResolver

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val editTextName = findViewById<EditText>(R.id.signUserName2)
        val editTextEmail = findViewById<EditText>(R.id.signUserMail2)
        val editTextPass = findViewById<EditText>(R.id.signUserPass2)
        val profileImage = findViewById<ImageButton>(R.id.signup_image)
        val addImageIcon = findViewById<ImageView>(R.id.add_image_icon)

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
            name = editTextName.text
            email = editTextEmail.text
            pass = editTextPass.text
            errorMsg = findViewById(R.id.sql_error_msg)

            clearAllInputs()
            if(checkAllInputs()){
                if (con != null) {
                    setImageLink(profileImage)
                    if(db?.insertTable("[dbo].[User]", "UserName, Email, EncryptedPass, imageUrl", "'${name}', '${email}', CONVERT(VARBINARY(160),'${pass}'), '${imageUrl}'") == true){
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        errorMsg.visibility = View.VISIBLE
                    }
                }
            }
        }

        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val source = result.data?.data?.let { ImageDecoder.createSource(this.contentResolver, it) }
                    val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                    profileImage.setImageBitmap(bitmap)
                    addImageIcon.visibility = View.GONE
                    imageUri = result.data?.data!!
                    cont = this.contentResolver
                }
            }

        profileImage.setOnClickListener{
            val cameraIntent = Intent(Intent.ACTION_PICK)
            cameraIntent.type = "image/*"
            galleryLauncher.launch(cameraIntent)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    private fun setImageLink(profileImage: ImageButton) {

        when (profileImage.drawable) {
            null -> {
                imageUrl = BlobConnection().returnImageUrl("users", "default")
            }
            else -> {
                val userCount = db?.checkCount("userName", "User")?.plus(1)
                GlobalScope.launch(Dispatchers.IO) {
                    storageCon.blobConnection(
                        imageUri,
                        cont,
                        "users",
                        "userid_${userCount}_profile_image"
                    )
                }
                imageUrl = storageCon.returnImageUrl("users", "userid_${userCount}_profile_image")
            }
        }
    }

    private fun checkAllInputs():Boolean {
        var count = 0
        if (name.isBlank()) {
            til = findViewById(R.id.signUserName);
            til.isErrorEnabled = true
            til.error = "Enter a username";
            count++
        }
        if (email.isBlank()) {
            til = findViewById(R.id.signUserMail);
            til.isErrorEnabled = true
            til.error = "Enter an email";
            count++
        }
        if (pass.isBlank()) {
            til = findViewById(R.id.signUserPass);
            til.isErrorEnabled = true
            til.error = "Enter a Password";
            count++
        }
        if(count > 0) return false

        return true
    }

    private fun clearAllInputs() {

        if(name.isNotBlank()){
            til = findViewById(R.id.signUserName);
            til.isErrorEnabled = false
        }
        if(email.isNotBlank()){
            til = findViewById(R.id.signUserMail);
            til.isErrorEnabled = false
        }
        if(pass.isNotBlank()){
            til = findViewById(R.id.signUserPass);
            til.isErrorEnabled = false
        }
    }

}