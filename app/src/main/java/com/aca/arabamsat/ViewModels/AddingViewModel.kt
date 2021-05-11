package com.aca.arabamsat.ViewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aca.arabamsat.Interfaces.AdRepo
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Repository.AdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddingViewModel @Inject constructor(
    val adRepository: AdRepo
) : ViewModel(){

    fun uploadAd(
        adObject: Ad,
        selectedFilePaths: MutableList<String>
    ):LiveData<Boolean> {

       return adRepository.uploadAd(adObject,selectedFilePaths)
    }

}