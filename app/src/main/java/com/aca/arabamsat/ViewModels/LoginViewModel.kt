package com.aca.arabamsat.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aca.arabamsat.Models.User
import com.aca.arabamsat.Repository.AuthRepository
import com.facebook.AccessToken

class LoginViewModel : ViewModel() {
    private  val TAG = "myTag"

    private var authenticatedUserLiveData = MutableLiveData<User>()

    fun getAuthenticatedUser():LiveData<User>{
        return authenticatedUserLiveData
    }

    fun signInWithEmailAndPassword(email: String, pass: String): MutableLiveData<User> {
        return AuthRepository.signInUserEmail(email,pass)
       // Log.d(TAG, "signInWithEmailAndPasswordVM: ${authenticatedUserLiveData.value}")
    }

    fun signInFacebook(accessToken: AccessToken?) {
        AuthRepository.signInUserFacebook(accessToken)
    }

    fun signInGoogle(idToken: String) {
        AuthRepository.signInUserGoogle(idToken)
    }
    fun isLogedIn():LiveData<Boolean>{
        return AuthRepository.isLogedIn();
    }
}