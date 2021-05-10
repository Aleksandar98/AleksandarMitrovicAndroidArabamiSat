package com.aca.arabamsat.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class UserRepository @Inject constructor(
    val db: FirebaseFirestore,
    val authRepository: AuthRepository
){

    val TAG:String = "myTag"
    var isAdFavoriteLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getAllFavoriteAds(): MutableLiveData<ArrayList<Ad>> {

        val userId = authRepository.firebaseAuth.currentUser?.uid

        var favAdsListLiveData:MutableLiveData<ArrayList<Ad>> = MutableLiveData()
        var favAdsList:ArrayList<Ad> = ArrayList()
        db.collection("Users").whereEqualTo("userId", userId).get()
            .addOnCompleteListener {
                if (!it.getResult().isEmpty) {
                    var document = it.getResult().documents.get(0)
                    val favoriteAdsList = document.data?.get("favoriteAds") as ArrayList<String>
                    if(favoriteAdsList.isEmpty()){
                        favAdsListLiveData.postValue(favAdsList)
                    }else{
                        for(adId in favoriteAdsList){
                            db.collection("Ads").document(adId).get()
                                .addOnSuccessListener {
                                    it.toObject(Ad::class.java)?.let { it1 -> favAdsList.add(it1) }
                                    favAdsListLiveData.postValue(favAdsList)
                                }
                        }
                    }
                }
            }
        return favAdsListLiveData
    }

    fun isAdFavorite(adId: String): MutableLiveData<Boolean> {
        val currentUserId = authRepository.firebaseAuth.currentUser?.uid

        currentUserId?.let {
            db.collection("Users").whereEqualTo("userId", it).get()
                .addOnCompleteListener {
                    if (!it.getResult().isEmpty) {
                        var document = it.getResult().documents.get(0)
                        val favoriteAdsList = document.data?.get("favoriteAds") as ArrayList<String>

                        if(favoriteAdsList.contains(adId))
                            isAdFavoriteLiveData.postValue(true)
                        else
                            isAdFavoriteLiveData.postValue(false)
                    }
                }
        }
        return isAdFavoriteLiveData
    }

    fun addToFavorite(adId: String) {

        val currentUserId = authRepository.firebaseAuth.currentUser?.uid

        currentUserId?.let{
            db.collection("Users").whereEqualTo("userId",it).get()
                .addOnCompleteListener {
                    if (!it.getResult().isEmpty) {

                        var document = it.getResult().documents.get(0)

                        val oldList = document.data?.get("favoriteAds") as ArrayList<String>

                        if(oldList.contains(adId)){
                            oldList.remove(adId)
                            isAdFavoriteLiveData.postValue(false)
                        }else{
                            oldList.add(adId)
                            isAdFavoriteLiveData.postValue(true)

                        }

                        db.collection("Users").document(document.id)
                            .update("favoriteAds",oldList)

                    }
                }
        }

    }

}