package edu.practice.utils.shared.com.example.rare_finds.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import com.example.rare_finds.fragments.SearchedItemFragment
import edu.practice.utils.shared.com.example.rare_finds.controllers.SearchRecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.models.Search
import java.io.Serializable
import kotlin.properties.Delegates

class SearchFragment : Fragment() {
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter : SearchRecyclerAdapter
    private lateinit var searchViewQuery : SearchView
    private var userId by Delegates.notNull<Int>()

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
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.search_frag)
        view.apply{
            val recyclerView = view.findViewById<RecyclerView>(R.id.searchView)
            adapter = SearchRecyclerAdapter("Mario")
            layoutManager = LinearLayoutManager(this.context)
            recyclerView.layoutManager = layoutManager
            val act = view.context as AppCompatActivity
            adapter.setOnItemClickListener(object: SearchRecyclerAdapter.OnItemClickListener {
                override fun onItemClick(item: Serializable) {
                    val itemBundle = Bundle()
                    val sear = item as Search
                    itemBundle.putSerializable("SearchedItemInfo", sear)

                    val srchdItemFragment = SearchedItemFragment()
                    srchdItemFragment.arguments = itemBundle
                    act.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.from_right, R.anim.from_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragmentContainerView,srchdItemFragment)
                        .commit()

                }
                override fun onLongItemClick(item: Serializable): Boolean{
                    return true
                }
            })
            recyclerView.adapter = adapter

            searchViewQuery = view.findViewById(R.id.searchBar)
            searchViewQuery.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    adapter.filter.filter(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    adapter.filter.filter(newText)
                    return true
                }
            })
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
