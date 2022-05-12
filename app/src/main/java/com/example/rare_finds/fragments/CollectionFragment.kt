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
import edu.practice.utils.shared.com.example.rare_finds.controllers.AddingCollectionFragment
import com.example.rare_finds.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.practice.utils.shared.com.example.rare_finds.controllers.RecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import java.io.Serializable
import kotlin.properties.Delegates

class CollectionFragment : Fragment() {

    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter : RecyclerAdapter
    private var userId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = loadUserData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onPause() {
        super.onPause()
        view?.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.collection_name)
        view.apply{

            loadCollectionList(view)
            openAddingColl(view)
        }
    }

    private fun loadCollectionList(view: View){
        val recyclerView = view.findViewById<RecyclerView>(R.id.collectionView)
        adapter = RecyclerAdapter(userId)
        layoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = layoutManager
        adapter.setOnItemClickListener(object: RecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Serializable) {
                val colId = item as Collection
                sharedUserPref(colId.colId)
                val act = view.context as AppCompatActivity
                val libFragment = LibraryFragment()
                act.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.from_left,R.anim.from_left)
                    .replace(R.id.fragmentContainerView,libFragment).addToBackStack(null)
                    .commit()
            }
        })
        recyclerView.adapter = adapter
    }

    private fun openAddingColl(view:View){
        val btn = view.findViewById<FloatingActionButton>(R.id.collectionFloatingBtn)
        btn.setOnClickListener {
            val act = view.context as AppCompatActivity
            val dio = AddingCollectionFragment()
            dio.show(act.supportFragmentManager,"collectionDialog")
        }
    }

    private fun loadUserData():Int{
        val sp = this.activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            return sp.getInt("userId", 0)
        }
        return 0
    }

    private fun sharedUserPref(colId:Int){
        val sp = this.activity?.getSharedPreferences("colInfo", Context.MODE_PRIVATE)
        val editor = sp?.edit()
        editor?.apply{
            putInt("colId",colId)
        }?.apply()
    }
}