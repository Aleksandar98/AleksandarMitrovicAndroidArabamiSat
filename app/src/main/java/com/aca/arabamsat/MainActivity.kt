package com.aca.arabamsat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.aca.arabamsat.Adapters.AdRecyclerAdapter
import com.aca.arabamsat.ViewModels.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private val mainActivityViewModel: MainViewModel by viewModels()
    private lateinit var adAdapter: AdRecyclerAdapter
    private var showingFavorites = false
    private var showingLocationSort = true
    private val TAG = "myTag"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocationLat: Double = 0.0
    private var userLocationLon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        initRecyclerView()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mainActivityViewModel.getAds().observe(this, Observer {
            adAdapter.submitList(it)
            adAdapter.notifyDataSetChanged()
        })

        mainActivityViewModel.isLoading().observe(this, Observer {
            //TODO Fix
            if (!it)
                progressBar.visibility = GONE
        })

        mainActivityViewModel.isLogedIn().observe(this, Observer {
            if (!it) {
                val loginActIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginActIntent)
                finish()
            }
        })

        fab.setOnClickListener {
            val intent = Intent(this, AddingActivity::class.java)
            startActivity(intent)
        }

        adAdapter.onItemClick = { ad ->
            Log.d(TAG, "onCreate: item: ${ad.model}")
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("selectedAd", ad)
            startActivity(intent)
        }

        checkIfImagesForUpload()


    }

    private fun checkIfImagesForUpload() {
        mainActivityViewModel.uploadCachedImages()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_items, menu)
        return true
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
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        userLocationLat = location.latitude
                        userLocationLon = location.longitude
                        mainActivityViewModel.getAdsByLocation(userLocationLat, userLocationLon)
                            .observe(this, Observer {
                                for (ad in it)
                                    Log.d(TAG, "onRequestPermissionsResult: sortirano ${ad}")
                                adAdapter.submitList(it)
                                adAdapter.notifyDataSetChanged()
                            })
                    }
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_showFavorites -> {
            if (showingFavorites) {
                item.title = "Show favorites"
                showingFavorites = false
                mainActivityViewModel.getAds().observe(this, Observer {
                    adAdapter.submitList(it)
                    adAdapter.notifyDataSetChanged()
                })
            } else {

                showingFavorites = true
                item.title = "Show all"
                mainActivityViewModel.getFavoriteAds().observe(this, Observer {
                    adAdapter.submitList(it)
                    adAdapter.notifyDataSetChanged()
                })
            }

            true
        }

        R.id.action_sortLocation -> {
            if (!showingLocationSort) {
                getUserLocation()
                item.title = "Sort by location"
                showingLocationSort = true

            } else {

                showingLocationSort = false
                item.title = "Don't sort by location"
                mainActivityViewModel.getAds().observe(this, Observer {
                    adAdapter.submitList(it)
                    adAdapter.notifyDataSetChanged()
                })
            }
            true
        }

        R.id.action_logout -> {
            mainActivityViewModel.logoutUser()
            true
        }

        else -> {

            super.onOptionsItemSelected(item)
        }
    }

    fun initRecyclerView() {
        adAdapter = AdRecyclerAdapter()

        carsRecyclerView.layoutManager = LinearLayoutManager(this)
        carsRecyclerView.adapter = adAdapter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLocationLat = location.latitude
                    userLocationLon = location.longitude
                    mainActivityViewModel.getAdsByLocation(userLocationLat, userLocationLon)
                        .observe(this, Observer {
                            for (ad in it)
                                Log.d(TAG, "onRequestPermissionsResult: sortirano ${ad}")
                            adAdapter.submitList(it)
                            adAdapter.notifyDataSetChanged()
                        })
                }
            }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}