package com.aca.arabamsat.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Repository.AdRepository
import com.aca.arabamsat.Repository.AuthRepository

class MainViewModel :ViewModel(){

    private var isLoading:MutableLiveData<Boolean> = MutableLiveData()

    fun getAds():LiveData<List<Ad>> {
        isLoading.postValue(true)
        var liveData = AdRepository.getAllAds()
        isLoading.postValue(false)
        return liveData
    }

    fun isLoading():LiveData<Boolean>{
        return isLoading
    }

    fun logoutUser() {
        AuthRepository.logoutUser()
    }

    fun isLogedIn():LiveData<Boolean>{
        return AuthRepository.isLogedIn()
    }

    fun isDatabaseEmpty():Boolean {
        return AdRepository.isDataBaseEmpry()
    }


}
