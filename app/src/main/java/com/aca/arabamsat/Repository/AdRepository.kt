package com.aca.arabamsat.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Models.UploadIntent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI
import javax.inject.Inject


class AdRepository @Inject constructor(
    val db: FirebaseFirestore,
    val storageRef: StorageReference
){

//    val storage = Firebase.storage
//    val db = Firebase.firestore
//    val storageRef = storage.reference
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

        }
        return adListLiveData;
    }

    fun uploadAd(
        adObject: Ad,
        selectedFilePaths: List<String>?
    ):MutableLiveData<Boolean> {

        var isUploadingLiveData:MutableLiveData<Boolean> = MutableLiveData(true)
        val newAdRef = db.collection("Ads").document()
        adObject.adId = newAdRef.id

        if (selectedFilePaths != null) {
            adObject.pictures = selectedFilePaths
        }

//        val  intentObject = object{
//            val userId = adObject.userId
//            val adId = newAdRef.id
//            val filePaths = selectedFilePaths
//        }

        var intentObject = UploadIntent("",adObject.userId,newAdRef.id,selectedFilePaths)
        val newIntentRef = db.collection("UploadIntents").document()
        intentObject.intentId =newIntentRef.id
        newIntentRef.set(intentObject)

        newAdRef.set(adObject)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${adObject.adId}")

//                val imageCount = uriList.size
//                var i = 0
//                var uploadImgUrls: MutableList<String> = mutableListOf()
//
//
//
//                Log.d(TAG, "uploadAd: Test moj FIle :${selectedFilePath}")
//                if (selectedFilePath != null) {
//                    uploadImgUrls.add(selectedFilePath)
//                }

//                db.collection("Ads").document(adObject.adId)
//                        .update("pictures", uploadImgUrls)
//                        .addOnSuccessListener {
//                           // Toast.makeText(this@AddingActivity,"Ad Published!", Toast.LENGTH_LONG).show()
//                            Log.d(TAG, "uploadAd: DONE UPLOADING")
//                            isUploadingLiveData.postValue(false)
//                        }

//                GlobalScope.launch(Dispatchers.IO) {
//                    while (i<imageCount) {
//                        val uri: Uri? = uriList.get(i)
//
//
//                        val carImagesRef = storageRef.child("ads/${adObject.adId}/i_$i.jpg")
//
//                        val uploadTask = uri?.let { it1 -> carImagesRef.putFile(it1).await() }
//                        val url: Uri = carImagesRef.downloadUrl.await()
//                        uploadImgUrls.add(url.toString())
//
//                        i++
//                    }
//                    Log.d(TAG, "onCreate: urlList Size ${uploadImgUrls.size}")
//                    for(url in uploadImgUrls){Log.d(TAG, "onCreate: url ${url}")}
//
//                    db.collection("Ads").document(adObject.adId)
//                        .update("pictures", uploadImgUrls)
//                        .addOnSuccessListener {
//                           // Toast.makeText(this@AddingActivity,"Ad Published!", Toast.LENGTH_LONG).show()
//                            Log.d(TAG, "uploadAd: DONE UPLOADING")
//                            isUploadingLiveData.postValue(false)
//                        }
//                }


            }
            .addOnFailureListener { e ->
               // Toast.makeText(this@AddingActivity,"Error publishing your Ad", Toast.LENGTH_LONG).show()
                Log.w(TAG, "Error adding document", e)
            }

        return isUploadingLiveData
    }



    fun sortByLocation(){

    }

    fun isDataBaseEmpry():Boolean {
        var doesExist:Boolean = db.collection("Ads").get().isSuccessful
        if(doesExist)
            return true
        else return false
    }
}