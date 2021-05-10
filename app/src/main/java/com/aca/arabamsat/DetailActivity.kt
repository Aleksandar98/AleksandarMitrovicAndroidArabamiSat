package com.aca.arabamsat

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.Menu
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

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    val TAG = "myTag"
    private val detailViewModel: DetailViewModel by viewModels()
    private var selectedIndex: Int = 0;

    private lateinit var adAdapter: ImgsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val ad : Ad = intent.extras?.getSerializable("selectedAd") as Ad


        populateCarInfo(ad)

        initRecyclerView(ad)

        detailViewModel.isAdFavorite(ad.adId).observe(this, Observer {
            if(it){
                bottom_navigation.menu.findItem(R.id.action_favoriteAd).setIcon(R.drawable.ic_baseline_favorite_fill_24)
            }else{

                bottom_navigation.menu.findItem(R.id.action_favoriteAd).setIcon(R.drawable.ic_baseline_favorite_border_24)
            }
        })


        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
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
                else -> {false}
            }
        }

    }

    private fun callAdOwner(phoneNumber: String) {
    when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED -> {
            // You can use the API that requires the permission.
            val intent = Intent(Intent.ACTION_DIAL)

            intent.data = Uri.parse("tel:" + phoneNumber)
            startActivity(intent)
        }
        else -> {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                99
            );
        }
    }


    }

    private fun emailAdOwner(userName: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto: "+userName) // only email apps should handle this

        intent.putExtra(Intent.EXTRA_EMAIL, userName)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Interested in buying your car")
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent)
        }
    }

    private fun populateCarInfo(ad: Ad) {

       // Glide.with(this).load(ad.pictures.get(0)).into(mainImg)

        carDetailPriceTxt.text = "${ad.price} €"
        carDetailTitleTxt.text = ad.model

        valueTextView.text = ad.model
        valueTextView2.text = ad.year
        valueTextView3.text = ad.userName
        valueTextView4.text = ad.description
    }


    fun initRecyclerView(ad: Ad) {
        adAdapter = ImgsRecyclerAdapter()
        adAdapter.submitList(ad.pictures)
        mainImgRecy.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        mainImgRecy.adapter = adAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.bottom_navigation_menu,menu)
        val favIcon = menu?.findItem(R.id.action_favoriteAd)
        favIcon?.setIcon(R.drawable.ic_baseline_email_24)
        return super.onCreateOptionsMenu(menu)


    }
}