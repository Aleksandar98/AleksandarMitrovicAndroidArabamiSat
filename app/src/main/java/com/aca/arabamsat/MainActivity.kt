package com.aca.arabamsat

import android.R.attr
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aca.arabamsat.Adapters.AdRecyclerAdapter
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.ViewModels.MainViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var mainActivityViewModel: MainViewModel
    private lateinit var adAdapter: AdRecyclerAdapter
    private  val TAG = "myTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))


        mainActivityViewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        initRecyclerView()

        mainActivityViewModel.getAds().observe(this, Observer {
            adAdapter.submitList(it)
            adAdapter.notifyDataSetChanged()
        })

        mainActivityViewModel.isLoading().observe(this, Observer {
            //TODO Fix
            if(!it)
                progressBar.visibility = GONE
        })

        mainActivityViewModel.isLogedIn().observe(this, Observer {
            if(!it){
                val loginActIntent = Intent(this,LoginActivity::class.java)
                startActivity(loginActIntent)
                finish()
            }
        })

        if(mainActivityViewModel.isDatabaseEmpty()){
            //TODO Fix dinamic change
            databaseEmptyTxt.visibility = VISIBLE
        }


        fab.setOnClickListener {
            val intent = Intent(this,AddingActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        R.id.action_settings -> {
//            // User chose the "Settings" item, show the app settings UI...
//            true
//        }

        R.id.action_logout -> {
            mainActivityViewModel.logoutUser()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    fun initRecyclerView(){
        adAdapter = AdRecyclerAdapter()

        carsRecyclerView.layoutManager = LinearLayoutManager(this)
        carsRecyclerView.adapter = adAdapter
    }
}