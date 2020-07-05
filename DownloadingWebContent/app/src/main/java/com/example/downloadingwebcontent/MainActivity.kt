package com.example.downloadingwebcontent

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread
import java.io.InputStreamReader
import java.net.URL
import kotlin.Exception

class MainActivity : AppCompatActivity() {
    var changeImageView:ImageView? = null
    var changeButton:Button? = null

    private fun downloadImage(imageURL:String): Bitmap? {
        return try {
            val url = URL(imageURL)
            val connection = url.openConnection()
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e : Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun downloadWebsite(url:String):String{
        var result = ""
        try {
            val urlLocal = URL(url)
            val urlConnection = urlLocal.openConnection()
            val inputFile = urlConnection.getInputStream()
            val reader = InputStreamReader(inputFile)
            var data = reader.read()

            while (data != -1) {
                var current:Char = data.toChar()
                result += current
                data = reader.read()
            }

        } catch(e:Exception) {
            e.printStackTrace()
            result = "failed"
        }
        return result
    }

    private fun background(url:String) {
        doAsync{
            // val result = downloadWebsite(url)
            val image = downloadImage(url)
            uiThread {
                if (image != null) {
                    changeImageView!!.setImageBitmap(image)
                }

                // Log.i("Result", result)
                return@uiThread
            }
        }


    }
    // var changeImageView = findViewById<ImageView>(R.id.displayImageView)



    fun changeImage(view : View) {
        try {
            background("https://wegotthiscovered.com/wp-content/uploads/2019/08/tarzan-new.jpg")
            if (changeButton != null) {
                changeButton!!.isEnabled = false
            }
            // downloadWebsite("https://zappycode.com/")
            // downloadImage
        }
        catch (e : Exception) {
            Log.i("Error", e.printStackTrace().toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Check Internet permission
        changeImageView = findViewById<ImageView>(R.id.displayImageView)
        changeButton = findViewById<Button>(R.id.changeImageButton)
    }
}