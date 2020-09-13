package com.example.orangeai.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.orangeai.R
import com.example.orangeai.adapters.HappyPlacesAdapter
import com.example.orangeai.database.ExerciseDatabaseHandler
import com.example.orangeai.models.ActivityPrograms
import com.example.orangeai.models.setActivityPrograms
import kotlinx.android.synthetic.main.activity_exercise_list.*

open class ExerciseListActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    var list = setActivityPrograms(this).createModels()
    var saveList =ArrayList<ActivityPrograms>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_list)
        getData()
        save_selection.setOnClickListener {

            val dbHandler = ExerciseDatabaseHandler(this)
            val addExercises = dbHandler.addExerciseToList(saveList)
            if(addExercises > 0){
                setResult(Activity.RESULT_OK)
                finish()
            }

        }




    }

    override fun onBackPressed() {
        super.onBackPressed()
        intent = Intent(this, ExerciseActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun getData() {
        val dataGetter = setActivityPrograms(this)
        val dataModelsList = dataGetter.createModels()
        setupHappyPlacesRecyclerView(dataModelsList)
    }
    private fun setupHappyPlacesRecyclerView(dataModelsList: ArrayList<ActivityPrograms>) {


        rv_move_exercises.layoutManager = LinearLayoutManager(this)
        rv_move_exercises.setHasFixedSize(true)

        val exerciseAdapter = HappyPlacesAdapter(this, dataModelsList)
        rv_move_exercises.adapter = exerciseAdapter

        exerciseAdapter.setOnClickListener(object : HappyPlacesAdapter.OnClickListener {
            override fun onClick(position: Int, model: ActivityPrograms) {
                save_selection.setVisibility(View.VISIBLE)
                val newObject = ActivityPrograms(list[position].id, list[position].tag, list[position].title, list[position].image, list[position].calPerMinL, list[position].calPerMinM, list[position].calPerMinH)
                saveList.add(newObject)
            }
        })
    }




}