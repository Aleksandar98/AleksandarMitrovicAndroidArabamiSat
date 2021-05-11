package com.aca.arabamsat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aca.arabamsat.ViewModels.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {


    private val loginViewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        loginViewModel.isLogedIn().observe(this, Observer {
            if(it){
                val mainActIntent = Intent(this,MainActivity::class.java)
                startActivity(mainActIntent)
                finish()
            }else{
                val loginActIntent = Intent(this,LoginActivity::class.java)
                startActivity(loginActIntent)
                finish()
            }
        })
    }
}