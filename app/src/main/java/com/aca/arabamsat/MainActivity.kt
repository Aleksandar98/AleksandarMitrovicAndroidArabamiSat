package com.aca.arabamsat

import android.R.attr
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aca.arabamsat.Adapters.AdRecyclerAdapter
import com.aca.arabamsat.Models.Ad
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
    private  val TAG = "myTag"
    private val PICK_IMAGE = 123;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        val adAdapter = AdRecyclerAdapter()

        carsRecyclerView.layoutManager = LinearLayoutManager(this)
        carsRecyclerView.adapter = adAdapter

        GlobalScope.launch(Dispatchers.IO) {

            var list:List<Ad>  = db.collection("Ads").get().await().toObjects(Ad::class.java)
            withContext(Main){
                adAdapter.submitList(list)
                adAdapter.notifyDataSetChanged()
            }

            var string = ""
            for (ad in list) {
                string+="${ad}\n"
                Log.d(TAG, "${ad}")
            }
        }

        val demoAds = listOf<Ad>(
            Ad("1999","1000","Y5355345D53425F","1233456","Prius 1","Aca123",
                listOf("https://www.designnews.com/sites/designnews.com/files/Prius-1-02%20%281%29.jpg")),
            Ad("2006","5000","Y5355345D53425F","1233456","Prius 2","Aca123",
            listOf("https://upload.wikimedia.org/wikipedia/commons/2/2b/2nd_Toyota_Prius.jpg")),
            Ad("2010","8000","Y5355345D53425F","1233456","Prius 3","Aca123",
                listOf("https://www.mad4wheels.com/img/free-car-images/mobile/8879/toyota-prius-zvw30--2011-315423.jpg")),
            Ad("2020","15000","Y5355345D53425F","1233456","Prius 4","Aca123",
                listOf("https://upload.wikimedia.org/wikipedia/commons/4/4a/Prius_4_IMG_6899.JPG"))

            )



//        flowersListViewModel.flowersLiveData.observe(this, {
//            it?.let {
//                flowersAdapter.submitList(it as MutableList<Flower>)
//                headerAdapter.updateFlowerCount(it.size)
//            }
//        })


        fab.setOnClickListener {
            val intent = Intent(this,AddingActivity::class.java)
            startActivity(intent)
        }


    }

}