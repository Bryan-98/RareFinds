package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import edu.practice.utils.shared.com.example.rare_finds.models.Library
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper
import java.io.Serializable

class LibRecyclerAdapter(colId : Int): RecyclerView.Adapter<LibRecyclerAdapter.ViewHolder>() {

    private lateinit var libList : ArrayList<Library>
    private lateinit var listener : OnItemClickListener

    init {
        val con = ConnectionHelper().dbConn()
        val db = con?.let { DatabaseHelper(it) }
        val lib = db?.fillLibraryList(colId)
        if (lib != null) {
            libList = lib
        }
    }

    interface OnItemClickListener{
        fun onItemClick(item: Serializable)
    }

    fun setOnItemClickListener(listener : OnItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.category_view, parent, false)
        return ViewHolder(v, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = libList[position].libName
        holder.itemDescription.text = libList[position].libDescription
    }

    override fun getItemCount(): Int {
        return libList.size
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        var itemName: TextView = itemView.findViewById(R.id.category_name_id)
        var itemDescription: TextView = itemView.findViewById(R.id.category_description_id)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(libList[adapterPosition])
            }
        }
    }
}