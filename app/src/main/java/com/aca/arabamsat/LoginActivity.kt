package com.aca.arabamsat

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Xml
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.aca.arabamsat.ViewModels.LoginViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import org.xmlpull.v1.XmlPullParser
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callBackManager: CallbackManager
    private lateinit var mainActIntent: Intent
    private val loginViewModel: LoginViewModel by viewModels()

    private companion object {
        private const val RC_SIGN_IN = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        mainActIntent = Intent(this, MainActivity::class.java)

        firebaseAuth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()

        googleSignInBtn.setOnClickListener {
            signInGoogle()
        }

        fBbutton.setOnClickListener {
            signInFacebook()
        }

        forgotPassTxt.setOnClickListener {
            showConfirmResetDialog()
        }

        createAccTxt.setOnClickListener {
            val mainActIntent = Intent(this, SignUpActivity::class.java)
            startActivity(mainActIntent)
        }

        loginBtn.setOnClickListener {
            signInEmailPass(emailEditTxt.text.toString(), passEditTxt.text.toString())
        }

        loginViewModel.isLogedIn().observe(this, Observer {
            if (it) {
                val mainActIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActIntent)
                finish()
            }
        })
    }

    private fun showConfirmResetDialog() {

        val editText = EditText(this)
        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS


        MaterialAlertDialogBuilder(this)
            .setTitle("Reset password")
            .setMessage("Enter email address assosiated with your account")
            .setView(editText)
            .setPositiveButton("Send") { dialog, which ->
                loginViewModel.sendPasswordResetMail(editText.text.toString())
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    private fun signInEmailPass(email: String, pass: String) {
        loginViewModel.signInWithEmailAndPassword(email, pass).observe(this, Observer {
            Toast.makeText(this,it,Toast.LENGTH_LONG).show()
        })

    }


    private fun signInFacebook() {
        fBbutton.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                loginViewModel.signInFacebook(result!!.accessToken)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }

        })
    }


    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                loginViewModel.signInGoogle(account.idToken!!)
            } catch (e: ApiException) {
            }
        }
        callBackManager.onActivityResult(requestCode, resultCode, data)
    }

}