package edu.practice.utils.shared.com.example.rare_finds.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.api.igdb.apicalypse.APICalypse
import com.api.igdb.exceptions.RequestException
import com.api.igdb.request.IGDBWrapper
import com.api.igdb.request.games
import com.example.rare_finds.R
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.com.example.rare_finds.models.Search
import proto.InvolvedCompany
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SearchRecycler(searchStr : String) : RecyclerView.Adapter<SearchRecycler.ViewHolder>() {
    private var searchList : ArrayList<Search>
    private lateinit var listener : OnItemClickListener
    //private var filterList : ArrayList<Search> = ArrayList()

    init {
        this.searchList = APICall(searchStr)
        //notifyDataSetChanged()
    }

//    private fun APICall(searchStr: String) : ArrayList<Search> {
//        IGDBWrapper.setCredentials("pzqarw3s181p3re13e0m4rftc2xphl", "dmrud9qwnvs3mj693z41n6m6xawzzd")
//        val apicalypse = APICalypse().fields("cover.url, name, first_release_date, summary, url, involved_companies.company.name, involved_companies.publisher, genres.name").search(searchStr).limit(1)
//        var searchList : ArrayList<Search> = ArrayList()
//        try {
//            val games = IGDBWrapper.games(apicalypse)
//            val get = games[0]
//            searchList.add(Search(0,"","","","","",""))
//            searchList[0].searchId = get.id
//
//            searchList[0].imageUrl = get.cover.url
//
//            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//            val date = Date(get.firstReleaseDate.seconds * 1000L)
//            val time = sdf.format(date)
//            searchList[0].yearRelease = time
//
////            val involvedCompanies = get.involvedCompaniesList
////            var i = 0
////            val companyIdList:ArrayList<InvolvedCompany> = ArrayList()
//            get.involvedCompaniesList.forEach {
//                if(it.publisher){
//                    searchList[0].searchPublisher = it.company.name
//                }
//            }
////            while(i < involvedCompanies.size){
////                companyIdList.add(involvedCompanies[i])
////                i++
////            }
////            for(id in companyIdList){
////                if(id.publisher){
////                    searchList[0].searchPublisher = id.company.name
////                }
////            }
//
//            searchList[0].searchName = get.name
//            searchList[0].colDescription = get.summary
//
//            var count = get.genresList.size - 1
//            for(genre in get.genresList){
//                searchList[0].genre += genre.name
//                count--
//                if(count < 0){
//                    break
//                }
//                searchList[0].genre += ", "
//            }
//
//            println(searchList)
//            //games.forEach{println(it)}
//        } catch (e: RequestException) {
//            println(e.message)
//        }
//        return searchList
//    }

    private fun APICall(searchStr: String) : ArrayList<Search>{
        IGDBWrapper.setCredentials("pzqarw3s181p3re13e0m4rftc2xphl", "dmrud9qwnvs3mj693z41n6m6xawzzd")
        val apicalypse = APICalypse().fields("cover.url, name, first_release_date, summary, url, involved_companies.company.name, involved_companies.publisher, genres.name").search(searchStr).limit(5)
        val searchList : ArrayList<Search> = ArrayList()
        try {
            val games = IGDBWrapper.games(apicalypse)

            games.forEach{
                searchList.add(Search(
                    it.id,
                    it.name,
                    getCompany(it.involvedCompaniesList),
                    it.summary,
                    getRelease(it.firstReleaseDate.seconds),
                    it.genresList[0].name,
                    "https:" + it.cover.url)
                )
            }
        } catch (e: RequestException) {
            println(e.message)
        }
        return searchList
    }

    private fun getCompany(com: List<InvolvedCompany>): String{
        com.forEach {
            if(it.publisher){
                return it.company.name
            }
        }
        return ""
    }

    private fun getRelease(sec: Long): String{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = Date(sec * 1000L)
        val time = sdf.format(date)
        return time
    }

//    override fun getFilter(): Filter {
//        return object: Filter(){
//            override fun performFiltering(p0: CharSequence?): FilterResults {
//                val filterResults = FilterResults()
//
//                return filterResults
//            }
//
//            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
//                filterList = filterResults!!.values as ArrayList<Search>
//                notifyDataSetChanged()
//            }
//
//        }
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_item_view,
            parent, false
        )
        return ViewHolder(v, listener)
    }

    private fun ImageView.setImageResource(imageUrl: String): String {
        return imageUrl;
    }

    interface OnItemClickListener{
        fun onItemClick(item: Serializable)
        fun onLongItemClick(item: Serializable): Boolean
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = searchList[position].searchName
        holder.itemDescription.text = searchList[position].colDescription
        Picasso.get().load(searchList[position].imageUrl).fit().into(holder.itemImage)
    }

    inner class ViewHolder(itemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(itemView){
        var itemName: TextView = itemView.findViewById(R.id.category_name_id)
        var itemDescription: TextView = itemView.findViewById(R.id.category_description_id)
        var itemImage: ImageView = itemView.findViewById(R.id.category_image_id)


        init {
            itemView.setOnClickListener {
                listener.onItemClick(searchList[adapterPosition])
            }
            itemView.setOnLongClickListener {
                listener.onLongItemClick(searchList[adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return searchList.size
    }
}