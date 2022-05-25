package edu.practice.utils.shared.com.example.rare_finds.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.rare_finds.R
import com.google.android.material.textfield.TextInputLayout
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.BlobConnection
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UpDatingUserFragment : Fragment() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private val storageCon = BlobConnection()
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var til : TextInputLayout;
    private lateinit var newPass : Editable
    private lateinit var confirmPass :Editable
    private lateinit var cont: ContentResolver
    private lateinit var imageUri: Uri
    private lateinit var errorMsg : TextView;
    private lateinit var imageUrl :String;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_up_dating_user, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.profile_settings)
        view.apply{
            val editTextPass = findViewById<EditText>(R.id.change_password2)
            val editTextChan = findViewById<EditText>(R.id.confirm_change_password2)
            val profileImage = findViewById<ImageButton>(R.id.imageButton)

            val btnAdd: View = findViewById(R.id.update_button)
            btnAdd.setOnClickListener{
                newPass = editTextPass.text
                confirmPass = editTextChan.text
                errorMsg = findViewById(R.id.sql_error_msg)

                clearAllInputs(view)
                if(checkAllInputs(view)){
                    if (con != null) {
                        setImageLink(profileImage)
                        if(newPass.toString() == confirmPass.toString()){
                            if(db?.updateUserTable("$newPass", imageUrl,loadUserData()) == true){
                                val act = view.context as AppCompatActivity
                                val colFragment = CollectionFragment()
                                act.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.from_right, R.anim.from_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                    .replace(R.id.fragmentContainerView,colFragment).addToBackStack(null)
                                    .commit()
                            }
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
                        val source = result.data?.data?.let { ImageDecoder.createSource((activity as AppCompatActivity).contentResolver, it) }
                        val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                        profileImage.setImageBitmap(bitmap)
                        imageUri = result.data?.data!!
                        cont = (activity as AppCompatActivity).contentResolver
                    }
                }

            profileImage.setOnClickListener{
                val cameraIntent = Intent(Intent.ACTION_PICK)
                cameraIntent.type = "image/*"
                galleryLauncher.launch(cameraIntent)
            }
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
                val userCount = db?.checkCount("UserId", "User")?.plus(1)
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

    private fun checkAllInputs(view: View):Boolean {
        var count = 0
        if (newPass.isBlank()) {
            til = view.findViewById(R.id.change_password);
            til.isErrorEnabled = true
            til.error = "Enter a new password";
            count++
        }
        if (confirmPass.isBlank()) {
            til = view.findViewById(R.id.confirm_change_password);
            til.isErrorEnabled = true
            til.error = "Confirm your new password";
            count++
        }
        if(count > 0) return false

        return true
    }

    private fun clearAllInputs(view: View) {

        if(newPass.isNotBlank()){
            til = view.findViewById(R.id.change_password);
            til.isErrorEnabled = false
        }
        if(confirmPass.isNotBlank()){
            til = view.findViewById(R.id.confirm_change_password);
            til.isErrorEnabled = false
        }
    }

    private fun loadUserData():Int{
        val sp = this.activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            return sp.getInt("userId", 0)
        }
        return 0
    }

}