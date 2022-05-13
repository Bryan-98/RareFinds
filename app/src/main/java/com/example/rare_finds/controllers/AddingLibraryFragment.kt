package com.example.rare_finds.controllers

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.rare_finds.R
import com.google.android.material.textfield.TextInputEditText
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper

private val con = ConnectionHelper().dbConn()
private val db = con?.let { DatabaseHelper(it) }
private const val table = "Library"
private const val col = "LibName,LibDesc,LibGenre,CollId"
private lateinit var genre: String

class AddingLibraryFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_adding_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.library_name)
        view.apply{
            val genres = resources.getStringArray(R.array.genre)
            val spinner = view.findViewById<Spinner>(R.id.genre_spinner)
            ArrayAdapter(this.context, R.layout.dropdown_item,genres).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
                spinner.onItemSelectedListener = object :

                    AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        genre = genres[p2]
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }

                }
            }

//            val closeBtn = view.findViewById<ImageButton>(R.id.collection_cancel_btn)
//            closeBtn.setOnClickListener{
//                dismiss()
//            }

            val submitBtn = view.findViewById<Button>(R.id.library_add_btn)
            submitBtn.setOnClickListener {
                if (con != null) {
                    val name = view.findViewById<TextInputEditText>(R.id.collection_name)
                    val des = view.findViewById<TextInputEditText>(R.id.collection_description)
                    val userId = loadUserData()
                    db?.insertTable(table, col, "'${name.text}','${des.text}','${genre}', $userId")
                }
                dismiss()
            }
        }
    }

    private fun loadUserData():Int{
        val sp = this.activity?.getSharedPreferences("colInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            return sp.getInt("colInfo", 0)
        }
        return 0
    }

}