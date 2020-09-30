package com.example.orangeai.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.orangeai.R
import com.example.orangeai.database.DietDatabaseHandler
import com.example.orangeai.database.MainDatabaseHandler
import com.example.orangeai.models.FoodNutrients
import kotlinx.android.synthetic.main.activity_log_food.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LogFoodActivity : AppCompatActivity() {

    lateinit var foodDetailsModel: FoodNutrients
    var calories: Int = 0
    var carbs: Double = 0.0
    var protein: Double = 0.0
    var lipids: Double = 0.0
    var imageUriText: String = ""
    var dateOfImage: String = ""
    lateinit var alteredModelSave : Array<FoodNutrients>


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_food)

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        dateOfImage = currentDate

        save_food_item.setOnClickListener {


            alteredModelSave = arrayOf(FoodNutrients(foodDetailsModel!!.foodName, "", protein, "", "", lipids, "", "", carbs, "", calories, dateOfImage, imageUriText))

            // Passed into the food history database
            val dbHandler = DietDatabaseHandler(this)
            val addDiet = dbHandler.addFoodToHistory(alteredModelSave)

            val anotherDBlol = MainDatabaseHandler(this)
            anotherDBlol.addCaloriesGainMain(calories)

            startActivity(Intent(this@LogFoodActivity, DietActivity::class.java))
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            finish()



        }

        if (intent.hasExtra(FoodSearchActivity.EXTRA_FOOD_DETAILS)) {
            foodDetailsModel =
                intent.getSerializableExtra(FoodSearchActivity.EXTRA_FOOD_DETAILS) as FoodNutrients
        } else if (intent.hasExtra(AddFoodActivity.EXTRA_ADD_FOODS)) {
            foodDetailsModel =
                intent.getSerializableExtra(AddFoodActivity.EXTRA_ADD_FOODS) as FoodNutrients
        }
        current_time.text = currentDate

        if (foodDetailsModel != null) {
            calories = foodDetailsModel!!.calories
            carbs = foodDetailsModel!!.carbValue
            lipids = foodDetailsModel!!.lipidValue
            protein = foodDetailsModel!!.proteinValue
            food_name_show.text = foodDetailsModel!!.foodName
            food_description.text = "Press save to log 1 serving automatically!"
            food_calories.text = foodDetailsModel!!.calories.toString() + " cal"
            food_protein.text = foodDetailsModel!!.proteinName + foodDetailsModel!!.proteinValue.toString()
            food_carbs.text = foodDetailsModel!!.carbName + foodDetailsModel!!.carbValue.toString()
            food_lipids.text = foodDetailsModel!!.lipidName + foodDetailsModel!!.lipidValue.toString()
            if (foodDetailsModel!!.imageURI != null) {
                try {
                    imageUriText = foodDetailsModel!!.imageURI.toString()
                    iv_show_food.setImageURI(Uri.fromFile(File(foodDetailsModel!!.imageURI)))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            alteredModelSave = arrayOf(foodDetailsModel)

            food_carbs.text = "Carbs: " + "%.2f".format(carbs)
            food_protein.text = "Protein: " + "%.2f".format(protein)
            food_lipids.text = "Lipids: " + "%.2f".format(lipids)

        }


        text_changed.addTextChangedListener( object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val content = text_changed.text.toString()
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    calories = text_changed.text.toString().toInt() * foodDetailsModel!!.calories
                    carbs = text_changed.text.toString().toDouble() * foodDetailsModel!!.carbValue
                    lipids = text_changed.text.toString().toDouble() * foodDetailsModel!!.lipidValue
                    protein = text_changed.text.toString().toDouble() * foodDetailsModel!!.proteinValue
                    food_calories.text = "$calories cal"
                    food_carbs.text = "Carbs: " + "%.2f".format(carbs)
                    food_protein.text = "Protein: " + "%.2f".format(protein)
                    food_lipids.text = "Lipids: " + "%.2f".format(lipids)
                } catch(e: Exception) {
                    food_calories.text = "0"
                    food_carbs.text = "Carbs: 0"
                    food_protein.text = "Protein: 0"
                    food_lipids.text = "Lipids: 0"
                }


            }
        })

    }
}