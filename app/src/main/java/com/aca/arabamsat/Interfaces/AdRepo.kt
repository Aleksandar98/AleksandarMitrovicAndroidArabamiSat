package com.aca.arabamsat.Interfaces

import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Models.Ad

interface AdRepo {
    fun getAllAds():MutableLiveData<List<Ad>>

    fun uploadAd(
        adObject: Ad,
        selectedFilePaths: List<String>?
    ): MutableLiveData<Boolean>

    fun getAdsByLocation(userLocationLon: Double, userLocationLat: Double): MutableLiveData<ArrayList<Ad>>
}