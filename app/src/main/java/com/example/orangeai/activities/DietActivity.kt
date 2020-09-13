package com.example.orangeai.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.orangeai.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_diet.*
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.activity_main.*

class DietActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet)
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
}