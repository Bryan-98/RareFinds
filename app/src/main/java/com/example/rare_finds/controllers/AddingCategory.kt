package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.activities.CollectionActivity
import edu.practice.utils.shared.com.example.rare_finds.activities.MainActivity
import edu.practice.utils.shared.com.example.rare_finds.models.SqlInfo
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper

class AddingCategory : AppCompatActivity() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_category)

        //Get text fields
        val editTextName = findViewById<EditText>(R.id.input_category_name)
        val editTextDescription = findViewById<EditText>(R.id.inpur_category_description)

        //Cancel button
        val btnCancel: View = findViewById(R.id.cancel_button)
        btnCancel.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Add button
        val btnAdd: View = findViewById(R.id.add_button)
        btnAdd.setOnClickListener{
            val sqlData = intent.getSerializableExtra("sqlDb") as SqlInfo
            val table = sqlData.table
            val col = sqlData.col
            val act = sqlData.activity
            if (con != null) {
                db?.insertTable(table, col, "'${editTextName.text}','${editTextDescription.text}'")
            }
            val intent = getActivity(act)
            startActivity(intent)
            finish()
        }
    }

    private fun getActivity(activity:String):Intent{
        var intent = Intent()

        when(activity){
            "category" -> intent = Intent(this, MainActivity::class.java)
            "collection" -> intent = Intent(this, CollectionActivity::class.java)
        }

        return intent
    }
}