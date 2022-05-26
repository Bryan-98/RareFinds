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
import edu.practice.utils.shared.com.example.rare_finds.controllers.UpdateCollectionFragment
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
        val act = view.context as AppCompatActivity
        adapter.setOnItemClickListener(object: RecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Serializable) {
                val colId = item as Collection
                sharedUserPref(colId.colId)
                val libFragment = LibraryFragment()
                act.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.from_right, R.anim.from_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragmentContainerView,libFragment).addToBackStack(null)
                    .commit()
            }
            override fun onLongItemClick(item: Serializable): Boolean{
                val colBundle = Bundle()
                val colId = item as Collection
                colBundle.putSerializable("collectionInfo", colId)
                val updateFrag = UpdateCollectionFragment()
                updateFrag.arguments = colBundle
                act.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.from_right, R.anim.from_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.fragmentContainerView,updateFrag).addToBackStack(null)
                    .commit()
                return true
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