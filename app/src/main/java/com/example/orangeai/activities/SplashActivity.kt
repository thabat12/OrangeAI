package com.example.orangeai.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.orangeai.R
import com.example.orangeai.database.ExerciseHistoryDatabaseHandler
import com.example.orangeai.database.MainDatabaseHandler
import com.projemanag.firebase.FirestoreClass
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : BaseActivity() {
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val currentTime = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
            .format(Date())


        val dbHandlerMain = MainDatabaseHandler(this)
        dbHandlerMain.initializeDatabase(currentTime)




        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        Handler(Looper.getMainLooper()).postDelayed({

            val currentUserID = FirestoreClass().getCurrentUserID()
            if (currentUserID.isNotEmpty()) {
                // Start the Main Activity
               startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                // Start the Intro Activity
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }


            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            finish() // Call this when your activity is done and should be closed.
        }, 2500)
    }
}