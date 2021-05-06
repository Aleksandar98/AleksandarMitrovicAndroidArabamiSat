package com.aca.arabamsat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aca.arabamsat.Models.Ad
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private  val TAG = "myTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        uploadBtn.setOnClickListener{
            val user = hashMapOf(
                "year" to "2020",
                "price" to "111500",
                "userId" to firebaseAuth.currentUser.uid
            )

            db.collection("Ads")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }



        db.collection("Ads")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var ad:Ad = document.toObject(Ad::class.java)

                    Log.d(TAG, "${ad}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}