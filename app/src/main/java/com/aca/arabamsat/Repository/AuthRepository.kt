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
import javax.inject.Inject

class AuthRepository @Inject constructor(
     val firebaseAuth:FirebaseAuth
){

    private  val TAG = "myTag"
    var isLogedInLiveData:MutableLiveData<Boolean>
    var isUserCreatedLiveData:MutableLiveData<Boolean> = MutableLiveData()

    init {

        if(firebaseAuth.currentUser!= null){
            isLogedInLiveData = MutableLiveData<Boolean>(true )
        }else{
            isLogedInLiveData = MutableLiveData<Boolean>(false )
        }

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
                    isLogedInLiveData.postValue(true)
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

    fun signInUserFacebook(accessToken: AccessToken?):MutableLiveData<User>{
        lateinit var logedInUser:User
        val signInUser:MutableLiveData<User> = MutableLiveData<User>()
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val email : String? = authResult.user.email
                val displayName : String? = authResult.user.displayName
                logedInUser = User(email,displayName)
                isLogedInLiveData.postValue(true)
                signInUser.value = logedInUser
                Log.d(TAG, "handleFaceBookAccessToken: ${authResult.user.displayName}")

            }
            .addOnFailureListener{ e ->
                Log.d(TAG, "handleFaceBookAccessToken: ${e.message}")
            }
        return signInUser
    }

    fun signInUserGoogle(idToken: String): MutableLiveData<User>{
        lateinit var logedInUser:User
        val signInUser:MutableLiveData<User> = MutableLiveData<User>()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    logedInUser = User(user.email,user.displayName)
                    isLogedInLiveData.postValue(true)
                    signInUser.value = logedInUser
                    Log.d(TAG, "onStart: $user")
                    //startActivity(mainActIntent)
                    ///updateUI(currentUser)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
        return signInUser
    }

    fun logoutUser() {
        firebaseAuth.signOut()
        isLogedInLiveData.postValue(false)

    }

    fun isLogedIn(): MutableLiveData<Boolean> {
       // if(firebaseAuth.currentUser!= null)
           // isLogedInLiveData.postValue(true)
        return isLogedInLiveData
    }

    fun sendPasswordResetMail() {
        firebaseAuth.sendPasswordResetEmail(firebaseAuth.currentUser.email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                }
            }
    }

    fun crateUser(email: String, password: String): MutableLiveData<Boolean> {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = firebaseAuth.currentUser
                    isUserCreatedLiveData.postValue(true)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    isUserCreatedLiveData.postValue(false)

                }
            }
        return isUserCreatedLiveData
    }
}