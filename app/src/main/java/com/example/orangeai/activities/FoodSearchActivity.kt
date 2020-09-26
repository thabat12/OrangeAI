package com.example.orangeai.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orangeai.R
import com.example.orangeai.adapters.SearchFoodsAdapter
import com.example.orangeai.models.ActivityPrograms
import com.example.orangeai.models.FoodNutrients
import kotlinx.android.synthetic.main.activity_food_search.*
import org.json.JSONObject
import java.net.URL
import kotlin.collections.ArrayList

class FoodSearchActivity : AppCompatActivity() {
    companion object {
        var EXTRA_FOOD_DETAILS = "extra_food_details"
    }

    val API_KEY = "pq6g1TEWAUPdOLP1awmPpGfoTFJbpidQrRFhjSH1"
    val ANOTHER_API_KEY = "9ugJbIYN9Z99dH8lu87GWPvqp9Ucaa92IVas1exq"
    var foodSearchText = ""
    var passInList: ArrayList<FoodNutrients> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_search)

        button.setOnClickListener {
            foodSearchText = editTextTextPersonName.text.toString()
            foodsTask().execute()
            setupFoodSearchRecyclerView(passInList)
        }




    }


    inner class foodsTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */

        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                //https://api.nal.usda.gov/fdc/v1/foods/search?api_key=pq6g1TEWAUPdOLP1awmPpGfoTFJbpidQrRFhjSH1&query=Orange
                // https://api.nal.usda.gov/fdc/v1/foods/search?api_key=9ugJbIYN9Z99dH8lu87GWPvqp9Ucaa92IVas1exq&query=Orange
                //    %20
                response = URL("https://api.nal.usda.gov/fdc/v1/foods/search?api_key=$API_KEY&query=$foodSearchText")
                    .readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */

                var foodNutrientsSave: ArrayList<FoodNutrients> = ArrayList()

                var stringTest = ""

                var foodNameL: String = ""
                var proteinNameL: String = ""
                var proteinValueL: Double = -1.0
                var proteinUnitsL: String = ""
                var lipidNameL: String = ""
                var lipidValueL: Double = -1.0
                var lipidUnitsL: String = ""
                var carbNameL: String = ""
                var carbValueL: Double = -1.0
                var carbUnitsL: String = ""
                var caloriesL: Int = -1
                val jsonObj = JSONObject(result)
                var lengthofParent = jsonObj.getJSONArray("foods").length()
                if (lengthofParent > 10) {
                    lengthofParent = 10
                    Log.e("lengthofparent", lengthofParent.toString())
                }
                for ( i in 0 until lengthofParent) {
                    val foods = jsonObj.getJSONArray("foods").getJSONObject(i)
                    val description = foods.getString("description")
                    stringTest += description + "\n"
                    foodNameL = description
                    val lengthofChildren = foods.getJSONArray("foodNutrients").length()
                    for ( j in 0 until lengthofChildren) {
                        val nutrientObj = foods.getJSONArray("foodNutrients").getJSONObject(j)
                        val nutrientId = nutrientObj.getInt("nutrientId")
                        if (nutrientId == 1004) {
                            // lipids
                            lipidNameL = nutrientObj.getString("nutrientName")
                            lipidValueL = nutrientObj.getDouble("value")
                            lipidUnitsL = nutrientObj.getString("unitName")
                        } else if (nutrientId == 1003) {
                            // protein
                             proteinNameL = nutrientObj.getString("nutrientName")
                             proteinValueL = nutrientObj.getDouble("value")
                             proteinUnitsL = nutrientObj.getString("unitName")
                        } else if (nutrientId == 1005) {
                            // carbs
                             carbNameL = nutrientObj.getString("nutrientName")
                             carbValueL= nutrientObj.getDouble("value")
                             carbUnitsL= nutrientObj.getString("unitName")
                        } else if (nutrientId == 1008) {
                            caloriesL = nutrientObj.getInt("value")

                        } else
                            continue


                    }
                    val addToArray = FoodNutrients(foodNameL, proteinNameL, proteinValueL, proteinUnitsL,
                        lipidNameL, lipidValueL, lipidUnitsL, carbNameL, carbValueL, carbUnitsL, caloriesL, null,null)
                    foodNutrientsSave.add(addToArray)
                }

                passInList = foodNutrientsSave
                setupFoodSearchRecyclerView(passInList)

                Log.e("all the things", "\n" + foodNutrientsSave.toString())
                Log.e("lawl", "\n" + stringTest)
            } catch (e: Exception) {
            }

        }
    } // end of the inner class

    fun setupFoodSearchRecyclerView(foodDetailsList: java.util.ArrayList<FoodNutrients>) {
        rv_search_foods.layoutManager = LinearLayoutManager(this)
        rv_search_foods.setHasFixedSize(true)

        val searchFoodsAdapter = SearchFoodsAdapter(this, foodDetailsList)
        rv_search_foods.adapter = searchFoodsAdapter

        searchFoodsAdapter.setOnClickListener(object :
            SearchFoodsAdapter.OnClickListener {
            override fun onClick(position: Int, model: FoodNutrients) {
                val logFood = Intent(this@FoodSearchActivity, LogFoodActivity::class.java)
                logFood.putExtra(EXTRA_FOOD_DETAILS, model)
                startActivity(logFood)

            }
        })
    }






}