package com.aca.arabamsat

import android.R.attr
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.aca.arabamsat.Models.Ad
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
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
        storage = Firebase.storage

//        val storageRef = storage.reference
//        storageRef.child("ads/mountains.jpg").downloadUrl.addOnSuccessListener {
//            // Got the download URL for 'users/me/profile.png'
//            Glide.with(this).load(it).into(tempImg)
//        }.addOnFailureListener {
//            // Handle any errors
//        }

//        uploadBtn.setOnClickListener{
//            val user = hashMapOf(
//                "year" to "2020",
//                "price" to "111500",
//                "userId" to firebaseAuth.currentUser.uid,
//                "phoneNuber" to "1234567",
//                "description" to "Random desc",
//                "userName" to firebaseAuth.currentUser.email
//            )
//
//            db.collection("Ads")
//                .add(user)
//                .addOnSuccessListener { documentReference ->
//                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                    db.collection("Ads").document(documentReference.id)
//                        .update("pictures", listOf("a","b","c"))
//                }
//                .addOnFailureListener { e ->
//                    Log.w(TAG, "Error adding document", e)
//                }
//        }

        fab.setOnClickListener {
            val intent = Intent(this,AddingActivity::class.java)
            startActivity(intent)
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

        db.collection("Ads")
            .get()
            .addOnSuccessListener { result ->
                var string = ""
                for (document in result) {
                    var ad:Ad = document.toObject(Ad::class.java)
                    string+="${ad}\n"
                    Log.d(TAG, "${ad}")
                }
                txtUser.text=string
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == PICK_IMAGE){
//            data?.clipData?.let {
//                val imageCount = it.itemCount
//                var i = 0
//                while (i<imageCount) {
//                    val uri: Uri? = it.getItemAt(i).uri
//
//                    // Create a storage reference from our app
//                    val storageRef = storage.reference
//
//                    // Create a reference to "mountains.jpg"
//                    val mountainsRef = storageRef.child("mountains.jpg")
//
//                    // Create a reference to 'images/mountains.jpg'
//                    val mountainImagesRef = storageRef.child("ads/adId/mountains$i.jpg")
//
//                    val uploadTask = uri?.let { it1 -> mountainImagesRef.putFile(it1) }
//                    if (uploadTask != null) {
//                        uploadTask.addOnFailureListener {
//                            // Handle unsuccessful uploads
//                            Log.d(TAG, "onActivityResult: ${it.message}")
//                        }.addOnSuccessListener { taskSnapshot ->
//                            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                            // ...
//                            Log.d(TAG, "onActivityResult: ${taskSnapshot.uploadSessionUri}")
//                        }
//                    }
//
//                    i++
//
//                }
//
//            }
////            val inputStream: InputStream? =
////                data?.data?.let { applicationContext.getContentResolver().openInputStream(it) }
//
//
//        }
//    }
}