package com.example.rare_finds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Category View
        layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.categoryList)
        recyclerView.layoutManager = layoutManager
        adapter = RecyclerAdapter()
        recyclerView.adapter = adapter

        //Adding Category
        val btnAddingCategory: View = findViewById(R.id.floatingActionButton)
        btnAddingCategory.setOnClickListener{
            val intent = Intent(this, AddingCategory::class.java)
            var category = Bundle()
            category.putSerializable("sqlDb", sendToCategory())
            intent.putExtras(category)
            startActivity(intent)
        }
    }

    private fun sendToCategory(): SqlInfo {
        return SqlInfo("Category", "CategoryName,CategoryDescription", "category")
    }
}
