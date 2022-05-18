package edu.practice.utils.shared.com.example.rare_finds.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        colId = loadColData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        adapter.setOnItemClickListener(object: LibRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Serializable) {
                val libId = item as Library
                sharedUserPref(libId.libId)
            }
        })
        recyclerView.adapter = adapter
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