package com.aca.arabamsat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.aca.arabamsat.ViewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_sign_up.*
@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signUpBtn.setOnClickListener {
            val email = emailEditTxt.text.toString()
            val password = passEditTxt.text.toString()
            val confirmPassTxt = confirmPassEditTxt.text.toString()
            val name = nameEditTxt.text.toString()
            if(password.equals(confirmPassTxt)){
                loginViewModel.createUser(email,password,name).observe(this, Observer {
                    if(it){
                        loginViewModel.signInWithEmailAndPassword(email,password)
                    }else{
                        Toast.makeText(this,"Error occurred while creating your profile",Toast.LENGTH_LONG).show()
                    }
                })
            }else{
                Toast.makeText(this,"Passwords do not match",Toast.LENGTH_LONG).show()
            }


            loginViewModel.isLogedIn().observe(this, Observer {
                if(it){
                    val mainActIntent = Intent(this,MainActivity::class.java)
                    startActivity(mainActIntent)
                    finish()
                }
            })
        }
    }
}