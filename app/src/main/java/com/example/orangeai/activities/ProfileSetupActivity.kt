package com.example.orangeai.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.orangeai.R
import com.example.orangeai.database.MainDatabaseHandler
import com.example.orangeai.models.User
import com.example.orangeai.utils.Constants
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_profile_setup.*
import kotlinx.android.synthetic.main.activity_profile_setup.profile_setup_button
import kotlinx.android.synthetic.main.test_file.*


class ProfileSetupActivity : BaseActivity() {



    val TAG = javaClass.simpleName

    // These are for the program selection things
    var loseWeight = false
    var becomeFit = false
    var buildMuscle = false


    var male = false
    var female = false

    var program = 0
    var genderP = 0



    lateinit var userInfoProfileSetup: User

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tv_before.animate().alpha(0f).setDuration(1200)
        tv_after.animate().alpha(1f).setDuration(5200)


        pFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(TAG, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                userInfoProfileSetup = document.toObject(User::class.java)!!
                name_profile_setup.text = "Hello " + userInfoProfileSetup.name + "!"
            }
            .addOnFailureListener { e ->
                // END
                Log.e(
                    TAG, "Error while getting loggedIn user details", e)
            }



        profile_setup_button.setOnClickListener {

            // check to see what programs are selected
            // lose weight, build muscle, become fit
            if (loseWeight == true) {
                program = 1
            } else if (buildMuscle == true) {
                program = 2
            } else if (becomeFit == true) {
                program = 3
            } else {
                showErrorSnackBar("Please select a program")
                return@setOnClickListener
            }
            if (male) {
                genderP = 1
            } else
                genderP = 2


            val age = age_setup.text.toString().toInt()
            val heightFeet = height_feet_setup.text.toString().toInt()
            val heightInches = height_inches_setup.text.toString().toInt()
            val weight = weight_setup.text.toString().toInt()
            // The goal page will be a seperate method using on click listeners for imageViews


            var BMR: Double = 0.0
            if (female) {
                BMR = 655 + (4.35 * weight) + (4.7 * ((heightFeet * 12) + heightInches)) - (4.7 * age)
            } else {
                BMR = 66 + (6.23 * weight) + (12.7 * ((heightFeet * 12) + heightInches)) - (6.8 * age)
            }

            val caloriesChangeAvg = (BMR * 1.55).toInt()

            dataPreprocessing(age, heightFeet, heightInches, weight, genderP, program, caloriesChangeAvg)

            val databaseMainHandler = MainDatabaseHandler(this)
            databaseMainHandler.updateGoals(BMR, program, weight)

            this.finish()
            intent = Intent(this@ProfileSetupActivity, IntroActivity2::class.java)
            startActivity(intent)
        }
        program_lose_weight.setOnClickListener {
            loseWeight = ! loseWeight
            if (loseWeight) {
                if (becomeFit == false && buildMuscle == false) {
                    program_lose_weight.setBackgroundResource(R.drawable.program_buttons_selected)
                }
            } else {
                program_lose_weight.setBackgroundResource(R.drawable.program_buttons_unselected)
                loseWeight = false
                becomeFit = false
                buildMuscle = false
            }
        }

        program_become_fit.setOnClickListener {
            becomeFit = ! becomeFit
            if (becomeFit) {
                if (loseWeight == false && buildMuscle == false) {
                    program_become_fit.setBackgroundResource(R.drawable.program_buttons_selected)
                }
            } else {
                program_become_fit.setBackgroundResource(R.drawable.program_buttons_unselected)
                loseWeight = false
                becomeFit = false
                buildMuscle = false
            }
        }

        program_build_muscle.setOnClickListener {
            buildMuscle = ! buildMuscle
            if (buildMuscle) {
                if (becomeFit == false && loseWeight == false) {
                    program_build_muscle.setBackgroundResource(R.drawable.program_buttons_selected)
                }
            } else {
                program_build_muscle.setBackgroundResource(R.drawable.program_buttons_unselected)
                loseWeight = false
                becomeFit = false
                buildMuscle = false
            }
        }

        male_profile_setup.setOnClickListener {
            male_profile_setup.setColorFilter(ContextCompat.getColor(this, R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY)
            female_profile_setup.setColorFilter(ContextCompat.getColor(this, R.color.lightGray))
            male = true
            female = false
        }
        female_profile_setup.setOnClickListener {
            female_profile_setup.setColorFilter(ContextCompat.getColor(this, R.color.pink), android.graphics.PorterDuff.Mode.MULTIPLY)
            male_profile_setup.setColorFilter(ContextCompat.getColor(this, R.color.lightGray))
            female = true
            male = false
        }




    }

    fun setName(loggedInUser: User) {
        name_profile_setup.text = loggedInUser.name
    }




    private fun dataPreprocessing(age: Int, heightFeet: Int, heightInches: Int, weight:Int, gender: Int, program: Int, goal: Int) {
        if (age >= 110) {
            showErrorSnackBar("Invalid age! Please try again.")
        }

        val heightInInches = (heightFeet * 12) + heightInches

        if (heightInInches > 107.1) {
            showErrorSnackBar("Woah! You are too tall for our systems!")
        }
        userInfoProfileSetup.age = age
        userInfoProfileSetup.height = heightInInches
        userInfoProfileSetup.goal = goal
        userInfoProfileSetup.weight = weight
        userInfoProfileSetup.gender = gender
        userInfoProfileSetup.program = program
        addUserDetails(userInfoProfileSetup)

    }



    fun addUserDetails(userInfo: User){
        pFireStore.collection(Constants.USERS)
            // Document ID for users fields. Here the document it is the User ID.
            .document(getCurrentUserID())
            // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                Log.e(
                    TAG,
                    "Error writing document"
                )
            }
            .addOnFailureListener { e ->
                Log.e(
                    TAG,
                    "Error writing document"
                )
            }


    }
}