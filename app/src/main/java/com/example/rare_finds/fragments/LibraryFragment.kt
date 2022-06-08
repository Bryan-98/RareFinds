package edu.practice.utils.shared.com.example.rare_finds.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.transaction
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import com.example.rare_finds.connection.ConnectionHelper
import com.example.rare_finds.connection.DatabaseHelper
import com.example.rare_finds.controllers.UpdateLibraryFragment
import com.example.rare_finds.fragments.LibraryViewFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.practice.utils.shared.com.example.rare_finds.controllers.AddingLibraryFragment
import edu.practice.utils.shared.com.example.rare_finds.controllers.LibRecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.models.Library
import java.io.Serializable
import kotlin.properties.Delegates

class LibraryFragment : Fragment() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter : LibRecyclerAdapter
    private var colId by Delegates.notNull<Int>()
    private lateinit var searchView: SearchView
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private val genres : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colId = loadColData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.library_name)
        view.apply{
            loadCollectionList(view)
            openAddingLib(view)
        }
    }

    private fun loadCollectionList(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.libraryView)
        adapter = LibRecyclerAdapter(colId)
        layoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = layoutManager

        val act = view.context as AppCompatActivity
        adapter.setOnItemClickListener(object: LibRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Serializable) {
                val libBundle = Bundle()
                val libId = item as Library
                libBundle.putSerializable("libraryInfo", libId)
                sharedUserPref(libId.libId)

                val libViewFragment = LibraryViewFragment()
                libViewFragment.arguments = libBundle
                act.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.from_right, R.anim.from_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragmentContainerView,libViewFragment).addToBackStack(null)
                    .commit()
            }

            override fun onLongItemClick(item: Serializable): Boolean{
                val libBundle = Bundle()
                val libId = item as Library
                libBundle.putSerializable("libraryInfo", libId)

                val updateFrag = UpdateLibraryFragment()
                updateFrag.arguments = libBundle
                act.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.from_right, R.anim.from_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragmentContainerView,updateFrag).addToBackStack(null)
                    .commit()
                return true
            }

        })
        recyclerView.adapter = adapter

        searchView = view.findViewById(R.id.searchView1)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        genres.add("Any")
        val libItems = db?.fillLibraryList(colId)
        libItems?.forEach{
            if(!genres.contains(it.libGenre)){
                genres.add(it.libGenre)
            }
        }

        val spinner = view.findViewById<Spinner>(R.id.spinner2)
        this.context?.let {
            ArrayAdapter(it, R.layout.dropdown_item,genres).also { ap ->
                ap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = ap
                spinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        adapter.filter.filter(genres[p2])
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
            }
        }
    }

    private fun openAddingLib(view:View){
        val btn = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        btn.setOnClickListener {
            val act = view.context as AppCompatActivity
            val dio = AddingLibraryFragment()
            dio.show(act.supportFragmentManager,"libraryDialog")
        }
    }

    private fun loadColData():Int{
        val sp = this.activity?.getSharedPreferences("colInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            return sp.getInt("colId", 0)
        }
        return 0
    }

    private fun sharedUserPref(libId:Int){
        val sp = this.activity?.getSharedPreferences("libInfo", Context.MODE_PRIVATE)
        val editor = sp?.edit()
        editor?.apply{
            putInt("libId",libId)
        }?.apply()
    }
}