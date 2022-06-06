package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.api.igdb.apicalypse.APICalypse
import com.api.igdb.exceptions.RequestException
import com.api.igdb.request.IGDBWrapper
import com.api.igdb.request.games
import com.example.rare_finds.R
import com.squareup.picasso.Picasso
import edu.practice.utils.shared.Keys
import edu.practice.utils.shared.com.example.rare_finds.models.Library
import edu.practice.utils.shared.com.example.rare_finds.models.Search
import proto.Genre
import proto.InvolvedCompany
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SearchRecyclerAdapter(searchStr : String) : RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>(), Filterable {
    private val clientId = Keys.clientId()
    private val token = Keys.token()
    private var searchList : ArrayList<Search>
    private var searchListFil : ArrayList<Search>
    private lateinit var listener : OnItemClickListener

    init {
        searchList = apiCall(searchStr)
        searchListFil = searchList
        notifyDataSetChanged()
    }

    private fun apiCall(searchStr: String) : ArrayList<Search>{
        IGDBWrapper.setCredentials(clientId, token)
        val api = APICalypse().fields("cover.url, name, first_release_date, summary, url, involved_companies.company.name, involved_companies.publisher, genres.name")
            .search(searchStr).where("(cover != null | name != null | first_release_date != null | summary != null | url != null | involved_companies != null | genres != null)")
            .limit(25)

        val search : ArrayList<Search> = ArrayList()
        try {
            val games = IGDBWrapper.games(api)
            games.forEach{
                search.add(Search(
                    it.id,
                    it.name,
                    getCompany(it.involvedCompaniesList),
                    it.summary,
                    getRelease(it.firstReleaseDate.seconds),
                    getGenre(it.genresList),
                    "https:" + it.cover.url)
                )
            }
        } catch (e: RequestException) {
            println(e.message)
        }
        return search
    }

    private fun getGenre(gen: List<Genre>):String{
        gen.forEach{
            if (it.name.isNotBlank()){
                return it.name
            }
        }
        return ""
    }

    private fun getCompany(com: List<InvolvedCompany>): String{
        com.forEach {
            if(it.publisher){
                return it.company.name
            }
        }
        return ""
    }

    private fun getRelease(sec: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = Date(sec * 1000L)
        return sdf.format(date)
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val itemModel = ArrayList<Search>()
                if(p0 == null || p0.length < 0){
                    filterResults.count = searchListFil.size
                    filterResults.values = searchListFil
                }else{
                    val searchChr = p0.toString()
                    apiCall(searchChr).forEach { itemModel.add(it) }

                    filterResults.count = itemModel.size
                    filterResults.values = itemModel
                }
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                searchList = filterResults!!.values as ArrayList<Search>
                notifyDataSetChanged()
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_item_view,
            parent, false
        )
        return ViewHolder(v, listener)
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
        holder.itemDescription.text = ""
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