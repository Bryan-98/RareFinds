package com.example.rare_finds.controllers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.rare_finds.R
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.com.example.rare_finds.fragments.LibraryFragment
import edu.practice.utils.shared.com.example.rare_finds.models.Library
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.BlobConnection
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper
import kotlinx.coroutines.*
import java.io.Serializable
import kotlin.properties.Delegates

private const val ARG_PARAM1 = "libraryInfo"

class UpdateLibraryFragment : Fragment() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private val storageCon = BlobConnection()
    private var libInfo: Serializable? = null
    private lateinit var imageUrl: String
    private lateinit var name: String
    private lateinit var des: String
    private var price by Delegates.notNull<Int>()
    private var year by Delegates.notNull<Int>()
    private lateinit var pub: String
    private var libId by Delegates.notNull<Int>()
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageUri: Uri
    private lateinit var cont: ContentResolver
    private lateinit var til: TextInputLayout;
    private lateinit var addImageIcon: ImageButton;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            libInfo = it.getSerializable(ARG_PARAM1)
            val lib = libInfo as Library
            name = lib.libName
            des = lib.libDescription
            price = lib.libPrice
            year = lib.libYear
            pub = lib.libPublisher
            libId = lib.libId
            imageUrl = lib.imageUrl

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_library, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.update_Lib)
        view.apply {
            val editTextName = view.findViewById<EditText>(R.id.library_name)
            val editTextDes = view.findViewById<EditText>(R.id.library_description)
            val editTextPrice = view.findViewById<EditText>(R.id.library_price)
            val editTextYear = view.findViewById<EditText>(R.id.library_year)
            val editTextPub = view.findViewById<EditText>(R.id.library_publisher)
            addImageIcon = findViewById(R.id.libray_image_selected)

            editTextName.setText(name)
            editTextDes.setText(des)
            editTextPrice.setText(price.toString())
            editTextYear.setText(year.toString())
            editTextPub.setText(pub)

            Picasso.get().load(imageUrl).fit()
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(addImageIcon)


            galleryLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val source = result.data?.data?.let {
                            ImageDecoder.createSource(
                                (activity as AppCompatActivity).contentResolver, it)
                        }
                        val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                        addImageIcon.setImageBitmap(bitmap)
                        imageUri = result.data?.data!!
                        cont = (activity as AppCompatActivity).contentResolver
                        setImageLink(addImageIcon)
                    }
                }

            addImageIcon.setOnClickListener {
                val cameraIntent = Intent(Intent.ACTION_PICK)
                cameraIntent.type = "image/*"
                galleryLauncher.launch(cameraIntent)
            }

            val deleteBtn = view.findViewById<Button>(R.id.delete_btn)
            deleteBtn.setOnClickListener {
                if (con != null) {
                    if(db?.deleteEntry("Library", "LibId", libId) == true){
                        GlobalScope.launch(Dispatchers.IO){
                            withContext(Dispatchers.Default) {
                                replaceFragment(LibraryFragment())
                            }
                        }
                    }
                }
            }

            val updateBtn = view.findViewById<Button>(R.id.update_btn)
            updateBtn.setOnClickListener {
                clearAllInputs(view)
                if (checkAllInputs(view)) {
                    if (con != null) {
                        name = editTextName.text.toString()
                        des = editTextDes.text.toString()
                        price = editTextPrice.text.toString().toInt()
                        year = editTextYear.text.toString().toInt()
                        pub = editTextPub.text.toString()
                        db?.updateLibTable(name, des, year, price, pub, imageUrl, libId)
                    }
                    GlobalScope.launch(Dispatchers.IO){
                        withContext(Dispatchers.Default) {
                            replaceFragment(LibraryFragment())
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
                    "library",
                    "userid_${loadUserData()}_libid_${libId}_library_image"
                )
            }
            imageUrl = storageCon.returnImageUrl(
                "library",
                "userid_${loadUserData()}_libid_${libId}_library_image"
            )
        }
    }

    private fun checkAllInputs(view: View): Boolean {
        var count = 0
        if (name.isBlank()) {
            til = view.findViewById(R.id.input_library_name);
            til.isErrorEnabled = true
            til.error = "Enter a Name";
            count++
        }
        if (des.isBlank()) {
            til = view.findViewById(R.id.input_library_description);
            til.isErrorEnabled = true
            til.error = "Enter a Description";
            count++
        }
        if (year.equals(null)) {
            til = view.findViewById(R.id.input_library_year);
            til.isErrorEnabled = true
            til.error = "Enter a Year";
            count++
        }
        if (price.equals(null)) {
            til = view.findViewById(R.id.input_library_price);
            til.isErrorEnabled = true
            til.error = "Enter a Price";
            count++
        }
        if (pub.isBlank()) {
            til = view.findViewById(R.id.input_library_publisher);
            til.isErrorEnabled = true
            til.error = "Enter a Publisher";
            count++
        }
        if (count > 0) return false

        return true
    }

    private fun clearAllInputs(view: View) {

        if (name.isNotBlank()) {
            til = view.findViewById(R.id.input_library_name);
            til.isErrorEnabled = false
        }
        if (des.isNotBlank()) {
            til = view.findViewById(R.id.input_library_description);
            til.isErrorEnabled = false
        }
        if (price.equals(null)) {
            til = view.findViewById(R.id.input_library_price);
            til.isErrorEnabled = false
        }
        if (year.equals(null)) {
            til = view.findViewById(R.id.input_library_year);
            til.isErrorEnabled = false
        }
        if (pub.isNotBlank()) {
            til = view.findViewById(R.id.input_library_publisher);
            til.isErrorEnabled = false
        }
    }

    private fun loadUserData(): Int {
        val sp = this.activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            return sp.getInt("userId", 0)
        }
        return 0
    }
}