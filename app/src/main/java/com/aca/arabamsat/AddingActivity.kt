package com.aca.arabamsat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.ViewModels.AddingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_adding.*


@AndroidEntryPoint
class AddingActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private val addingViewModel: AddingViewModel by viewModels()
    private  val TAG = "myTag"
    private val PICK_IMAGE = 123;
    private var selectedData: Intent? = null
    private var selectedFilePaths: MutableList<String> = mutableListOf()
    private var didUploadImages: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding)

        firebaseAuth = FirebaseAuth.getInstance()

        phoneEdit.setText(firebaseAuth.currentUser.phoneNumber)
        emailEdit.setText(firebaseAuth.currentUser.email)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "onCreate: treba mi jos premisihe")
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(this,
                 arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                99);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions( arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1441);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    Log.d(TAG, "onCreate: ${location.latitude}")
                }
            }

        uploadAdBtn.setOnClickListener{

            if(modelEdit.text.toString().isEmpty() ||
                yearEdit.text.toString().isEmpty() ||
                priceEdit.text.toString().isEmpty() ||
                phoneEdit.text.toString().isEmpty() ||
                modelEdit.text.toString().isEmpty() ||
                descEdit.text.toString().isEmpty()){

                Toast.makeText(this,"Please fill in all fields",Toast.LENGTH_LONG).show()
            }else if(didUploadImages){

                var adObject = Ad("",yearEdit.text.toString(),modelEdit.text.toString(),priceEdit.text.toString(),firebaseAuth.currentUser.uid,phoneEdit.text.toString(),descEdit.text.toString(),firebaseAuth.currentUser.email,
                    emptyList<String>())

                addingViewModel.uploadAd(adObject,selectedFilePaths).observe(this, Observer {
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

        uploadLocation.setOnClickListener {
            getUserLocation()
        }

    }

    private fun getUserLocation() {

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
            data?.data?.let {
               // selectedData = it
                selectedFilePaths.add(ImageFilePath.getPath(this,it))


                didUploadImages = true
            }



            data?.clipData?.let {
                Log.d(TAG, "onActivityResult: ulazim ovde za vise slika")
                var i = 0;
                val clipDataSize = it.itemCount

                while (i<clipDataSize){
                    selectedFilePaths.add(ImageFilePath.getPath(this,it.getItemAt(i).uri))
                    i++
                }
                didUploadImages = true

            }

        }
    }

}