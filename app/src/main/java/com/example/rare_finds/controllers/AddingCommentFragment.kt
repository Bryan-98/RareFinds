package edu.practice.utils.shared.com.example.rare_finds.controllers

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.rare_finds.R
import com.example.rare_finds.fragments.LibraryViewFragment
import com.google.android.material.textfield.TextInputLayout
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.ConnectionHelper
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.DatabaseHelper
import kotlinx.coroutines.*
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val ARG_PARAM1 = "name"
private const val ARG_PARAM2 = "libraryInfo"

class AddCommentFragment : DialogFragment() {
    private val con = ConnectionHelper().dbConn()
    private val db = con?.let { DatabaseHelper(it) }
    private val table = "Comments"
    private val col = "LibName, UserName, UserImage, Comment, TimePosted, Rating"
    private lateinit var starOne: ImageButton
    private lateinit var starTwo: ImageButton
    private lateinit var starThree: ImageButton
    private lateinit var starFour: ImageButton
    private lateinit var starFive: ImageButton
    private lateinit var cancelBtn: ImageButton
    private lateinit var commentEdit: EditText
    private lateinit var til : TextInputLayout;
    private lateinit var addBtn: Button

    private lateinit var titleName: String
    private lateinit var userName: String
    private lateinit var userImage: String
    private lateinit var comment: Editable
    private lateinit var timePosted: String
    private var libInfo: Serializable? = null
    private var starRate = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            titleName = it.getString(ARG_PARAM1).toString()
            libInfo = it.getSerializable(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_adding_comment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.collection_name)
        view.apply{
            starOne = view.findViewById(R.id.rating1)
            starTwo = view.findViewById(R.id.rating2)
            starThree = view.findViewById(R.id.rating3)
            starFour = view.findViewById(R.id.rating4)
            starFive = view.findViewById(R.id.rating5)
            cancelBtn = view.findViewById(R.id.exit)
            addBtn = view.findViewById(R.id.add_comment)
            commentEdit = view.findViewById(R.id.comment_text)
            comment = commentEdit.text

            starOne.setOnClickListener {
                starSelect(starOne)
                starRate = 1
            }

            starTwo.setOnClickListener {
                starSelect(starTwo)
                starRate = 2
            }

            starThree.setOnClickListener {
                starSelect(starThree)
                starRate = 3
            }

            starFour.setOnClickListener {
                starSelect(starFour)
                starRate = 4
            }

            starFive.setOnClickListener {
                starSelect(starFive)
                starRate = 5
            }

            cancelBtn.setOnClickListener {
                dismiss()
            }

            addBtn.setOnClickListener {
                timePosted = getTime()
                loadUserData()
                clearAllInputs(view)
                if(checkAllInputs(view)){
                    if (con != null) {
                        db?.insertTable(table, col, "'${titleName}','${userName}','${userImage}', '${comment}','${timePosted}', $starRate")
                    }
                    GlobalScope.launch(Dispatchers.IO){
                        withContext(Dispatchers.Default) {
                            val libReturn = LibraryViewFragment()
                            val itemName = Bundle()
                            itemName.putSerializable("libraryInfo", libInfo)
                            libReturn.arguments = itemName
                            replaceFragment(libReturn)
                        }
                        withContext(Dispatchers.Default) { dismiss() }
                    }
                }
            }

        }
    }

    private fun loadUserData(){
        val sp = this.activity?.getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        if (sp != null) {
            userName = sp.getString("userName","").toString()
            userImage = sp.getString("imageUrl","").toString()
        }
    }

    private fun checkAllInputs(view: View):Boolean {
        if (comment.isBlank()) {
            til = view.findViewById(R.id.comment_input);
            til.isErrorEnabled = true
            til.error = "Enter a Comment"
            return false
        }
        return true
    }

    private fun clearAllInputs(view: View) {

        if(comment.isNotBlank()){
            til = view.findViewById(R.id.comment_input);
            til.isErrorEnabled = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return current.format(formatter)
    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = (activity as AppCompatActivity).supportFragmentManager
        val fragmentTrans = fragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragmentContainerView,fragment)
        fragmentTrans.commit()
    }

    private fun starSelect(view: View){
        when(view){
            starOne -> {
                starOne.setBackgroundResource(R.drawable.dimond)
                starTwo.setBackgroundResource(R.drawable.empty_dimond_2)
                starThree.setBackgroundResource(R.drawable.empty_dimond_2)
                starFour.setBackgroundResource(R.drawable.empty_dimond_2)
                starFive.setBackgroundResource(R.drawable.empty_dimond_2)
            }
            starTwo -> {
                starOne.setBackgroundResource(R.drawable.dimond)
                starTwo.setBackgroundResource(R.drawable.dimond)
                starThree.setBackgroundResource(R.drawable.empty_dimond_2)
                starFour.setBackgroundResource(R.drawable.empty_dimond_2)
                starFive.setBackgroundResource(R.drawable.empty_dimond_2)
            }
            starThree -> {
                starOne.setBackgroundResource(R.drawable.dimond)
                starTwo.setBackgroundResource(R.drawable.dimond)
                starThree.setBackgroundResource(R.drawable.dimond)
                starFour.setBackgroundResource(R.drawable.empty_dimond_2)
                starFive.setBackgroundResource(R.drawable.empty_dimond_2)
            }
            starFour -> {
                starOne.setBackgroundResource(R.drawable.dimond)
                starTwo.setBackgroundResource(R.drawable.dimond)
                starThree.setBackgroundResource(R.drawable.dimond)
                starFour.setBackgroundResource(R.drawable.dimond)
                starFive.setBackgroundResource(R.drawable.empty_dimond_2)
            }
            starFive -> {
                starOne.setBackgroundResource(R.drawable.dimond)
                starTwo.setBackgroundResource(R.drawable.dimond)
                starThree.setBackgroundResource(R.drawable.dimond)
                starFour.setBackgroundResource(R.drawable.dimond)
                starFive.setBackgroundResource(R.drawable.dimond)
            }
        }
    }
}