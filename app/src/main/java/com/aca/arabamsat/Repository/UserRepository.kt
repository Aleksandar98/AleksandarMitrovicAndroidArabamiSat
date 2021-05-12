package com.aca.arabamsat.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Interfaces.UserRepo
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Models.UploadIntent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject


class UserRepository @Inject constructor(
    val db: FirebaseFirestore,
    val authRepository: AuthRepository,
    val storageRef: StorageReference
): UserRepo {

    var isAdFavoriteLiveData: MutableLiveData<Boolean> = MutableLiveData()

    override fun getAllFavoriteAds(): MutableLiveData<ArrayList<Ad>> {

        val userId = authRepository.firebaseAuth.currentUser?.uid

        var favAdsListLiveData: MutableLiveData<ArrayList<Ad>> = MutableLiveData()
        var favAdsList: ArrayList<Ad> = ArrayList()
        db.collection("Users").whereEqualTo("userId", userId).get()
            .addOnCompleteListener {
                if (!it.result.isEmpty) {
                    var document = it.result.documents.get(0)
                    val favoriteAdsList = document.data?.get("favoriteAds") as ArrayList<String>
                    if (favoriteAdsList.isEmpty()) {
                        favAdsListLiveData.postValue(favAdsList)
                    } else {
                        for (adId in favoriteAdsList) {
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

    override fun isAdFavorite(adId: String): MutableLiveData<Boolean> {
        val currentUserId = authRepository.firebaseAuth.currentUser?.uid

        currentUserId?.let {
            db.collection("Users").whereEqualTo("userId", it).get()
                .addOnCompleteListener {
                    if (!it.result.isEmpty) {
                        var document = it.result.documents.get(0)
                        val favoriteAdsList = document.data?.get("favoriteAds") as ArrayList<String>

                        if (favoriteAdsList.contains(adId))
                            isAdFavoriteLiveData.postValue(true)
                        else
                            isAdFavoriteLiveData.postValue(false)
                    }
                }
        }
        return isAdFavoriteLiveData
    }

    override fun addToFavorite(adId: String) {

        val currentUserId = authRepository.firebaseAuth.currentUser?.uid

        currentUserId?.let {
            db.collection("Users").whereEqualTo("userId", it).get()
                .addOnCompleteListener {
                    if (!it.result.isEmpty) {

                        var document = it.result.documents.get(0)

                        val oldList = document.data?.get("favoriteAds") as ArrayList<String>
                        if (oldList.contains(adId)) {
                            oldList.remove(adId)
                            isAdFavoriteLiveData.postValue(false)
                        } else {
                            oldList.add(adId)
                            isAdFavoriteLiveData.postValue(true)
                        }
                        db.collection("Users").document(document.id)
                            .update("favoriteAds", oldList)
                    }
                }
        }

    }

     fun uploadCachedImages() {
        val currentUserId = authRepository.firebaseAuth.currentUser?.uid

        db.collection("UploadIntents").whereEqualTo("userId", currentUserId)
            .addSnapshotListener { snapshot, e ->
                val listIntents = snapshot?.toObjects(UploadIntent::class.java)
                if (listIntents != null) {
                    for ((index, intent) in listIntents.withIndex()) {
                        uploadImage(intent)
                    }

                }
            }
    }

    private fun uploadImage(intent: UploadIntent) {
        val listUploadedUris: MutableList<String> = mutableListOf()

        GlobalScope.launch(Dispatchers.IO) {
            for ((index, filePath) in intent.filePaths?.withIndex()!!) {

                val carImagesRef = storageRef.child("ads/${intent.adId}/i_$index.jpg")

                var file = Uri.fromFile(File(filePath))

                val uploadTask = filePath.let { it1 -> carImagesRef.putFile(file).await() }
                val url: Uri = carImagesRef.downloadUrl.await()
                listUploadedUris.add(url.toString())

            }

            db.collection("Ads").document(intent.adId)
                .update("pictures", listUploadedUris)
                .addOnSuccessListener {
                    deleteUploadIntent(intent)
                }
        }
    }

    private fun deleteUploadIntent(intent: UploadIntent) {
        db.collection("UploadIntents").document(intent.intentId).delete()
    }


}