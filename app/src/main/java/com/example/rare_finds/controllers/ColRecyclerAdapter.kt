package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.com.example.rare_finds.models.Collection
import com.example.rare_finds.connection.ConnectionHelper
import com.example.rare_finds.connection.DatabaseHelper
import java.io.Serializable

class ColRecyclerAdapter(userID : Int): RecyclerView.Adapter<ColRecyclerAdapter.ViewHolder>(), Filterable{

    private var colList : ArrayList<Collection>
    private var colListFil : ArrayList<Collection>
    private lateinit var listener : OnItemClickListener
    private var col : ArrayList<Collection>

    init {
        val con = ConnectionHelper().dbConn()
        val db = con?.let { DatabaseHelper(it) }
        col = db?.fillCollectionList(userID)!!
        colList = col
        colListFil = col
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

        holder.itemName.text = colList[position].colName
        holder.itemDescription.text = colList[position].colDescription

        Picasso.get().load(colList[position].imageUrl)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .into(holder.itemImage)
    }

    override fun getItemCount(): Int {
        return colList.size
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        var itemName: TextView = itemView.findViewById(R.id.category_name_id)
        var itemDescription: TextView = itemView.findViewById(R.id.category_description_id)
        var itemImage: ImageView = itemView.findViewById(R.id.category_image_id)


        init {
            itemView.setOnClickListener {
                listener.onItemClick(colList[adapterPosition])
            }
            itemView.setOnLongClickListener {
                listener.onLongItemClick(colList[adapterPosition])
            }
        }
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if(p0 == null || p0.length < 0){
                    filterResults.count = colListFil.size
                    filterResults.values = colListFil
                }else{
                    val searchChr = p0.toString().lowercase()
                    val itemModel = ArrayList<Collection>()

                    when(searchChr){
                        "any" -> {
                            col.forEach { itemModel.add(it) }
                        }
                        else -> {
                            for (item in colListFil) {
                                if (item.colName.lowercase()
                                        .contains(searchChr) || item.colDescription.lowercase()
                                        .contains(searchChr) || item.genre.lowercase().contains(searchChr)
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
                colList = filterResults!!.values as ArrayList<Collection>
                notifyDataSetChanged()
            }

        }
    }


}