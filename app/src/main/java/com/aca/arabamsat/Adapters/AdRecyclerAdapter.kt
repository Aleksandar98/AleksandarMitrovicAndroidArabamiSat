package com.aca.arabamsat.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aca.arabamsat.Models.Ad
import com.aca.arabamsat.R
import com.bumptech.glide.Glide

class AdRecyclerAdapter() :
    RecyclerView.Adapter<AdRecyclerAdapter.ViewHolder>() {

    private var dataSet: List<Ad> = ArrayList()
    var onItemClick: ((Ad) -> Unit)? = null
    inner class ViewHolder constructor(view: View): RecyclerView.ViewHolder(view){

        val priceText: TextView
        val modelText: TextView
        val yearText: TextView
        val carImg: ImageView

        init {
            priceText = view.findViewById(R.id.priceTxtView)
            modelText = view.findViewById(R.id.modelTxtView)
            yearText = view.findViewById(R.id.yearTxtView)
            carImg = view.findViewById(R.id.carImgView)

            itemView.setOnClickListener {
                onItemClick?.invoke(dataSet[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ad_item,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.priceText.text = "${dataSet.get(position).price} €"
        holder.modelText.text = dataSet.get(position).model
        holder.yearText.text = dataSet.get(position).year
        if(dataSet.get(position).pictures.isNotEmpty()) {
            Glide.with(holder.carImg).load(dataSet.get(position).pictures.get(0))
                .into(holder.carImg)
        }
    }

    fun submitList(adList: List<Ad>){
        dataSet = adList
    }
}