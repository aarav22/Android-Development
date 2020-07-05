package com.example.weather

import android.content.Context
import android.hardware.input.InputManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {
    lateinit var weatherButton:Button
    lateinit var inputEditText: EditText
    lateinit var displayTextView: TextView
    private fun downloadWeather(weatherUrl:String):String {
        var result = ""
        try {
            val urlLocal = URL(weatherUrl)
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

    public fun getWeather(view: View) {
        try {
            var cityName:String = URLEncoder.encode(inputEditText.text.toString(), "UTF-8")
            background("https://api.openweathermap.org/data/2.5/weather?q=$cityName&APPID=88335b0bd3118a0d58afb4649488e1f9")
            weatherButton.isEnabled = false
            // To push down keyboard
            var mgr:InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }
        catch (e : Exception) {
            Log.i("Error", e.printStackTrace().toString())
        }
    }

    private fun background(weatherUrl:String) {
        doAsync {
            val result = downloadWeather(weatherUrl)
            uiThread {
                try {
                    var jsonObject = JSONObject(result)
                    val weatherInfo = jsonObject.getString("weather")
                    var arr:JSONArray = JSONArray(weatherInfo)
                    var message: String
                    for (i in 0 until arr.length()) {
                        var jsonPart:JSONObject = arr.getJSONObject(i)
                        message = jsonPart.getString("main") + "\n" + jsonPart.getString("description")
                        displayTextView.text = message
                        Log.i("Description", jsonPart.getString("description"))
                    }

                } catch (e:Exception) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Could not find the info :(", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        weatherButton = findViewById(R.id.weatherButton)
        inputEditText = findViewById(R.id.inputEditText)
        displayTextView = findViewById(R.id.displayTextView)
    }
}