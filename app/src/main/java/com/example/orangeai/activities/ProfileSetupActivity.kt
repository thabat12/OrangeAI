package com.example.orangeai.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import com.example.orangeai.R
import com.example.orangeai.models.User
import com.example.orangeai.utils.Constants
import com.google.firebase.firestore.SetOptions
import com.projemanag.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_profile_setup.*


class ProfileSetupActivity : BaseActivity() {
    val TAG = javaClass.simpleName



    lateinit var userInfoProfileSetup: User

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
            }
            .addOnFailureListener { e ->
                // END
                Log.e(
                    TAG, "Error while getting loggedIn user details", e)
            }

        profile_setup_button.setOnClickListener {
            val age = age_setup.text.toString().toInt()
            val heightFeet = height_feet_setup.text.toString().toInt()
            val heightInches = height_inches_setup.text.toString().toInt()
            val weight = weight_setup.text.toString().toInt()
            // The goal page will be a seperate method using on click listeners for imageViews
            dataPreprocessing(age, heightFeet, heightInches, weight)
            this.finish()
            intent = Intent(this@ProfileSetupActivity, IntroActivity2::class.java)
            startActivity(intent)



        }



    }

    private fun dataPreprocessing(age: Int, heightFeet: Int, heightInches: Int, weight:Int) {
        if (age >= 110) {
            showErrorSnackBar("Invalid age! Please try again.")
        }

        val heightInInches = (heightFeet * 12) + heightInches

        if (heightInInches > 107.1) {
            showErrorSnackBar("Woah! You are too tall for our systems!")
        }
        userInfoProfileSetup.age = age
        userInfoProfileSetup.height = heightInInches
        userInfoProfileSetup.weight = weight
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