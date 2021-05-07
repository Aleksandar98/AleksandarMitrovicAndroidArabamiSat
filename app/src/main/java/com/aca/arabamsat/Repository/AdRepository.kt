package com.aca.arabamsat.Repository

import android.content.ClipData
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Models.Ad
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

object AdRepository {

    val storage = Firebase.storage
    val db = Firebase.firestore
    val storageRef = storage.reference
    private val TAG = "myTag"

    fun getAllAds():MutableLiveData<List<Ad>>{
        var adListLiveData: MutableLiveData<List<Ad>> = MutableLiveData()
        GlobalScope.launch(Dispatchers.IO) {

           // var list:List<Ad>  = db.collection("Ads").get().await().toObjects(Ad::class.java)
            //adListLiveData.postValue(list)
            db.collection("Ads").addSnapshotListener{snapshot,e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null ) {
                    Log.d(TAG, "Current data: ${snapshot.toObjects(Ad::class.java).size}")
                    adListLiveData.postValue(snapshot.toObjects(Ad::class.java))

                } else {
                    Log.d(TAG, "Current data: null")
                }
            }


//            var string = ""
//            for (ad in list) {
//                string+="${ad}\n"
//                Log.d("myTag", "${ad}")
//            }
        }
        return adListLiveData;
    }

    fun uploadAd(
        adObject: HashMap<String, String>,
        uriList: List<Uri>
    ):MutableLiveData<Boolean> {

        var isUploadingLiveData:MutableLiveData<Boolean> = MutableLiveData(true)
        db.collection("Ads")
            .add(adObject)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

                val imageCount = uriList.size
                var i = 0
                var uploadImgUrls: MutableList<String> = mutableListOf()
                GlobalScope.launch(Dispatchers.IO) {
                    while (i<imageCount) {
                        val uri: Uri? = uriList.get(i)


                        val carImagesRef = storageRef.child("ads/${documentReference.id}/i_$i.jpg")

                        val uploadTask = uri?.let { it1 -> carImagesRef.putFile(it1).await() }
                        val url: Uri = carImagesRef.downloadUrl.await()
                        uploadImgUrls.add(url.toString())

                        i++
                    }
                    Log.d(TAG, "onCreate: urlList Size ${uploadImgUrls.size}")
                    for(url in uploadImgUrls){Log.d(TAG, "onCreate: url ${url}")}

                    db.collection("Ads").document(documentReference.id)
                        .update("pictures", uploadImgUrls)
                        .addOnSuccessListener {
                           // Toast.makeText(this@AddingActivity,"Ad Published!", Toast.LENGTH_LONG).show()
                            Log.d(TAG, "uploadAd: DONE UPLOADING")
                            isUploadingLiveData.postValue(false)
                        }
                }


            }
            .addOnFailureListener { e ->
               // Toast.makeText(this@AddingActivity,"Error publishing your Ad", Toast.LENGTH_LONG).show()
                Log.w(TAG, "Error adding document", e)
            }
        return isUploadingLiveData
    }

    fun isDataBaseEmpry():Boolean {
        var doesExist:Boolean = db.collection("Ads").get().isSuccessful
        if(doesExist)
            return true
        else return false
    }
}