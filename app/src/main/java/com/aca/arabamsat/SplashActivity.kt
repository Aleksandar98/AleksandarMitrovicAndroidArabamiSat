package com.aca.arabamsat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aca.arabamsat.ViewModels.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {


    private lateinit var loginViewModel: LoginViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initLoginViewModel()

        firebaseAuth = FirebaseAuth.getInstance()

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

//    override fun onStart() {
//        super.onStart()
//
//        val currentUser = firebaseAuth.currentUser
//
//        Log.d("myTag", "onStart: ${currentUser.email}")
//        if(currentUser!= null){
//            val mainActIntent = Intent(this,MainActivity::class.java)
//            startActivity(mainActIntent)
//            finish()
//
//        }else{
//            val loginActIntent = Intent(this,LoginActivity::class.java)
//            startActivity(loginActIntent)
//            finish()
//        }
//    }

    private fun initLoginViewModel() {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }
}