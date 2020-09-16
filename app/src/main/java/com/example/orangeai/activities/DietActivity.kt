package com.example.orangeai.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orangeai.R
import com.example.orangeai.adapters.RecipeAdapter
import com.example.orangeai.models.Recipes
import com.example.orangeai.models.setRecipes
import kotlinx.android.synthetic.main.activity_diet.*
import kotlinx.android.synthetic.main.activity_exercise_list.*

class DietActivity : BaseActivity() {

    var list = setRecipes(this).createModels()
    var saveList =ArrayList<Recipes>()
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


    }





    fun getData() {
        val dataGetter = setRecipes(this)
        val dataModelsList = dataGetter.createModels()
        setUpRecipesRecyclerView(dataModelsList)
    }

    private fun setUpRecipesRecyclerView(dataModelsList: ArrayList<Recipes>) {


        rv_for_recipes.layoutManager = LinearLayoutManager(this)
        rv_for_recipes.setHasFixedSize(true)

        val rAdapter = RecipeAdapter(this, dataModelsList)
        rv_for_recipes.adapter = rAdapter

        rAdapter.setOnClickListener(object : RecipeAdapter.OnClickListener {
            override fun onClick(position: Int, model: Recipes) {
                save_selection.setVisibility(View.VISIBLE)
                val newObject = Recipes(dataModelsList[position].id, dataModelsList[position].title, dataModelsList[position].description, dataModelsList[position].calories, dataModelsList[position].img)
                saveList.add(newObject)
            }
        })
    }
}