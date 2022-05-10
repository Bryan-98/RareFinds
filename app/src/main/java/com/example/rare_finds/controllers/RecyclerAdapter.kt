package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper
import java.io.Serializable

class RecyclerAdapter(userID : Int): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private lateinit var colList : ArrayList<Collection>
    private lateinit var listener : OnItemClickListener

    init {
        val con = ConnectionHelper().dbConn()
        val db = con?.let { DatabaseHelper(it) }
        val col = db?.fillCollectionList(userID)
        if (col != null) {
            colList = col
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
        holder.itemName.text = colList[position].colName
        holder.itemDescription.text = colList[position].colDescription
    }

    override fun getItemCount(): Int {
        return colList.size
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        var itemName: TextView = itemView.findViewById(R.id.category_name_id)
        var itemDescription: TextView = itemView.findViewById(R.id.category_description_id)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(colList[adapterPosition])
            }
        }
    }
}