package com.aca.arabamsat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser

        Log.d("myTag", "onStart: $currentUser")
        if(currentUser!= null){
            val mainActIntent = Intent(this,MainActivity::class.java)
            startActivity(mainActIntent)
            finish()

        }else{
            val loginActIntent = Intent(this,LoginActivity::class.java)
            startActivity(loginActIntent)
            finish()
        }
    }
}