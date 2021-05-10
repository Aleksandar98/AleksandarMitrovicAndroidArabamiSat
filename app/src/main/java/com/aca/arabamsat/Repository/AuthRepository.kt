package com.aca.arabamsat.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.aca.arabamsat.Models.User
import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class AuthRepository @Inject constructor(
     val firebaseAuth:FirebaseAuth,
     val db: FirebaseFirestore
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
                    logedInUser = User(user.uid,user.email,user.displayName, mutableListOf())

                    signInUser.value = logedInUser
                    isLogedInLiveData.postValue(true)
                    Log.d(TAG, "signInWithEmail:success value ${ signInUser.value}")
                    //startActivity(mainActIntent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
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
                val userid : String = authResult.user.uid
                logedInUser = User(userid,email,displayName, mutableListOf())
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
        lateinit var user:User
        val signInUser:MutableLiveData<User> = MutableLiveData<User>()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val userAuth = firebaseAuth.currentUser
                    user = User(userAuth.uid,userAuth.email,userAuth.displayName, mutableListOf())

                    createUserIfNotExists(user)

                    isLogedInLiveData.postValue(true)
                    signInUser.value = user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
        return signInUser
    }

    private fun createUserIfNotExists(user: User){
        Log.d(TAG, "createUserIfNotExists: proveravam da li postoji korisnik sa id ${user.userId}")
        db.collection("Users").whereEqualTo("userId",user.userId).get()
            .addOnCompleteListener {
                if (it.getResult().isEmpty) {
                    db.collection("Users").add(user)
                }
            }


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

    fun crateUser(email: String, password: String,name:String): MutableLiveData<Boolean> {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val userAuth = firebaseAuth.currentUser
                    var user = User(userAuth.uid,userAuth.email,name, mutableListOf())
                    db.collection("Users").add(user)
                        .addOnSuccessListener {

                            isUserCreatedLiveData.postValue(true)
                        }
                        .addOnFailureListener {
                            isUserCreatedLiveData.postValue(false)
                        }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    isUserCreatedLiveData.postValue(false)

                }
            }
        return isUserCreatedLiveData
    }


}