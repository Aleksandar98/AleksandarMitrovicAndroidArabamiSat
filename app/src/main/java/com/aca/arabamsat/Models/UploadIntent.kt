package com.aca.arabamsat.Models

data class UploadIntent(
    var intentId:String,
    var userId:String,
    var adId:String,
    var filePaths:List<String>?
){
    constructor():this("","","", emptyList())
}