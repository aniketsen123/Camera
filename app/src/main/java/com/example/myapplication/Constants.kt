package com.example.myapplication

import android.Manifest

object Constants {

    const val TAG="camerax"
    const val fileFormat="yy-MM-dd-HH-mm-ss-sss"
    const val REQUES_CODE_PERMISSION=101
    val REQUIRED_PERMISSION= arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
}