package com.aca.arabamsat.Models

import java.io.Serializable

data class Ad (
    val year:String,
    val model:String,
    val price:String,
    val userId:String,
    val phoneNuber:String,
    val description:String,
    val userName:String,
    val pictures:List<String>
): Serializable{
    constructor():this("","","","","","","", emptyList())
}