package com.aca.arabamsat.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Repository.AdRepository
import com.aca.arabamsat.Repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val adRepository: AdRepository
) :ViewModel(){

    private var isLoading:MutableLiveData<Boolean> = MutableLiveData()


    fun getAds():LiveData<List<Ad>> {
        isLoading.postValue(true)
        var liveData = adRepository.getAllAds()
        isLoading.postValue(false)
        return liveData
    }

    fun isLoading():LiveData<Boolean>{
        return isLoading
    }

    fun logoutUser() {
        authRepository.logoutUser()
    }

    fun isLogedIn():LiveData<Boolean>{
        return authRepository.isLogedIn()
    }

    fun isDatabaseEmpty():Boolean {
        return adRepository.isDataBaseEmpry()

    }


}
