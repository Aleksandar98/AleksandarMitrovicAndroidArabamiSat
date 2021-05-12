package com.aca.arabamsat.Models

data class User(
    var userId:String,
    val email:String?,
    val name:String?,
    var favoriteAds:MutableList<String>,
    var uploadIntents:MutableList<String>){
    constructor():this("","","", mutableListOf(), mutableListOf())
}