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


    fun signInWithEmailAndPassword(email: String, pass: String): MutableLiveData<String> {
        if(email.isNotEmpty() && email.isNotBlank() && pass.isNotEmpty() && pass.isNotBlank())
            return authRepository.signInUserEmail(email,pass)
        else
            return MutableLiveData("Email or password is empty")
    }

    fun signInFacebook(accessToken: AccessToken?) {
        authRepository.signInUserFacebook(accessToken)
    }

    fun signInGoogle(idToken: String) {
        authRepository.signInUserGoogle(idToken)
    }
    fun isLogedIn():LiveData<Boolean>{
        return authRepository.isLogedIn()
    }

    fun sendPasswordResetMail(email:String) {
        if(email.isNotEmpty() && email.isNotBlank())
        authRepository.sendPasswordResetMail(email)
    }

    fun createUser(email: String, password: String,name:String):LiveData<Boolean> {
        if(email.isNotEmpty() && email.isNotBlank() && password.isNotEmpty() && password.isNotBlank() && name.isNotEmpty() && name.isNotBlank())
            return authRepository.crateUser(email,password,name)
        else
            return MutableLiveData(false)
    }
}