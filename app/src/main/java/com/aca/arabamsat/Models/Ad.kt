package com.aca.arabamsat.Models

import java.io.Serializable

data class Ad (
    var adId:String,
    var year:String,
    var model:String,
    var price:String,
    var userId:String,
    var phoneNumber:String,
    var description:String,
    var userName:String,
    var pictures:List<String>,
    var latitude: Double,
    var longitude: Double
): Serializable{
    constructor():this("","","","","","","","", emptyList(),0.0, 0.0)
}