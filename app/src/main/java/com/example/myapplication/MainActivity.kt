package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import org.w3c.dom.Text
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    var lan:Double=0.0
    var log:Double=0.0
    private lateinit var fusedLocation:FusedLocationProviderClient

    private lateinit var binding: ActivityMainBinding
    private var imageCapture:ImageCapture?=null
    private lateinit var outputDirectry: File
    private lateinit var gecoder:Geocoder
   private lateinit var locationmanager:LocationManager
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding= ActivityMainBinding.inflate(layoutInflater)
        outputDirectry=getOutputDirectory()
        setContentView(binding.root)
    fusedLocation=LocationServices.getFusedLocationProviderClient(this)
        gecoder= Geocoder(this,Locale.getDefault())
        if(allpermissiongranted())
        {
            val lastlocation=fusedLocation.lastLocation
            lastlocation.addOnSuccessListener {
                lan = it.latitude
                log = it.longitude

                val address = gecoder.getFromLocation(lan, log, 1)
                Log.d("LOCATION", "${address?.get(0)?.getAddressLine(0)}")
                Log.d("LOCATION", "${address?.get(0)?.locality}")

                val textView = findViewById<TextView>(R.id.testText)
                textView.text =address?.get(0)?.locality.toString()
                val c = Calendar.getInstance()

                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                val stri=day.toString()+"-"+month.toString()+"-"+year.toString()
                val textview1=findViewById<TextView>(R.id.testText1)
                textview1.text=stri
                val time=hour.toString()+":"+minute.toString()
                val textview2=findViewById<TextView>(R.id.testText2)
                textview2.text=time
            }
            startCamera()
            Toast.makeText(this,"GRANTED ALL PERMISSION", Toast.LENGTH_SHORT).show()
        }
        else
        {
            ActivityCompat.requestPermissions(this,Constants.REQUIRED_PERMISSION,Constants.REQUES_CODE_PERMISSION)
        }
        binding.cameraButton.setOnClickListener{
            takePhoto()
        }
    }





    private fun getOutputDirectory(): File {
        val mediaDIr=externalMediaDirs.firstOrNull()?.let {
            File(it,resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return  if(mediaDIr!=null && mediaDIr.exists())
            mediaDIr else filesDir
    }

    private fun takePhoto() {
        val imageCapture=imageCapture?:return

        val photofile=
            File(outputDirectry, SimpleDateFormat(Constants.fileFormat, Locale.getDefault()).format(System.currentTimeMillis())+".jpg")
        val outputOption=androidx.camera.core.ImageCapture.OutputFileOptions.Builder(photofile).build()
        imageCapture.takePicture(outputOption, ContextCompat.getMainExecutor(this),
            object:ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val saveUri= Uri.fromFile(photofile)
                    val ms="Photo Saved"
                    Toast.makeText(this@MainActivity,"$ms$saveUri", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==Constants.REQUES_CODE_PERMISSION)
        {
            if(allpermissiongranted())
            {
                startCamera()
            }
            else
            {
                Toast.makeText(this,"Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun startCamera() {
        val cameraProviderfuture=ProcessCameraProvider.getInstance(this)
        cameraProviderfuture.addListener({
            val CameraProvider:ProcessCameraProvider=cameraProviderfuture.get()
            val preview=Preview.Builder().build().also {
                it.setSurfaceProvider(
                    binding.camrex.surfaceProvider
                )
            }
            imageCapture=ImageCapture.Builder().build()
            val cameraSelector=CameraSelector.DEFAULT_BACK_CAMERA
            try {
                CameraProvider.unbindAll()
                CameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
            }catch (e:Exception)
            {

            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allpermissiongranted()=Constants.REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext,it)== PackageManager.PERMISSION_GRANTED
    }



}