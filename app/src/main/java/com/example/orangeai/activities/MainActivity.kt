package com.example.orangeai.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide

import com.example.orangeai.R
import com.example.orangeai.background.SensorService
import com.example.orangeai.database.MainDatabaseHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.projemanag.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_exercise.*


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.home_move
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {

    val CITY: String = "delhi,in"
    val API: String = "8118ed6ee68db2debfaaa5a44c832918" // Use your own API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        home_move.setOnClickListener {
            intent = Intent(this@MainActivity, ExerciseActivity::class.java)
            startActivity(intent)
            finish()
        }
        home_diet.setOnClickListener {
            intent = Intent(this@MainActivity, DietActivity::class.java)
            startActivity(intent)
            finish()
        }
        home_profile.setOnClickListener {
            intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        addFoodTab_main.setOnClickListener {
            intent = Intent(this@MainActivity, AddFoodActivity::class.java)
            startActivity(intent)
            //finish()
        }


        weatherTask().execute()
        getDailyInfo()

        Intent(this, SensorService::class.java).also { intent ->
            startService(intent)
        }
    }

    fun getDailyInfo() {
        // cal burned, cal gained, water, diet award, exercise award
        val mainDBHandler = MainDatabaseHandler(this)
        val todayDetails = mainDBHandler.getTodayData()

        val calBCurrent = todayDetails.caloriesBurned
        val calBGoal = todayDetails.caloriesBurnGoal
        val calGCurrent = todayDetails.caloriesGained
        val calGGoal = todayDetails.caloriesGainGoal

        tv_calories_burned.text = "$calBCurrent /$calBGoal Calories Burned"
        tv_calories_gained.text = "$calGCurrent /$calGGoal Calories Gained"

        val calBProgress = ((calBCurrent.toDouble() / calBGoal.toDouble()) * 100).toInt()
        val calGProgress = ((calGCurrent.toDouble() / calGGoal.toDouble()) * 100).toInt()

        pb_calories_burn.progress = calBProgress
        pb_calories_gain.progress = calGProgress

        tv_program_name.text = todayDetails.program
        tv_program_day.text = "Program day ${todayDetails.currentDay.toString()} /30 "
    }


    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
//            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
//            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
//            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt*1000)
                )
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                /* Populating extracted data into our views */
//                findViewById<TextView>(R.id.address).text = address
//                findViewById<TextView>(R.id.updated_at).text =  updatedAtText
//                findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
//                findViewById<TextView>(R.id.temp).text = temp
//                findViewById<TextView>(R.id.temp_min).text = tempMin
//                findViewById<TextView>(R.id.temp_max).text = tempMax
//
//
//                /* Views populated, Hiding the loader, Showing the main design */
//                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
//                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE

            } catch (e: Exception) {
//                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
//                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }

        }
    }

}

