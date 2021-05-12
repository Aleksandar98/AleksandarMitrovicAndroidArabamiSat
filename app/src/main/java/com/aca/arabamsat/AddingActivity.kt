package com.aca.arabamsat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.ViewModels.AddingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_adding.*


@AndroidEntryPoint
class AddingActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private val addingViewModel: AddingViewModel by viewModels()
    private val PICK_IMAGE = 123
    private var selectedData: Intent? = null
    private var selectedFilePaths: MutableList<String> = mutableListOf()
    private var didUploadImages: Boolean = false
    private var didSetLocation: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocationLat: Double = 0.0
    private var userLocationLon: Double = 0.0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding)
        setSupportActionBar(findViewById(R.id.toolbarAdd))

        firebaseAuth = FirebaseAuth.getInstance()

        phoneEdit.setText(firebaseAuth.currentUser.phoneNumber)
        emailEdit.setText(firebaseAuth.currentUser.email)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        uploadAdBtn.setOnClickListener {

            if (modelEdit.text.toString().isEmpty() ||
                yearEdit.text.toString().isEmpty() ||
                priceEdit.text.toString().isEmpty() ||
                phoneEdit.text.toString().isEmpty() ||
                modelEdit.text.toString().isEmpty() ||
                descEdit.text.toString().isEmpty()
            ) {

                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            } else if (didUploadImages && didSetLocation) {

                var adObject = Ad(
                    "",
                    yearEdit.text.toString(),
                    modelEdit.text.toString(),
                    priceEdit.text.toString(),
                    firebaseAuth.currentUser.uid,
                    phoneEdit.text.toString(),
                    descEdit.text.toString(),
                    firebaseAuth.currentUser.email,
                    emptyList<String>(),
                    userLocationLat,
                    userLocationLon
                )

                addingViewModel.uploadAd(adObject, selectedFilePaths).observe(this, Observer {
                    if (it) {
                        uploadingProgress.visibility = VISIBLE
                        uploadAdBtn.isEnabled = false
                        if (!hasIntenret()) {
                            uploadingProgress.visibility = GONE
                            Toast.makeText(this, "Done uploading", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        //
                    } else {
                        //
                        uploadingProgress.visibility = GONE
                        Toast.makeText(this, "Done uploading", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                })
            } else {
                if (!didSetLocation) {

                    MaterialAlertDialogBuilder(this)
                        .setTitle("Do you want to set your location?")
                        .setMessage("Set location if you want people nearby to find you easier")
                        .setPositiveButton(
                            "Set"
                        ) { dialogInterface, i ->
                            getUserLocation()
                            didSetLocation = true
                            Toast.makeText(this, "Location Set", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton(
                            "No"
                        ) { dialogInterface, i ->
                            dialogInterface.cancel()
                            didSetLocation = true
                        }
                        .show()

                } else

                    Toast.makeText(this, "Please upload at least one picture", Toast.LENGTH_LONG)
                        .show()
            }
        }

        uploadImagesBtn.setOnClickListener {
            launcImagePicker()
        }

        uploadLocation.setOnClickListener {
            getUserLocation()
            didSetLocation = true
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasIntenret(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                99
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude
                }
            }
    }

    private fun launcImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Discard current ad")
            .setMessage("Your ad wont be saved if you exit")
            .setPositiveButton(
                "Exit"
            ) { dialogInterface, i ->
                super.onBackPressed()
            }
            .setNegativeButton(
                "CANCEL"
            ) { dialogInterface, i ->
                dialogInterface.cancel()
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (requestCode == 12345) {
                uploadImages(selectedData)
            }
        } else {
            Toast.makeText(this, "Permission required", Toast.LENGTH_LONG).show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {

            selectedData = data
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    12345
                )
            } else {
                uploadImages(data)
            }


        }
    }

    private fun uploadImages(data: Intent?) {


        data?.data?.let {
            selectedFilePaths.add(ImageFilePath.getPath(this, it))
            didUploadImages = true
        }



        data?.clipData?.let {
            var i = 0
            val clipDataSize = it.itemCount
            while (i < clipDataSize) {
                selectedFilePaths.add(ImageFilePath.getPath(this, it.getItemAt(i).uri))
                i++
            }
            didUploadImages = true

        }
    }

}