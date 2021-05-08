package com.aca.arabamsat.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.R
import com.bumptech.glide.Glide


class ImgsRecyclerAdapter() :
    RecyclerView.Adapter<ImgsRecyclerAdapter.ViewHolder>() {

    private var dataSet: List<String> = ArrayList()
    inner class ViewHolder constructor(view: View): RecyclerView.ViewHolder(view){

        val carImg: ImageView

        init {
            carImg = view.findViewById(R.id.carImgDetail)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_img_item,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            Glide.with(holder.carImg).load(dataSet.get(position))
                .into(holder.carImg)

    }

    fun submitList(adList: List<String>){
        dataSet = adList
    }
}