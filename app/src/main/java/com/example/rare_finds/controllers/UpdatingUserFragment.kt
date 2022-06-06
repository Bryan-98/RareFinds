package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.annotation.SuppressLint
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
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.com.example.rare_finds.fragments.CollectionFragment
import com.example.rare_finds.connection.BlobConnection
import com.example.rare_finds.connection.ConnectionHelper
import com.example.rare_finds.connection.DatabaseHelper
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class UpdatingUserFragment : Fragment() {
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
    private lateinit var addImageIcon: ImageButton;
    private var userId by Delegates.notNull<Int>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadUserData()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_user, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.profile_settings)
        view.apply{
            val editTextPass = findViewById<EditText>(R.id.change_password2)
            val editTextChan = findViewById<EditText>(R.id.confirm_change_password2)
            val profileImage = findViewById<ImageButton>(R.id.imageButton)

            Picasso.get().load(imageUrl).fit()
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(profileImage)

            galleryLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val source = result.data?.data?.let { ImageDecoder.createSource((activity as AppCompatActivity).contentResolver, it) }
                        val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                        profileImage.setImageBitmap(bitmap)
                        imageUri = result.data?.data!!
                        cont = (activity as AppCompatActivity).contentResolver
                        setImageLink(profileImage)
                    }
                }

            profileImage.setOnClickListener{
                val cameraIntent = Intent(Intent.ACTION_PICK)
                cameraIntent.type = "image/*"
                galleryLauncher.launch(cameraIntent)
            }

            val btnAdd: View = findViewById(R.id.update_button)
            btnAdd.setOnClickListener{
                newPass = editTextPass.text
                confirmPass = editTextChan.text
                errorMsg = findViewById(R.id.sql_error_msg)

                clearAllInputs(view)
                if(checkAllInputs(view)){
                    if (con != null) {
                        loadUserData()
                        if(newPass.toString() == confirmPass.toString()){
                            if(db?.updateUserTable("$newPass", imageUrl,userId) == true){
                                GlobalScope.launch(Dispatchers.IO){
                                    withContext(Dispatchers.Default) {
                                        replaceFragment(CollectionFragment())
                                    }
                                }
                            }
                        }
                        else{
                            errorMsg.visibility = View.VISIBLE
                        }
                    }
                }

            }
        }
    }

    @SuppressLint("DetachAndAttachSameFragment")
    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
        val fragmentTrans = fragmentManager.beginTransaction().setCustomAnimations(R.anim.from_right, R.anim.from_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        fragmentTrans.replace(R.id.fragmentContainerView,fragment)
        fragmentTrans.detach(fragment);
        fragmentTrans.attach(fragment);
        fragmentTrans.commit()
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    private fun setImageLink(profileImage: ImageButton) {

        if(profileImage.drawable != null) {
            GlobalScope.launch(Dispatchers.IO) {
                storageCon.blobConnection(
                    imageUri,
                    cont,
                    "users",
                    "userid_${userId}_profile_image"
                )
            }
            imageUrl = storageCon.returnImageUrl("users", "userid_${userId}_profile_image")
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

    private fun loadUserData(){
        val sp = this.activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            userId = sp.getInt("userId", 0)
            imageUrl = sp.getString("imageUrl", "").toString()
        }

    }

}