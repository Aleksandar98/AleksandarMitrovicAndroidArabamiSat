package com.aca.arabamsat.Repository

import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Interfaces.UserRepo
import com.aca.arabamsat.Models.Ad

class FakeUserRepository : UserRepo {

    private val favAdList = arrayListOf<Ad>()

    private val observableAdList = MutableLiveData<ArrayList<Ad>>(favAdList)
    private val isAdFavoriteLiveData = MutableLiveData<Boolean>()

    override fun getAllFavoriteAds(): MutableLiveData<ArrayList<Ad>> {
        return observableAdList
    }

    override fun isAdFavorite(adId: String): MutableLiveData<Boolean> {
       for(ad in favAdList)
           if(ad.adId.equals(adId)){
               isAdFavoriteLiveData.postValue(true)
           }else{
               isAdFavoriteLiveData.postValue(false)
           }
        return isAdFavoriteLiveData
    }

    override fun addToFavorite(adId: String) {
       var ad= Ad(adId,"","","","","","","", emptyList(),0.0,0.0)
        favAdList.add(ad)
    }

}