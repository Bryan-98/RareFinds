package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.activities.MainActivity
import edu.practice.utils.shared.com.example.rare_finds.models.SqlInfo
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper

class AddingCategory : AppCompatActivity() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private val backIntent = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_category)

        //Get text fields
        val editTextName = findViewById<EditText>(R.id.input_category_name)
        val editTextDescription = findViewById<EditText>(R.id.inpur_category_description)

        //Get intent info
        val sqlData = intent.getSerializableExtra("sqlDb") as SqlInfo
        val table = sqlData.table
        val col = sqlData.col
        val act = sqlData.activity
        val userId = sqlData.userId
        val userBack = intent.getSerializableExtra(backActivity(act))


        //Cancel button
        val btnPicture: View = findViewById(R.id.imageButton)
        btnPicture.setOnClickListener{

        }

        //Cancel button
        val btnCancel: View = findViewById(R.id.cancel_button)
        btnCancel.setOnClickListener{
            val intent = getActivity(act)
            backIntent.putSerializable(backActivity(act),userBack)
            intent.putExtras(backIntent)
            startActivity(intent)
            finish()
        }

        //Add button
        val btnAdd: View = findViewById(R.id.add_button)
        btnAdd.setOnClickListener{
            if (con != null) {
                db?.insertTable(table, col, "'${editTextName.text}','${editTextDescription.text}','Music', $userId")
            }
            val intent = getActivity(act)
            backIntent.putSerializable(backActivity(act),userBack)
            intent.putExtras(backIntent)
            startActivity(intent)

        }
    }

    private fun backActivity(activity:String):String{
        when(activity){
            "collection" -> return "userInfo"
            "library" -> return "colInfo"
        }
        return ""
    }

    private fun getActivity(activity:String):Intent{
        var intent = Intent()

        when(activity){
            "collection" -> intent = Intent(this, MainActivity::class.java)
            "library" -> intent = Intent(this, MainActivity::class.java)
        }

        return intent
    }
}