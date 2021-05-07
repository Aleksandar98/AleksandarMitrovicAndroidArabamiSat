package com.aca.arabamsat

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aca.arabamsat.Repository.AuthRepository
import com.aca.arabamsat.ViewModels.LoginViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callBackManager: CallbackManager
    private lateinit var mainActIntent: Intent
    private lateinit var loginViewModel: LoginViewModel

    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "myTag"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        mainActIntent = Intent(this,MainActivity::class.java)

        firebaseAuth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()



        initLoginViewModel()

        googleSignInBtn.setOnClickListener {
            signInGoogle()
        }

        fBbutton.setOnClickListener {
            signInFacebook()
        }

        loginBtn.setOnClickListener {
            signInEmailPass(emailEditTxt.text.toString(),passEditTxt.text.toString())
        }

        loginViewModel.isLogedIn().observe(this, Observer {
            if(it){
                val mainActIntent = Intent(this,MainActivity::class.java)
                startActivity(mainActIntent)
                finish()
            }
        })
    }

    private fun initLoginViewModel() {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    private fun signInEmailPass(email: String, pass: String) {
        loginViewModel.signInWithEmailAndPassword(email, pass)

    }


    private fun signInFacebook(){
        fBbutton.registerCallback(callBackManager,object:FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                loginViewModel.signInFacebook(result!!.accessToken)
               // handleFaceBookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                TODO("Not yet implemented")
            }

        })
    }




    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                loginViewModel.signInGoogle(account.idToken!!)
                //firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
        callBackManager.onActivityResult(requestCode,resultCode, data)
    }



    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = firebaseAuth.currentUser
        //Log.d(TAG, "onStart: ${currentUser!!.displayName}")
        ///updateUI(currentUser)
    }
}