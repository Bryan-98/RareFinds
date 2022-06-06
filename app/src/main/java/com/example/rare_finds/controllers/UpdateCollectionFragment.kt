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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.rare_finds.R
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.com.example.rare_finds.fragments.CollectionFragment
import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import com.example.rare_finds.connection.BlobConnection
import com.example.rare_finds.connection.ConnectionHelper
import com.example.rare_finds.connection.DatabaseHelper
import kotlinx.coroutines.*
import java.io.Serializable
import kotlin.properties.Delegates


private const val ARG_PARAM1 = "collectionInfo"

class UpdateCollectionFragment : Fragment() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private val storageCon = BlobConnection()
    private lateinit var name: String
    private lateinit var des: String
    private lateinit var imageUrl: String
    private var colId by Delegates.notNull<Int>()
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageUri: Uri
    private lateinit var cont: ContentResolver
    private lateinit var til: TextInputLayout
    private lateinit var addImageIcon: ImageButton

    private var colInfo: Serializable? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            colInfo = it.getSerializable(ARG_PARAM1)
            val col = colInfo as Collection
            name = col.colName
            des = col.colDescription
            imageUrl = col.imageUrl
            colId = col.colId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_collection, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.update_Col)
        view.apply {
            val editTextName = view.findViewById<EditText>(R.id.collection_name)
            val editTextDes = view.findViewById<EditText>(R.id.collection_description)
            addImageIcon = findViewById(R.id.col_image_selected)

            editTextName.setText(name)
            editTextDes.setText(des)

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
                    if(db?.deleteEntry("Library", "CollId", colId) == true && db.deleteEntry("Collection", "CollId", colId)){
                        GlobalScope.launch(Dispatchers.IO){
                            withContext(Dispatchers.Default) {
                                replaceFragment(CollectionFragment())
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
                        setImageLink(addImageIcon)
                        db?.updateColTable(name, des, imageUrl, colId)

                    }
                    GlobalScope.launch(Dispatchers.IO){
                        withContext(Dispatchers.Default) {
                            replaceFragment(CollectionFragment())
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
        fragmentTrans.detach(fragment)
        fragmentTrans.attach(fragment)
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
                    "collection",
                    "userid_${loadUserData()}_colid_${colId}_collection_image"
                )
            }
            imageUrl = storageCon.returnImageUrl(
                "collection",
                "userid_${loadUserData()}_colid_${colId}_collection_image"
            )
        }
    }

    private fun checkAllInputs(view: View): Boolean {
        var count = 0
        if (name.isBlank()) {
            til = view.findViewById(R.id.input_collection_name)
            til.isErrorEnabled = true
            til.error = "Enter a Name"
            count++
        }
        if (des.isBlank()) {
            til = view.findViewById(R.id.input_collection_description)
            til.isErrorEnabled = true
            til.error = "Enter a Description"
            count++
        }
        if (count > 0) return false

        return true
    }

    private fun clearAllInputs(view: View) {

        if (name.isNotBlank()) {
            til = view.findViewById(R.id.input_collection_name)
            til.isErrorEnabled = false
        }
        if (des.isNotBlank()) {
            til = view.findViewById(R.id.input_collection_description)
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