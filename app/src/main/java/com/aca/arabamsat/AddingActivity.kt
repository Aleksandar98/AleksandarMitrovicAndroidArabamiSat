package com.aca.arabamsat

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aca.arabamsat.ViewModels.AddingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_adding.*
@AndroidEntryPoint
class AddingActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private val addingViewModel: AddingViewModel by viewModels()
    private  val TAG = "myTag"
    private val PICK_IMAGE = 123;
    private var selectedData: Intent? = null
    private var didUploadImages: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding)

        firebaseAuth = FirebaseAuth.getInstance()

        phoneEdit.setText(firebaseAuth.currentUser.phoneNumber)
        emailEdit.setText(firebaseAuth.currentUser.email)



        uploadAdBtn.setOnClickListener{

            if(modelEdit.text.toString().isEmpty() ||
                yearEdit.text.toString().isEmpty() ||
                priceEdit.text.toString().isEmpty() ||
                phoneEdit.text.toString().isEmpty() ||
                modelEdit.text.toString().isEmpty() ||
                descEdit.text.toString().isEmpty()){

                Toast.makeText(this,"Please fill in all fields",Toast.LENGTH_LONG).show()
            }else if(didUploadImages){

                val adObject = hashMapOf(
                    "model" to modelEdit.text.toString(),
                    "year" to yearEdit.text.toString(),
                    "price" to priceEdit.text.toString(),
                    "userId" to firebaseAuth.currentUser.uid,
                    "phoneNuber" to phoneEdit.text.toString(),
                    "description" to descEdit.text.toString(),
                    "email" to firebaseAuth.currentUser.email
                )

                addingViewModel.uploadAd(adObject,selectedData).observe(this, Observer {
                    if(it){
                        uploadingProgress.visibility = VISIBLE
                        //
                    }else{
                        //
                        uploadingProgress.visibility = GONE
                        Toast.makeText(this,"Done uploading",Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                Toast.makeText(this,"Please upload at least one picture",Toast.LENGTH_LONG).show()
            }



        }

        uploadImagesBtn.setOnClickListener {
            launcImagePicker()
        }

    }

    private fun launcImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE){
            data?.let {
                selectedData = it
                didUploadImages = true
            }
        }
    }

}