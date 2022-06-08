package com.example.rare_finds.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import com.example.rare_finds.connection.ConnectionHelper
import com.example.rare_finds.connection.DatabaseHelper
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.com.example.rare_finds.controllers.AddCommentFragment
import edu.practice.utils.shared.com.example.rare_finds.controllers.ComRecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.fragments.LibraryFragment
import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import edu.practice.utils.shared.com.example.rare_finds.models.Comment
import edu.practice.utils.shared.com.example.rare_finds.models.Library
import edu.practice.utils.shared.com.example.rare_finds.models.Search
import kotlinx.coroutines.*
import java.io.Serializable
import kotlin.properties.Delegates

private const val ARG_PARAM1 = "SearchedItemInfo"

class SearchedItemFragment : DialogFragment() {
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter : ComRecyclerAdapter
    private lateinit var name: String
    private lateinit var des: String
    private lateinit var publisher: String
    private lateinit var genre: String
    private lateinit var imageUrl: String
    val con = ConnectionHelper().dbConn()
    val db = con?.let { DatabaseHelper(it) }
    private lateinit var collectionNamesList: ArrayList<String>
    private lateinit var collectionName: String
    private var com = arrayListOf<Comment>()
    private var col = arrayListOf<Collection>()
    private val libEntry = "LibName,LibDesc,LibYear, LibPrice, LibPublisher, LibGenre, ImageUrl, CollId"

    private var itemId by Delegates.notNull<Long>()
    private var year by Delegates.notNull<String>()
    private var price by Delegates.notNull<Int>()
    private var itemInfo: Serializable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemInfo = it.getSerializable(ARG_PARAM1)
            val item = itemInfo as Search
            name = item.searchName.replace("'", "")
            des = item.colDescription.replace("'", "")
            year = item.yearRelease
            price = 0
            publisher = item.searchPublisher.replace("'", "")
            genre = item.genre
            imageUrl = item.imageUrl
            itemId = item.searchId
            collectionNamesList = ArrayList()
            collectionName = ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_searched_item, container, false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = name
        view.apply {
            val editDes = view.findViewById<TextView>(R.id.description)
            val editYear = view.findViewById<TextView>(R.id.releaseDate)
            val editPrice = view.findViewById<TextView>(R.id.price)
            val editPublish = view.findViewById<TextView>(R.id.publisher)
            val editGenre = view.findViewById<TextView>(R.id.genre)
            val image = view.findViewById<ImageView>(R.id.itemImage)
            val spinner = view.findViewById<Spinner>(R.id.collectionSpinner)

            editDes.text = des
            editYear.text = year
            editPrice.text = price.toString()
            editPublish.text = publisher
            editGenre.text = genre
            Picasso.get().load(imageUrl).fit().into(image)


            col = db?.fillCollectionList(loadUserData())!!

            col.forEach{ collectionNamesList.add(it.colName) }

            ArrayAdapter(this.context, R.layout.dropdown_item,collectionNamesList).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object :

                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        collectionName = collectionNamesList[p2]
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                }
            }

            loadCommentList(view)

            val btn = view.findViewById<Button>(R.id.button)
            btn.setOnClickListener{
                val colId = loadColData()
                db.insertTable(
                    "Library", libEntry,
                    "'${name}', '${des}', ${year.removeRange(4, year.length)}, ${price}, '${publisher}', '${genre}', '${imageUrl}', $colId"
                )
                GlobalScope.launch(Dispatchers.IO){

                    withContext(Dispatchers.Default) {
                        replaceFragment(LibraryFragment())
                    }
                    withContext(Dispatchers.Default) { dismiss() }
                }
            }
        }
    }

    private fun loadCommentList(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.commentView)
        adapter = ComRecyclerAdapter(name)
        layoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
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

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
        val fragmentTrans = fragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragmentContainerView,fragment)
        fragmentTrans.commit()
    }

}