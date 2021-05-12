package com.aca.arabamsat.Interfaces

import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Models.Ad

interface UserRepo {
    fun getAllFavoriteAds(): MutableLiveData<ArrayList<Ad>>

    fun isAdFavorite(adId: String): MutableLiveData<Boolean>

    fun addToFavorite(adId: String)
}