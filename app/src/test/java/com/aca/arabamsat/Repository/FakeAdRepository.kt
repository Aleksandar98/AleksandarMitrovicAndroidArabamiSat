package com.aca.arabamsat.Repository

import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Interfaces.AdRepo
import com.aca.arabamsat.Models.Ad

class FakeAdRepository : AdRepo {

    private val adList = mutableListOf<Ad>()

    private val observableAdList = MutableLiveData<List<Ad>>(adList)

    override  fun getAllAds():MutableLiveData<List<Ad>>{
        return observableAdList
    }

    override  fun uploadAd(
        adObject: Ad,
        selectedFilePaths: List<String>?
    ):MutableLiveData<Boolean>
    {
        var isUploadingLiveData:MutableLiveData<Boolean> = MutableLiveData(true)
        adList.add(adObject)
        observableAdList.postValue(adList)
        isUploadingLiveData.postValue(false)
        return isUploadingLiveData
    }


    override fun getAdsByLocation(userLocationLon: Double, userLocationLat: Double): MutableLiveData<ArrayList<Ad>> {
        var closestAdsListLiveData:MutableLiveData<ArrayList<Ad>> = MutableLiveData()
        adList.sortWith(compareBy({Math.abs(it.longitude-userLocationLon)},{Math.abs(it.latitude-userLocationLat)}))

        closestAdsListLiveData.postValue(adList as ArrayList<Ad>?)
        return closestAdsListLiveData
    }
}