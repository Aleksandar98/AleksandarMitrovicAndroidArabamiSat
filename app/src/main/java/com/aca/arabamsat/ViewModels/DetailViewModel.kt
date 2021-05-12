package com.aca.arabamsat.ViewModels

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aca.arabamsat.Interfaces.UserRepo
import com.aca.arabamsat.Repository.AdRepository
import com.aca.arabamsat.Repository.AuthRepository
import com.aca.arabamsat.Repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(
    val userRepository: UserRepo
): ViewModel(){

    fun addAdToFavorite(adId:String){
        userRepository.addToFavorite(adId)
    }

    fun isAdFavorite(adId: String): LiveData<Boolean> {
        return userRepository.isAdFavorite(adId)
    }

}