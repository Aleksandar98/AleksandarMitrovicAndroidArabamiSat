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
   /// private lateinit var  loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //loginViewModel = ViewModelProvider(this,ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(LoginViewModel::class.java)

        signUpBtn.setOnClickListener {
            val email = emailEditTxt.text.toString()
            val password = passEditTxt.text.toString()
            loginViewModel.createUser(email,password).observe(this, Observer {
                if(it){
                    loginViewModel.signInWithEmailAndPassword(email,password)
                }else{
                    Toast.makeText(this,"Error occurred while creating your profile",Toast.LENGTH_LONG).show()
                }
            })

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