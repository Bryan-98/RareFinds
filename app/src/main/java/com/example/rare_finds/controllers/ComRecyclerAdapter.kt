package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rare_finds.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.com.example.rare_finds.models.Comment
import com.example.rare_finds.connection.ConnectionHelper
import com.example.rare_finds.connection.DatabaseHelper

class ComRecyclerAdapter(libName: String) : RecyclerView.Adapter<ComRecyclerAdapter.ViewHolder>() {

    private lateinit var comList : ArrayList<Comment>

    init {
        val con = ConnectionHelper().dbConn()
        val db = con?.let { DatabaseHelper(it) }
        val com = db?.fillCommentList(libName)
        if (com != null) {
            comList = com
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comment_item_view, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemUserName.text = comList[position].userName
        holder.itemUserDate.text = comList[position].date.toString()
        holder.itemComment.text = comList[position].comment
        starSelect(holder,comList[position].rating)

        Picasso.get().load(comList[position].userImage).fit()
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .into(holder.itemImage)

    }

    override fun getItemCount(): Int {
        return comList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemUserName: TextView = itemView.findViewById(R.id.userName)
        var itemUserDate: TextView = itemView.findViewById(R.id.postDate)
        var itemComment: TextView = itemView.findViewById(R.id.comment)
        var itemImage: ImageView = itemView.findViewById(R.id.userImage)

        var starOne: ImageView = itemView.findViewById(R.id.star1)
        var starTwo: ImageView = itemView.findViewById(R.id.star2)
        var starThree: ImageView = itemView.findViewById(R.id.star3)
        var starFour: ImageView = itemView.findViewById(R.id.star4)
        var starFive: ImageView = itemView.findViewById(R.id.star5)
    }

    private fun starSelect(holder: ViewHolder,rate: Int){
        when(rate){
            1 -> {
                holder.starOne.setImageResource(R.drawable.dimond)
                holder.starTwo.setImageResource(R.drawable.empty_dimond_2)
                holder.starThree.setImageResource(R.drawable.empty_dimond_2)
                holder.starFour.setImageResource(R.drawable.empty_dimond_2)
                holder.starFive.setImageResource(R.drawable.empty_dimond_2)
            }
            2 -> {
                holder.starOne.setImageResource(R.drawable.dimond)
                holder.starTwo.setImageResource(R.drawable.dimond)
                holder.starThree.setImageResource(R.drawable.empty_dimond_2)
                holder.starFour.setImageResource(R.drawable.empty_dimond_2)
                holder.starFive.setImageResource(R.drawable.empty_dimond_2)
            }
            3 -> {
                holder.starOne.setImageResource(R.drawable.dimond)
                holder.starTwo.setImageResource(R.drawable.dimond)
                holder.starThree.setImageResource(R.drawable.dimond)
                holder.starFour.setImageResource(R.drawable.empty_dimond_2)
                holder.starFive.setImageResource(R.drawable.empty_dimond_2)
            }
            4 -> {
                holder.starOne.setImageResource(R.drawable.dimond)
                holder.starTwo.setImageResource(R.drawable.dimond)
                holder.starThree.setImageResource(R.drawable.dimond)
                holder.starFour.setImageResource(R.drawable.dimond)
                holder.starFive.setImageResource(R.drawable.empty_dimond_2)
            }
            5 -> {
                holder.starOne.setImageResource(R.drawable.dimond)
                holder.starTwo.setImageResource(R.drawable.dimond)
                holder.starThree.setImageResource(R.drawable.dimond)
                holder.starFour.setImageResource(R.drawable.dimond)
                holder.starFive.setImageResource(R.drawable.dimond)
            }
        }
    }
}