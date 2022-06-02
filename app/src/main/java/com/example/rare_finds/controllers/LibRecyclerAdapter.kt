package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.com.example.rare_finds.models.Library
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper
import java.io.Serializable

class LibRecyclerAdapter(colId : Int): RecyclerView.Adapter<LibRecyclerAdapter.ViewHolder>(), Filterable {

    private var libList : ArrayList<Library>
    private var libListFil : ArrayList<Library>
    private lateinit var listener : OnItemClickListener
    private var lib : ArrayList<Library>

    init {
        val con = ConnectionHelper().dbConn()
        val db = con?.let { DatabaseHelper(it) }
        lib = db?.fillLibraryList(colId)!!
        libList = lib
        libListFil = lib
        notifyDataSetChanged()
    }

    interface OnItemClickListener{
        fun onItemClick(item: Serializable)
        fun onLongItemClick(item: Serializable): Boolean
    }

    fun setOnItemClickListener(listener : OnItemClickListener){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_view, parent, false)
        return ViewHolder(v, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = libList[position].libName
        holder.itemDescription.text = libList[position].libDescription
        Picasso.get().load(libList[position].imageUrl).fit().into(holder.itemImage)
    }

    override fun getItemCount(): Int {
        return libList.size
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        var itemName: TextView = itemView.findViewById(R.id.category_name_id)
        var itemDescription: TextView = itemView.findViewById(R.id.category_description_id)
        var itemImage: ImageView = itemView.findViewById(R.id.category_image_id)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(libList[adapterPosition])
            }
            itemView.setOnLongClickListener {
                listener.onLongItemClick(libList[adapterPosition])
            }
        }
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if(p0 == null || p0.length < 0){
                    filterResults.count = libListFil.size
                    filterResults.values = libListFil
                }else{
                    val searchChr = p0.toString().lowercase()
                    val itemModel = ArrayList<Library>()
                    when(searchChr){
                        "any" -> {
                            lib.forEach { itemModel.add(it) }
                        }
                        "a-z" -> {
                            val sorted = lib.sortedWith(compareBy { it.libName })
                            sorted.forEach{itemModel.add(it)}
                        }
                        "old" -> {
                            val sorted = lib.sortedBy { it.libYear }
                            sorted.forEach{itemModel.add(it)}
                        }
                        "new" -> {
                            val sorted = lib.sortedByDescending { it.libYear }
                            sorted.forEach{itemModel.add(it)}
                        }
                        else -> {
                            for (item in libListFil) {
                                if (item.libName.lowercase()
                                        .contains(searchChr) || item.libDescription.lowercase()
                                        .contains(searchChr) || item.libGenre.lowercase().contains(searchChr)
                                ) {
                                    itemModel.add(item)
                                }
                            }
                        }
                    }
                    filterResults.count = itemModel.size
                    filterResults.values = itemModel
                }
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                libList = filterResults!!.values as ArrayList<Library>
                notifyDataSetChanged()
            }

        }
    }
}