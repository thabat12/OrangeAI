package com.example.orangeai.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import com.example.orangeai.FirestoreClass
import com.example.orangeai.R
import com.example.orangeai.database.DietDatabaseHandler
import com.example.orangeai.database.ExerciseHistoryDatabaseHandler
import com.example.orangeai.database.MainDatabaseHandler
import com.example.orangeai.models.Today
import com.example.orangeai.models.User
import com.example.orangeai.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile_setup.*
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : BaseActivity() {

    var keepRunning = true
    lateinit var firestoreContents: User


    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        try{
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash)
            val sFireStore = FirebaseFirestore.getInstance()

            initializeAllDatabases()

            val currentTime = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                .format(Date())
            val dbHandlerMain = MainDatabaseHandler(this)

            sFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.e("Setting it all up!", document.toString())

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    firestoreContents = document.toObject(User::class.java)!!
                    dbHandlerMain.initializeDatabase(currentTime, firestoreContents)

                }
                .addOnFailureListener { e ->
                    // END
                    Log.e(
                        "oops!", "Error while getting loggedIn user details", e)
                }




            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            )
            Handler(Looper.getMainLooper()).postDelayed({



                val currentUserID = FirestoreClass().getCurrentUserID()
                if (currentUserID.isNotEmpty()) {
                    // Start the Main Activity
                    FirestoreClass().signInUser(this@SplashActivity)
                    //
                    //                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))

                } else {
                    // Start the Intro Activity
                    startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                }


                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                ) // Call this when your activity is done and should be closed.
            }, 2500)
        }catch (e: NullPointerException)
        {
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
        }


    }

    fun initializeAllDatabases() {
        val mainDB = MainDatabaseHandler(this)
        val dietDB = DietDatabaseHandler(this)
        val exerciseDB = ExerciseHistoryDatabaseHandler(this)
    }


    fun finishSetup() {
        startActivity(Intent(this@SplashActivity, ProfileSetupActivity::class.java))
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

    }
    fun goToMain() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }




}