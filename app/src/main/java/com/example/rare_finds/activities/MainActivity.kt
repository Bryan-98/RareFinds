package edu.practice.utils.shared.com.example.rare_finds.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.rare_finds.R
import edu.practice.utils.shared.com.example.rare_finds.controllers.UpdatingUserFragment
import com.example.rare_finds.login.LoginActivity
import com.google.android.material.navigation.NavigationView
import edu.practice.utils.shared.com.example.rare_finds.fragments.CollectionFragment
import edu.practice.utils.shared.com.example.rare_finds.fragments.SearchFragment
import edu.practice.utils.shared.com.example.rare_finds.sqlconnection.BlobConnection
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val storageCon = BlobConnection()
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userImage: ImageView

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigationView)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navItemSelect()
        userName = navView.getHeaderView(0).findViewById(R.id.header_username)
        userEmail = navView.getHeaderView(0).findViewById(R.id.header_email)
        userImage = navView.getHeaderView(0).findViewById(R.id.header_image)

        GlobalScope.launch(Dispatchers.IO){
            loadUserData()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun navItemSelect(){
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.complete_signout -> {

                    GlobalScope.launch(Dispatchers.IO){
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.upDatingUserFragmentFragment -> {

                    GlobalScope.launch(Dispatchers.IO){
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    replaceFragment(UpdatingUserFragment())
                    true
                }
                R.id.collectionFragment -> {

                    GlobalScope.launch(Dispatchers.IO){
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    replaceFragment(CollectionFragment())
                    true
                }
                R.id.searchFragment -> {

                    GlobalScope.launch(Dispatchers.IO){
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    replaceFragment(SearchFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun replaceFragment(fragment:Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTrans = fragmentManager.beginTransaction().setCustomAnimations(R.anim.from_right, R.anim.from_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        fragmentTrans.replace(R.id.fragmentContainerView,fragment).addToBackStack(null)
        fragmentTrans.commit()
    }

    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
    override fun onSupportNavigateUp(): Boolean {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            this.drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    private fun loadUserData(){
        val sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        userName.text = sp.getString("userName","")
        userEmail.text = sp.getString("email","")
        val image = sp.getString("imageUrl","")
        userImage.setImageBitmap(image?.let { storageCon.getImage(it) })
    }
}
