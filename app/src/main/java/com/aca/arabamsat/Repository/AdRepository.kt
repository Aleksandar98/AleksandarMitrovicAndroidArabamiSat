package com.aca.arabamsat.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Interfaces.AdRepo
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Models.UploadIntent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class AdRepository @Inject constructor(
    val db: FirebaseFirestore
) : AdRepo{
    override  fun getAllAds():MutableLiveData<List<Ad>>{
        var adListLiveData: MutableLiveData<List<Ad>> = MutableLiveData()
        GlobalScope.launch(Dispatchers.IO) {

            db.collection("Ads").addSnapshotListener{snapshot,e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null ) {
                    adListLiveData.postValue(snapshot.toObjects(Ad::class.java))

                } else {
                }
            }

        }
        return adListLiveData
    }

    override  fun uploadAd(
        adObject: Ad,
        selectedFilePaths: List<String>?
    ):MutableLiveData<Boolean> {

        var isUploadingLiveData:MutableLiveData<Boolean> = MutableLiveData(true)
        val newAdRef = db.collection("Ads").document()
        adObject.adId = newAdRef.id

        if (selectedFilePaths != null) {
            adObject.pictures = selectedFilePaths
        }

        var intentObject = UploadIntent("",adObject.userId,newAdRef.id,selectedFilePaths)
        val newIntentRef = db.collection("UploadIntents").document()
        intentObject.intentId =newIntentRef.id
        newIntentRef.set(intentObject)

        newAdRef.set(adObject)
            .addOnSuccessListener { documentReference ->
                isUploadingLiveData.postValue(false)

            }
            .addOnFailureListener { e ->
            }


        return isUploadingLiveData
    }

    override  fun getAdsByLocation(userLocationLon: Double, userLocationLat: Double): MutableLiveData<ArrayList<Ad>> {

        var closestAdsListLiveData:MutableLiveData<ArrayList<Ad>> = MutableLiveData()
        db.collection("Ads").get().addOnSuccessListener {
            var listOfAds = it.toObjects(Ad::class.java)
            listOfAds.sortWith(compareBy({Math.abs(it.longitude-userLocationLon)},{Math.abs(it.latitude-userLocationLat)}))
            closestAdsListLiveData.postValue(listOfAds as ArrayList<Ad>?)
        }
        return closestAdsListLiveData
    }
}