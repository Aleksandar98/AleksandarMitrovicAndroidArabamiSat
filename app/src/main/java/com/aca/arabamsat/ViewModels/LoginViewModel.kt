package com.aca.arabamsat.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aca.arabamsat.Models.User
import com.aca.arabamsat.Repository.AuthRepository
import com.facebook.AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authRepository: AuthRepository
) : ViewModel() {
    private  val TAG = "myTag"

    private var authenticatedUserLiveData = MutableLiveData<User>()

    fun getAuthenticatedUser():LiveData<User>{
        return authenticatedUserLiveData
    }

    fun signInWithEmailAndPassword(email: String, pass: String): MutableLiveData<User> {
        return authRepository.signInUserEmail(email,pass)
       // Log.d(TAG, "signInWithEmailAndPasswordVM: ${authenticatedUserLiveData.value}")
    }

    fun signInFacebook(accessToken: AccessToken?) {
        authRepository.signInUserFacebook(accessToken)
    }

    fun signInGoogle(idToken: String) {
        authRepository.signInUserGoogle(idToken)
    }
    fun isLogedIn():LiveData<Boolean>{
        return authRepository.isLogedIn();
    }

    fun sendPasswordResetMail() {
        authRepository.sendPasswordResetMail()
    }

    fun createUser(email: String, password: String):LiveData<Boolean> {
        return authRepository.crateUser(email,password)
    }
}