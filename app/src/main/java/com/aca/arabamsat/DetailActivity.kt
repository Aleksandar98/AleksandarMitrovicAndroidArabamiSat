package com.aca.arabamsat

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_DIAL
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.aca.arabamsat.Adapters.ImgsRecyclerAdapter
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.ViewModels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_detail.*
import android.content.Intent.ACTION_DIAL
import android.os.Build
import androidx.annotation.RequiresApi

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var globalPhoneNumber: String
    private val detailViewModel: DetailViewModel by viewModels()

    private lateinit var adAdapter: ImgsRecyclerAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val ad: Ad = intent.extras?.getSerializable("selectedAd") as Ad


        populateCarInfo(ad)

        initRecyclerView(ad)

        carInfoScrollView.viewTreeObserver.addOnScrollChangedListener {

            if (carInfoScrollView.scrollY < 170) {
                bottom_navigation.visibility = View.VISIBLE

            } else {
                bottom_navigation.visibility = View.GONE
            }
        }
        detailViewModel.isAdFavorite(ad.adId).observe(this, Observer {
            if (it) {
                bottom_navigation.menu.findItem(R.id.action_favoriteAd)
                    .setIcon(R.drawable.ic_baseline_favorite_fill_24)
            } else {

                bottom_navigation.menu.findItem(R.id.action_favoriteAd)
                    .setIcon(R.drawable.ic_baseline_favorite_border_24)
            }
        })


        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_callAd -> {
                    callAdOwner(ad.phoneNumber)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_emailAd -> {
                    emailAdOwner(ad.userName)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_favoriteAd -> {
                    detailViewModel.addAdToFavorite(ad.adId)

                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    false
                }
            }
        }

    }

    private fun callAdOwner(phoneNumber: String) {
        globalPhoneNumber = phoneNumber
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                val intent = Intent(Intent.ACTION_DIAL)

                intent.data = Uri.parse("tel:" + phoneNumber)
                startActivity(intent)
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    1919
                )
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (requestCode == 1919) {
                val intent = Intent(Intent.ACTION_DIAL)

                intent.data = Uri.parse("tel:" + globalPhoneNumber)
                startActivity(intent)

            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun emailAdOwner(userName: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto: " + userName)

        intent.putExtra(Intent.EXTRA_EMAIL, userName)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Interested in buying your car")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun populateCarInfo(ad: Ad) {
        carDetailPriceTxt.text = "${ad.price} €"
        carDetailTitleTxt.text = ad.model

        valueTextView.text = ad.model
        valueTextView2.text = ad.year
        valueTextView3.text = ad.userName
        valueTextView4.text = ad.description
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun initRecyclerView(ad: Ad) {
        adAdapter = ImgsRecyclerAdapter()
        adAdapter.submitList(ad.pictures)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mainImgRecy.layoutManager = linearLayoutManager
        mainImgRecy.adapter = adAdapter

        pageNumberTxt.text = "${linearLayoutManager.itemCount} Images"

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
        val favIcon = menu?.findItem(R.id.action_favoriteAd)
        favIcon?.setIcon(R.drawable.ic_baseline_email_24)
        return super.onCreateOptionsMenu(menu)


    }
}