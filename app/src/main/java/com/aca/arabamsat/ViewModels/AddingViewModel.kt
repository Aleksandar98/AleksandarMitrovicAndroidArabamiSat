package com.aca.arabamsat.ViewModels

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.aca.arabamsat.Repository.AdRepository

class AddingViewModel : ViewModel(){

    private val TAG = "myTag"

    fun uploadAd(
        adObject: HashMap<String, String>,
        data: Intent?
    ):LiveData<Boolean> {

        val uriList:MutableList<Uri> = mutableListOf()

        data?.clipData?.let {
            var i = 0;
            val clipDataSize = it.itemCount

            while (i<clipDataSize){
                uriList.add(it.getItemAt(i).uri)
                i++
            }

        }

        data?.data?.let{
            uriList.add(it)
        }

        Log.d(TAG, "uploadAd: ${uriList.size}")
       return AdRepository.uploadAd(adObject,uriList)
    }

}