package com.example.orangeai.activities

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.orangeai.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        // Apply activity transition
        val animDrawable = root_layout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(4000)
        animDrawable.start()

        btnSignUpIntro.setOnClickListener {
            intent = Intent(this, SignUpActivity:: class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        btnSignInIntro.setOnClickListener {
            intent = Intent(this, SignInActivity:: class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }
}