package com.example.orangeai.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orangeai.R
import com.example.orangeai.adapters.FoodImageAdapter
import com.example.orangeai.adapters.RecipeAdapter
import com.example.orangeai.database.DietDatabaseHandler
import com.example.orangeai.models.FoodImages
import com.example.orangeai.models.Recipes
import com.example.orangeai.models.setRecipes
import kotlinx.android.synthetic.main.activity_diet.*
import kotlinx.android.synthetic.main.activity_exercise_list.*


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

        protein.text=proteinNum.toString()
        fats.text=fatsNum.toString()
        carbsCount.text=carbsNum.toString()
        total_calories_gained.text=calGain.toString()

    }





    fun getData() {
        val dataGetter = setRecipes(this)
        val dataModelsList = dataGetter.createModels()
        setUpRecipesRecyclerView(dataModelsList)

        val anotherGetter = DietDatabaseHandler(this)
        val anotherDataModelsList = anotherGetter.getFoodData()
        setUpFoodImageRecyclerView(anotherDataModelsList)
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
        rv_food_history_thing.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL ,true)
        rv_food_history_thing.setHasFixedSize(true)

        val rAdapter = FoodImageAdapter(this, dataModelsList)
        rv_food_history_thing.adapter = rAdapter

        rAdapter.setOnClickListener(object : FoodImageAdapter.OnClickListener {
            override fun onClick(position: Int, model: FoodImages) {
            }
        })
    }
}