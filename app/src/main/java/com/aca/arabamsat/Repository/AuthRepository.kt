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
    val firebaseAuth: FirebaseAuth,
    val db: FirebaseFirestore
) {

    var isLogedInLiveData: MutableLiveData<Boolean>
    var isUserCreatedLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {

        if (firebaseAuth.currentUser != null) {
            isLogedInLiveData = MutableLiveData<Boolean>(true)
        } else {
            isLogedInLiveData = MutableLiveData<Boolean>(false)
        }
    }


    fun signInUserEmail(email: String, pass: String): MutableLiveData<String> {
        //lateinit var logedInUser: User
        val message: MutableLiveData<String> = MutableLiveData<String>()
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    isLogedInLiveData.postValue(true)

                }
            }
            .addOnFailureListener { exception ->
                message.postValue(exception.message)
            }
        return message

    }

    fun signInUserFacebook(accessToken: AccessToken?): MutableLiveData<User> {
        lateinit var logedInUser: User
        val signInUser: MutableLiveData<User> = MutableLiveData<User>()
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val email: String? = authResult.user.email
                val displayName: String? = authResult.user.displayName
                val userid: String = authResult.user.uid
                logedInUser = User(userid, email, displayName, mutableListOf(), mutableListOf())
                isLogedInLiveData.postValue(true)
                signInUser.value = logedInUser

            }
            .addOnFailureListener { e ->

            }
        return signInUser
    }

    fun signInUserGoogle(idToken: String): MutableLiveData<User> {
        lateinit var user: User
        val signInUser: MutableLiveData<User> = MutableLiveData<User>()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userAuth = firebaseAuth.currentUser
                    user = User(
                        userAuth.uid,
                        userAuth.email,
                        userAuth.displayName,
                        mutableListOf(),
                        mutableListOf()
                    )

                    createUserIfNotExists(user)

                    isLogedInLiveData.postValue(true)
                    signInUser.value = user
                } else {

                }
            }
        return signInUser
    }

    private fun createUserIfNotExists(user: User) {
        db.collection("Users").whereEqualTo("userId", user.userId).get()
            .addOnCompleteListener {
                if (it.result.isEmpty) {
                    db.collection("Users").add(user)
                }
            }
    }

    fun logoutUser() {
        firebaseAuth.signOut()
        isLogedInLiveData.postValue(false)

    }

    fun isLogedIn(): MutableLiveData<Boolean> {
        return isLogedInLiveData
    }

    fun sendPasswordResetMail(email:String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                }
            }
    }

    fun crateUser(email: String, password: String, name: String): MutableLiveData<Boolean> {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userAuth = firebaseAuth.currentUser
                    var user =
                        User(userAuth.uid, userAuth.email, name, mutableListOf(), mutableListOf())
                    db.collection("Users").add(user)
                        .addOnSuccessListener {

                            isUserCreatedLiveData.postValue(true)
                        }
                        .addOnFailureListener {
                            isUserCreatedLiveData.postValue(false)
                        }

                } else {
                    isUserCreatedLiveData.postValue(false)

                }
            }
        return isUserCreatedLiveData
    }


}