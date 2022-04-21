package edu.practice.utils.shared.com.example.rare_finds.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.models.Category

class CollectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        val name: TextView = findViewById(R.id.textView3)
        val dec = findViewById<TextView>(R.id.textView4)

        val item = intent.getSerializableExtra("info") as Category

        name.text = item.catName
        dec.text = item.catDescription
    }
}