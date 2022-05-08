package edu.practice.utils.shared.com.example.rare_finds.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.practice.utils.shared.com.example.rare_finds.controllers.AddingCategory
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.controllers.RecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import edu.practice.utils.shared.com.example.rare_finds.models.SqlInfo
import edu.practice.utils.shared.com.example.rare_finds.models.User
import java.io.Serializable
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter :RecyclerAdapter
    private var userId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //load userInfo
        userId = loadUserData()

        //Recycler View
        recyclerSetup(userId)

        //Adding Category
        val btnAddingCategory: View = findViewById(R.id.floatingActionButton)
        btnAddingCategory.setOnClickListener{
            val cat = Intent(this, AddingCategory::class.java)
            val category = Bundle()
            category.putSerializable("sqlDb", sendToCategory(userId))
            cat.putExtras(category)
            startActivity(cat)
            finish()
        }
    }

    private fun sendToCategory(userId: Int): SqlInfo {
        return SqlInfo("Collection", "CollName,CollDesc,CollGenre,UserId", "collection", userId)
    }

    private fun recyclerSetup(userInfo: Int) {
        adapter = RecyclerAdapter(userInfo)
        //Category View
        layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.categoryList)
        recyclerView.layoutManager = layoutManager
        val intent = Intent(this, LibraryActivity::class.java)
        adapter.setOnItemClickListener(object: RecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Serializable) {
                val colId = item as Collection
                sharedUserPref(colId.colId)
                val coll = Bundle()
                coll.putSerializable("colInfo", item)
                intent.putExtras(coll)
                startActivity(intent)
            }
        })
        recyclerView.adapter = adapter
    }

    private fun loadUserData():Int{
        val sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        return sp.getInt("userId", 0)
    }

    private fun sharedUserPref(colId:Int){
        val sp = getSharedPreferences("collInfo", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.apply{
            putInt("colId",colId)
        }.apply()
    }
}
