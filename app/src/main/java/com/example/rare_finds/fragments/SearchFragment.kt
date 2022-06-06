package edu.practice.utils.shared.com.example.rare_finds.fragments

import android.content.Context
import android.location.GnssAntennaInfo
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.controller.SearchRecycler
import edu.practice.utils.shared.com.example.rare_finds.models.Search
import kotlin.properties.Delegates
import java.io.Serializable

private lateinit var layoutManager: RecyclerView.LayoutManager
private lateinit var adapter : SearchRecycler
//private lateinit var searchViewQuery : SearchView
private var userId by Delegates.notNull<Int>()

class SearchFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = loadUserData()
    }

    override fun onCreateView(
        layout: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) :View? {
        return layout.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.collection_name)
        view.apply{
            val recyclerView = view.findViewById<RecyclerView>(R.id.searchView)
            adapter = SearchRecycler("Mario")
            layoutManager = LinearLayoutManager(this.context)
            recyclerView.layoutManager = layoutManager
            val act = view.context as AppCompatActivity
            adapter.setOnItemClickListener(object: SearchRecycler.OnItemClickListener {
                override fun onItemClick(item: Serializable) {
                    val libFragment = LibraryFragment()

                    act.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.from_right, R.anim.from_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragmentContainerView,libFragment).addToBackStack(null)
                        .commit()
                }
                override fun onLongItemClick(item: Serializable): Boolean{
                    return true
                }
            })
            recyclerView.adapter = adapter

//            searchViewQuery = view.findViewById(R.id.searchBar)
//            searchViewQuery.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//                override fun onQueryTextSubmit(query: String): Boolean {
//                    adapter.filter.filter(query)
//                    return true
//                }
//
//                override fun onQueryTextChange(newText: String): Boolean {
//                    adapter.filter.filter(newText)
//                    return true
//                }
//            })
        }
    }
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        val searchItem = menu.findItem(R.id.searchView)
//        val searchView = searchItem.actionView as SearchView
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.collection_name)
//                view?.apply{
//                    val recyclerView = menu.findItem(R.id.searchView)
//                    adapter = SearchRecycler(query)
//                    layoutManager = LinearLayoutManager(this.context)
//                    recyclerView.actionView = layoutManager1
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                // Do whatever you need when text changes.
//                // This will be fired every time you input any character.
//                return false
//            }
//        })
//    }

    private fun loadUserData():Int{
        val sp = this.activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            return sp.getInt("userId", 0)
        }
        return 0
    }
}
