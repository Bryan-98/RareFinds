package edu.practice.utils.shared.com.example.rare_finds.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.controllers.AddingCategory
import edu.practice.utils.shared.com.example.rare_finds.controllers.LibRecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.controllers.RecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import edu.practice.utils.shared.com.example.rare_finds.models.Library
import edu.practice.utils.shared.com.example.rare_finds.models.SqlInfo
import edu.practice.utils.shared.com.example.rare_finds.models.User
import java.io.Serializable
import kotlin.properties.Delegates

class LibraryActivity : AppCompatActivity() {
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: LibRecyclerAdapter
    private var colId by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        //Recycler View
        colId = loadColData()
        recyclerSetup(colId)

        val btnAddingCategory: View = findViewById(R.id.floatingActionButton2)
        btnAddingCategory.setOnClickListener{
            val col = Intent(this, AddingCategory::class.java)
            val collection = Bundle()
            collection.putSerializable("sqlDb", sendToCategory(colId))
            col.putExtras(collection)
            startActivity(col)
            finish()
        }
    }

    private fun sendToCategory(colId: Int): SqlInfo {
        return SqlInfo("Library", "LibName,LibDesc,LibGenre,CollId", "library", colId)
    }

    private fun recyclerSetup(colId: Int) {
        adapter = LibRecyclerAdapter(colId)
        //Category View
        layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.librarylist)
        recyclerView.layoutManager = layoutManager
        val intent = Intent(this, LibraryActivity::class.java)
        adapter.setOnItemClickListener(object: LibRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Serializable) {
                val libId = item as Library
                sharedUserPref(libId.libId)
                val lib = Bundle()
                lib.putSerializable("libInfo", item)
                intent.putExtras(lib)
                startActivity(intent)
            }
        })
        recyclerView.adapter = adapter
    }

    private fun loadColData():Int{
        val sp = getSharedPreferences("collInfo", Context.MODE_PRIVATE)
        return sp.getInt("colId", 0)
    }

    private fun sharedUserPref(libId:Int){
        val sp = getSharedPreferences("libInfo", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.apply{
            putInt("libId",libId)
        }.apply()
    }
}