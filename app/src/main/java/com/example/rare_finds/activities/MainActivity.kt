package edu.practice.utils.shared.com.example.rare_finds.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.practice.utils.shared.com.example.rare_finds.controllers.AddingCategory
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.controllers.RecyclerAdapter
import edu.practice.utils.shared.com.example.rare_finds.models.SqlInfo
import edu.practice.utils.shared.com.example.rare_finds.models.User
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var adapter = RecyclerAdapter(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userInfo = intent.getSerializableExtra("userInfo") as User
        adapter = RecyclerAdapter(userInfo.userId)
        //Category View
        layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.categoryList)
        recyclerView.layoutManager = layoutManager
        val intent = Intent(this, CollectionActivity::class.java)
        adapter.setOnItemClickListener(object: RecyclerAdapter.OnItemClickListener {
            override fun onItemClick(item: Serializable) {
                val coll = Bundle()
                coll.putSerializable("info", item)
                intent.putExtras(coll)
                startActivity(intent)
            }
        })
        recyclerView.adapter = adapter

        //Adding Category
        val btnAddingCategory: View = findViewById(R.id.floatingActionButton)
        btnAddingCategory.setOnClickListener{
            val cat = Intent(this, AddingCategory::class.java)
            val category = Bundle()
            category.putSerializable("sqlDb", sendToCategory())
            cat.putExtras(category)
            startActivity(cat)
        }
    }

    private fun sendToCategory(): SqlInfo {
        return SqlInfo("Collection", "CollName,CollDesc", "category")
    }
}
