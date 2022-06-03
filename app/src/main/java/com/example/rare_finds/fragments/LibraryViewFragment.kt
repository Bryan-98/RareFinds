package com.example.rare_finds.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.com.example.rare_finds.controllers.AddCommentFragment
import edu.practice.utils.shared.com.example.rare_finds.controllers.ComRecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.models.Library
import java.io.Serializable
import kotlin.properties.Delegates

private const val ARG_PARAM1 = "libraryInfo"

class LibraryViewFragment : Fragment() {
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter : ComRecyclerAdapter
    private lateinit var name: String
    private lateinit var des: String
    private lateinit var publisher: String
    private lateinit var genre: String
    private lateinit var imageUrl: String

    private var libId by Delegates.notNull<Int>()
    private var year by Delegates.notNull<Int>()
    private var price by Delegates.notNull<Int>()
    private var libInfo: Serializable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            libInfo = it.getSerializable(ARG_PARAM1)
            val lib = libInfo as Library
            name = lib.libName
            des = lib.libDescription
            year = lib.libYear
            price = lib.libPrice
            publisher = lib.libPublisher
            genre = lib.libGenre
            imageUrl = lib.imageUrl
            libId = lib.colId
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library_view, container, false)
    }

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

            editDes.text = des
            editYear.text = year.toString()
            editPrice.text = price.toString()
            editPublish.text = publisher
            editGenre.text = genre
            Picasso.get().load(imageUrl).fit().into(image)

            loadCommentList(view)

            val btn = view.findViewById<Button>(R.id.button)
            btn.setOnClickListener{
                val act = view.context as AppCompatActivity
                val dio = AddCommentFragment()
                val itemName = Bundle()
                itemName.putString("name", name)
                itemName.putSerializable("libraryInfo", libInfo)
                dio.arguments = itemName
                dio.show(act.supportFragmentManager,"commentDialog")
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
}