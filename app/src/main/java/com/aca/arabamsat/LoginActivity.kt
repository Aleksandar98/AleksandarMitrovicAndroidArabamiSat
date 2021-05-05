package com.aca.arabamsat

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

    private companion object{
        private const val RC_SIGN_IN = 100
        private const val TAG = "myTag"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        googleSignInBtn.setOnClickListener {
            signIn()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()

        fBbutton.setOnClickListener {
            signInFacebook()
        }
    }


    private fun signInFacebook(){
        fBbutton.registerCallback(callBackManager,object:FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFaceBookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun handleFaceBookAccessToken(accessToken: AccessToken?) {
            val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener { authResult -> 
                    val email : String? = authResult.user.email
                    Log.d(TAG, "handleFaceBookAccessToken: ${authResult.user.displayName}")
                }
                .addOnFailureListener{ e ->
                    Log.d(TAG, "handleFaceBookAccessToken: ${e.message}")
                }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "onStart: $user")
                    ///updateUI(currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
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
                firebaseAuthWithGoogle(account.idToken!!)
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