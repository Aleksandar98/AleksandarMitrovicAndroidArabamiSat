package com.aca.arabamsat.Models

data class Ad(
    val year:String,
    val model:String,
    val price:String,
    val userId:String,
    val phoneNuber:String,
    val description:String,
    val userName:String,
    val pictures:List<String>
){
    constructor():this("","","","","","","", emptyList())
}