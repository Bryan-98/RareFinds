package edu.practice.utils.shared.com.example.rare_finds.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.controllers.LibRecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.controllers.RecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import edu.practice.utils.shared.com.example.rare_finds.models.User
import java.io.Serializable

class LibraryActivity : AppCompatActivity() {
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var adapter = LibRecyclerAdapter(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        val colInfo = intent.getSerializableExtra("colInfo") as Collection
        recyclerSetup(colInfo)
    }

    private fun recyclerSetup(colInfo: Collection) {
        adapter = LibRecyclerAdapter(colInfo.ColId)
        //Category View
        layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.librarylist)
        recyclerView.layoutManager = layoutManager
        val intent = Intent(this, LibraryActivity::class.java)
        adapter.setOnItemClickListener(object: LibRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Serializable) {
                val coll = Bundle()
                coll.putSerializable("info", item)
                intent.putExtras(coll)
                startActivity(intent)
            }
        })
        recyclerView.adapter = adapter
    }
}