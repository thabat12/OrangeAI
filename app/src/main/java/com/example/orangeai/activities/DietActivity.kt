package com.example.orangeai.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orangeai.R
import com.example.orangeai.adapters.FoodImageAdapter
import com.example.orangeai.adapters.RecipeAdapter
import com.example.orangeai.database.DietDatabaseHandler
import com.example.orangeai.database.MainDatabaseHandler
import com.example.orangeai.models.FoodImages
import com.example.orangeai.models.Recipes
import com.example.orangeai.models.setRecipes
import kotlinx.android.synthetic.main.activity_diet.*
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.activity_exercise_list.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DietActivity : BaseActivity() {

    var list = setRecipes(this).createModels()
    var saveList =ArrayList<Recipes>()
    var proteinNum:Int=0
    private var carbsNum:Int=0
    var fatsNum:Int=0
    var calGain:Int=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)
        getData()
        diet_home.setOnClickListener {
            intent = Intent(this@DietActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        diet_move.setOnClickListener {
            intent = Intent(this@DietActivity, ExerciseActivity::class.java)
            startActivity(intent)
            finish()
        }
        diet_profile.setOnClickListener {
            intent = Intent(this@DietActivity, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        addFoodTab_diet.setOnClickListener {
            intent = Intent(this@DietActivity, AddFoodActivity::class.java)
            startActivity(intent)
            //finish()
        }

//        protein.text=proteinNum.toString()
//        fats.text=fatsNum.toString()
//        carbsCount.text=carbsNum.toString()
//        total_calories_gained.text=calGain.toString()

    }





    fun getData() {
        val dataGetter = setRecipes(this)
        val dataModelsList = dataGetter.createModels()
        setUpRecipesRecyclerView(dataModelsList)
        try {
            val dietGetter = DietDatabaseHandler(this)
            val anotherDataModelsList = dietGetter.getFoodData()
            val actualModelRecents = modelPreprocessing(anotherDataModelsList)
            setUpFoodImageRecyclerView(actualModelRecents)
        } catch (e: Exception) {
            rv_food_history_thing.visibility = View.GONE
        }

        // im just going to set all the readable data over here
        val dataMainGetter = MainDatabaseHandler(this)
        val goalsArray = dataMainGetter.getTodayData()

        progress_circular_diet.progress = ( (goalsArray.caloriesGained).toDouble() / (goalsArray.caloriesGainGoal).toDouble() * 100 ).toFloat()
        calories_consumed.text = goalsArray.caloriesGained.toString()
        calories_remaining.text = (goalsArray.caloriesGainGoal - goalsArray.caloriesGained).toString()
        // time to calculate the proteins fat and etc
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        // get the string of today's date
        val dateScanner: Scanner = Scanner(currentDate)
        val dateString = dateScanner.next()
        val anotherGetter = DietDatabaseHandler(this)

        val arrayOfInts = anotherGetter.getTodayMacroNutrientData(dateString)
        // proteins, carbs, lipids: order of this array

        Log.e("ArrayofInts", arrayOfInts)
        val arrayScanner = Scanner(arrayOfInts)
        val proteinAmount = arrayScanner.nextDouble().toInt()
        val carbAmount = arrayScanner.nextDouble().toInt()
        val lipidAmount = arrayScanner.nextDouble().toInt()

        Log.e("Dis one", "proteins: $proteinAmount carbs: $carbAmount lipids: $lipidAmount")

        progress_proteins.progress = ((proteinAmount / 112.0) * 100).toInt()
        progress_carbs.progress = ((carbAmount / 150.0) * 100).toInt()
        progress_lipids.progress = ((lipidAmount / 50.0) * 100).toInt()
        Log.e("arrayOfInts", arrayOfInts)
        // cool everything is set now


        tv_protein_amount.text = "$proteinAmount g"
        tv_carb_amount.text = "$carbAmount g"
        tv_lipid_amount.text = "$lipidAmount g"

    }
    fun modelPreprocessing(model: ArrayList<FoodImages>) : ArrayList<FoodImages> {
        val size = model.size
        if (size < 3) {
            return model
        } else
        {
            val modelRecents: ArrayList<FoodImages> = ArrayList()
            val indexLast = size - 1
            for (i in 0 until 3) {
                val addModel = model[indexLast - i]
                modelRecents.add(addModel)
            }
            return modelRecents
        }

    }

    private fun setUpRecipesRecyclerView(dataModelsList: ArrayList<Recipes>) {


        rv_for_recipes.layoutManager = LinearLayoutManager(this)
        rv_for_recipes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL ,false)
        rv_for_recipes.setHasFixedSize(true)

        val rAdapter = RecipeAdapter(this, dataModelsList)
        rv_for_recipes.adapter = rAdapter

        rAdapter.setOnClickListener(object : RecipeAdapter.OnClickListener {
            override fun onClick(position: Int, model: Recipes) {
//                save_selection.setVisibility(View.VISIBLE)
//                val newObject = Recipes(dataModelsList[position].id, dataModelsList[position].title, dataModelsList[position].description, dataModelsList[position].calories, dataModelsList[position].img)
//                saveList.add(newObject)

//                var goUrl=Intent(android.content.Intent.ACTION_VIEW)
//                goUrl.data= Uri.parse("https://developer.android.com/reference/android/app/Activity/")

                var goUrl = Intent(Intent.ACTION_VIEW,Uri.parse(dataModelsList[position].web))
                startActivity(goUrl)

            }
        })
    }

    private fun setUpFoodImageRecyclerView(dataModelsList: ArrayList<FoodImages>) {


        rv_food_history_thing.layoutManager = LinearLayoutManager(this)
        rv_food_history_thing.setHasFixedSize(true)

        val rAdapter = FoodImageAdapter(this, dataModelsList)
        rv_food_history_thing.adapter = rAdapter

        rAdapter.setOnClickListener(object : FoodImageAdapter.OnClickListener {
            override fun onClick(position: Int, model: FoodImages) {
            }
        })
    }
}