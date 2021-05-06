package com.aca.arabamsat.Repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.LoginActivity
import com.aca.arabamsat.Models.User
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

object AuthRepository{

    private const val TAG = "myTag"
    val firebaseAuth :FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun signInUserEmail(email:String,pass:String):MutableLiveData<User>{
        lateinit var logedInUser:User
        val signInUser:MutableLiveData<User> = MutableLiveData<User>()
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = firebaseAuth.currentUser
                    logedInUser = User(user.email,user.displayName)
                    signInUser.value = logedInUser
                    Log.d(TAG, "signInWithEmail:success value ${ signInUser.value}")
                    //startActivity(mainActIntent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
                    // updateUI(null)
                }
            }
        return signInUser

    }

    fun signInUserFacebook(accessToken: AccessToken?){
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

    fun signInUserGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    Log.d(TAG, "onStart: $user")
                    //startActivity(mainActIntent)
                    ///updateUI(currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }
}