package com.aca.arabamsat.ViewModels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.Repository.AdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddingViewModel @Inject constructor(
    val adRepository: AdRepository
) : ViewModel(){

    private val TAG = "myTag"

    fun uploadAd(
        adObject: Ad,
        selectedFilePaths: MutableList<String>
    ):LiveData<Boolean> {
//
//        val uriList:MutableList<Uri> = mutableListOf()
//
//        data?.clipData?.let {
//            var i = 0;
//            val clipDataSize = it.itemCount
//
//            while (i<clipDataSize){
//                uriList.add(it.getItemAt(i).uri)
//                i++
//            }
//
//        }
//
//        data?.data?.let{
//            uriList.add(it)
//        }
//
//        Log.d(TAG, "uploadAd: ${uriList.size}")
       return adRepository.uploadAd(adObject,selectedFilePaths)
    }

}