package com.aca.arabamsat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aca.arabamsat.Adapters.ImgsRecyclerAdapter
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.ViewModels.DetailViewModel
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_main.*

class DetailActivity : AppCompatActivity() {

    val TAG = "myTag"
    private lateinit var detailViewModel: DetailViewModel
    private var selectedIndex: Int = 0;

    private lateinit var adAdapter: ImgsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val ad : Ad = intent.extras?.getSerializable("selectedAd") as Ad


        initDetailViewModel()

        populateCarInfo(ad)

        initRecyclerView(ad)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_callAd -> {
                    detailViewModel.callAdOwner()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_emailAd -> {
                    detailViewModel.emailAdOwner()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_favoriteAd -> {
                    detailViewModel.addAdToFavorite()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {false}
            }
        }

    }

    private fun populateCarInfo(ad: Ad) {

       // Glide.with(this).load(ad.pictures.get(0)).into(mainImg)

        carDetailPriceTxt.text = "${ad.price} €"
        carDetailTitleTxt.text = ad.model

        valueTextView.text = ad.model
        valueTextView2.text = ad.year
        valueTextView3.text = ad.model
        valueTextView4.text = ad.year
        valueTextView5.text = ad.model
    }

    private fun initDetailViewModel() {
        detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
    }

    fun initRecyclerView(ad: Ad) {
        adAdapter = ImgsRecyclerAdapter()
        adAdapter.submitList(ad.pictures)
        mainImgRecy.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        mainImgRecy.adapter = adAdapter
    }


}