package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.rare_finds.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.practice.utils.shared.com.example.rare_finds.fragments.CollectionFragment
import edu.practice.utils.shared.com.example.rare_finds.fragments.LibraryFragment
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.BlobConnection
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.net.URL

class AddingLibraryFragment : DialogFragment() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private val storageCon = BlobConnection()
    private  val table = "Library"
    private  val col = "LibName,LibDesc,LibYear, LibPrice, LibPublisher, LibGenre, ImageUrl, CollId"
    private lateinit var genre: String
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageUrl :String;
    private lateinit var imageUri: Uri
    private lateinit var cont: ContentResolver
    private lateinit var til : TextInputLayout;
    private lateinit var name : Editable;
    private lateinit var des : Editable;
    private lateinit var price : Editable;
    private lateinit var year : Editable;
    private lateinit var publisher : Editable;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_adding_library, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.library_name)
        view.apply{

            val genres = resources.getStringArray(R.array.game_genre)
            val spinner = view.findViewById<Spinner>(R.id.genre_spinner)
            val editTextName = view.findViewById<EditText>(R.id.library_name)
            val editTextDes = view.findViewById<EditText>(R.id.library_description)
            val editTextPrice = view.findViewById<EditText>(R.id.library_price)
            val editTextYear = view.findViewById<EditText>(R.id.library_year)
            val editTextPublisher = view.findViewById<EditText>(R.id.library_publisher)
            val img = view.findViewById<ImageButton>(R.id.libray_image_selected)

            name = editTextName.text
            des = editTextDes.text
            price = editTextPrice.text
            year = editTextYear.text
            publisher = editTextPublisher.text

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
                        imageUri = result.data?.data!!
                        cont = (activity as AppCompatActivity).contentResolver
                    }
                }

            img.setOnClickListener{
                val cameraIntent = Intent(Intent.ACTION_PICK)
                cameraIntent.type = "image/*"
                galleryLauncher.launch(cameraIntent)
            }

            val closeBtn = view.findViewById<ImageButton>(R.id.cancel_btn)
            closeBtn.setOnClickListener{
                dismiss()
            }

            val submitBtn = view.findViewById<Button>(R.id.library_add_btn)
            submitBtn.setOnClickListener {
                clearAllInputs(view)
                if(checkAllInputs(view)) {
                    if (con != null) {
                        val colId = loadColData()
                        setImageLink(img)
                        db?.insertTable(
                            table,
                            col,
                            "'${name}', '${des}', ${year}, ${price}, '${publisher}', '${genre}', '${imageUrl}', $colId"
                        )
                    }
                    GlobalScope.launch(Dispatchers.IO){

                        withContext(Dispatchers.Default) {
                            replaceFragment(LibraryFragment())
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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setImageLink(profileImage: ImageButton) {

        if(profileImage.drawable == null) {
            imageUrl = BlobConnection().returnImageUrl("library", "default")
            val url = URL(imageUrl)
            val image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            val act = (activity as AppCompatActivity)
            imageUri = getImageUriFromBitmap(act, image)
            cont = act.contentResolver
            storeImage()
        }
        else{
            storeImage()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    private fun storeImage(){
        val userCount = db?.checkCount("LibId", "Library")?.plus(1)
        GlobalScope.launch(Dispatchers.IO) {
            storageCon.blobConnection(
                imageUri,
                cont,
                "library",
                "userid_${loadUserData()}_libid_${userCount}_library_image"
            )
        }
        imageUrl = storageCon.returnImageUrl("library", "userid_${loadUserData()}_libid_${userCount}_library_image")
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    private fun checkAllInputs(view: View):Boolean {
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
        if (price.isBlank()) {
            til = view.findViewById(R.id.input_library_price);
            til.isErrorEnabled = true
            til.error = "Enter a Description";
            count++
        }
        if (year.isBlank()) {
            til = view.findViewById(R.id.input_library_year);
            til.isErrorEnabled = true
            til.error = "Enter a Description";
            count++
        }
        if (publisher.isBlank()) {
            til = view.findViewById(R.id.input_library_publisher);
            til.isErrorEnabled = true
            til.error = "Enter a Description";
            count++
        }
        if(count > 0) return false

        return true
    }

    private fun clearAllInputs(view: View) {

        if(name.isNotBlank()){
            til = view.findViewById(R.id.input_library_name);
            til.isErrorEnabled = false
        }
        if(des.isNotBlank()){
            til = view.findViewById(R.id.input_library_description);
            til.isErrorEnabled = false
        }
        if(price.isNotBlank()){
            til = view.findViewById(R.id.input_library_price);
            til.isErrorEnabled = false
        }
        if(year.isNotBlank()){
            til = view.findViewById(R.id.input_library_year);
            til.isErrorEnabled = false
        }
        if(publisher.isNotBlank()){
            til = view.findViewById(R.id.input_library_publisher);
            til.isErrorEnabled = false
        }
    }

    private fun loadColData():Int{
        val sp = this.activity?.getSharedPreferences("colInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            return sp.getInt("colId", 0)
        }
        return 0
    }
    private fun loadUserData():Int{
        val sp = this.activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            return sp.getInt("userId", 0)
        }
        return 0
    }

}