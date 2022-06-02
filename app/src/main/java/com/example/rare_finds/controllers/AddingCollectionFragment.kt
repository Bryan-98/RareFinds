package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.rare_finds.R
import com.google.android.material.textfield.TextInputLayout
import edu.practice.utils.shared.com.example.rare_finds.fragments.CollectionFragment
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.BlobConnection
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper
import kotlinx.coroutines.*

class AddingCollectionFragment : DialogFragment() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private val storageCon = BlobConnection()
    private val table = "Collection"
    private val col = "CollName,CollDesc,CollGenre,UserId,ImageUrl"
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageUrl :String;
    private lateinit var imageUri: Uri
    private lateinit var cont: ContentResolver
    private lateinit var til : TextInputLayout;
    private lateinit var genre: String
    private lateinit var name : Editable;
    private lateinit var des : Editable;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_adding_collection, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.collection_name)
        view.apply{

            val genres = resources.getStringArray(R.array.genre)
            val spinner = view.findViewById<Spinner>(R.id.genre_spinner)
            val editTextName = view.findViewById<EditText>(R.id.collection_name)
            val editTextDes = view.findViewById<EditText>(R.id.collection_description)
            val img = view.findViewById<ImageButton>(R.id.library_image_selected)
            val addImageIcon = findViewById<ImageView>(R.id.add_image_icon)

            name = editTextName.text
            des = editTextDes.text

            ArrayAdapter(this.context, R.layout.dropdown_item,genres).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object :

                    AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        genre = genres[p2]
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                }
            }

            galleryLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val source = result.data?.data?.let { ImageDecoder.createSource((activity as AppCompatActivity).contentResolver, it) }
                        val bitmap = source?.let { ImageDecoder.decodeBitmap(it) }
                        img.setImageBitmap(bitmap)
                        addImageIcon.visibility = View.GONE
                        imageUri = result.data?.data!!
                        cont = (activity as AppCompatActivity).contentResolver
                    }
                }

            img.setOnClickListener{
                val cameraIntent = Intent(Intent.ACTION_PICK)
                cameraIntent.type = "image/*"
                galleryLauncher.launch(cameraIntent)
            }

            val closeBtn = view.findViewById<ImageButton>(R.id.collection_cancel_btn)
            closeBtn.setOnClickListener{
                dismiss()
            }

            val submitBtn = view.findViewById<Button>(R.id.collection_add_btn)
            submitBtn.setOnClickListener {
                clearAllInputs(view)
                if(checkAllInputs(view)){
                    if (con != null) {
                        val userId = loadUserData()
                        setImageLink(img)
                        db?.insertTable(table, col, "'${name}','${des}','${genre}', $userId,'${imageUrl}'")
                    }
                    GlobalScope.launch(Dispatchers.IO){

                            withContext(Dispatchers.Default) {
                                replaceFragment(CollectionFragment())
                            }
                            withContext(Dispatchers.Default) { dismiss() }
                    }
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
        val fragmentTrans = fragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragmentContainerView,fragment)
        fragmentTrans.commit()
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    private fun setImageLink(profileImage: ImageButton) {

        when (profileImage.drawable) {
            null -> {
                imageUrl = BlobConnection().returnImageUrl("collection", "default")
            }
            else -> {
                val userCount = db?.checkCount("CollId", "Collection")?.plus(1)
                GlobalScope.launch(Dispatchers.IO) {
                    storageCon.blobConnection(
                        imageUri,
                        cont,
                        "collection",
                        "userid_${loadUserData()}_colid_${userCount}_collection_image"
                    )
                }
                imageUrl = storageCon.returnImageUrl("collection", "userid_${loadUserData()}_colid_${userCount}_collection_image")
            }
        }
    }

    private fun checkAllInputs(view: View):Boolean {
        var count = 0
        if (name.isBlank()) {
            til = view.findViewById(R.id.input_collection_name);
            til.isErrorEnabled = true
            til.error = "Enter a Name"
            count++
        }
        if (des.isBlank()) {
            til = view.findViewById(R.id.input_collection_description);
            til.isErrorEnabled = true
            til.error = "Enter a Description"
            count++
        }
        if(count > 0) return false

        return true
    }

    private fun clearAllInputs(view: View) {

        if(name.isNotBlank()){
            til = view.findViewById(R.id.input_collection_name);
            til.isErrorEnabled = false
        }
        if(des.isNotBlank()){
            til = view.findViewById(R.id.input_collection_description);
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