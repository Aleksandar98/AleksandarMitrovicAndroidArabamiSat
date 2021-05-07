package com.aca.arabamsat

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.aca.arabamsat.Models.Ad
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_adding.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class AddingActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private  val TAG = "myTag"
    private val PICK_IMAGE = 123;
    private lateinit var selectedImagesData: ClipData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding)

        firebaseAuth = FirebaseAuth.getInstance()
        val db = Firebase.firestore
        storage = Firebase.storage

        phoneEdit.setText(firebaseAuth.currentUser.phoneNumber)
        emailEdit.setText(firebaseAuth.currentUser.email)

        val storageRef = storage.reference



        uploadAdBtn.setOnClickListener{
            val adObject = hashMapOf(
                "model" to modelEdit.text.toString(),
                "year" to yearEdit.text.toString(),
                "price" to priceEdit.text.toString(),
                "userId" to firebaseAuth.currentUser.uid,
                "phoneNuber" to phoneEdit.text.toString(),
                "description" to descEdit.text.toString(),
                "email" to firebaseAuth.currentUser.email
            )


            db.collection("Ads")
                .add(adObject)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

                    val imageCount = selectedImagesData.itemCount
                    var i = 0
                    var uploadImgUrls: MutableList<String> = mutableListOf()
                    GlobalScope.launch(Dispatchers.IO) {
                        while (i<imageCount) {
                            val uri: Uri? = selectedImagesData.getItemAt(i).uri


                            val carImagesRef = storageRef.child("ads/${documentReference.id}/i_$i.jpg")

                            val uploadTask = uri?.let { it1 -> carImagesRef.putFile(it1).await() }
                            val url:Uri = carImagesRef.downloadUrl.await()
                            uploadImgUrls.add(url.toString())

                            i++
                        }
                        Log.d(TAG, "onCreate: urlList Size ${uploadImgUrls.size}")
                        for(url in uploadImgUrls){Log.d(TAG, "onCreate: url ${url}")}

                        db.collection("Ads").document(documentReference.id)
                            .update("pictures", uploadImgUrls)
                            .addOnSuccessListener {
                                Toast.makeText(this@AddingActivity,"Ad Published!",Toast.LENGTH_LONG).show()
                            }
                    }


                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@AddingActivity,"Error publishing your Ad",Toast.LENGTH_LONG).show()
                    Log.w(TAG, "Error adding document", e)
                }
        }

        uploadImagesBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE){

            data?.clipData?.let {
                selectedImagesData = it

            }
        }
    }

}