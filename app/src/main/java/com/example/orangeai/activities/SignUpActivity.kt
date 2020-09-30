package com.example.orangeai.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.orangeai.FirestoreClass
import com.example.orangeai.R
import com.example.orangeai.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setupActionBar()
        btn_sign_up.setOnClickListener{
            registerUser()
        }
        }
    fun userRegisteredSuccess() {

        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()



        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        finish()
    }
    fun setupActionBar(){
        setSupportActionBar(toolbar_sign_up_activity)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun registerUser(){
        val name: String = et_name_sign_up.text.toString().trim { it <= ' ' }
        val email: String = et_email_sign_up.text.toString().trim { it <= ' ' }
        val password: String = et_password_sign_up.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {

            if (validateForm(name, email, password)) {
                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        OnCompleteListener<AuthResult> { task ->


                            // If the registration is successfully done
                            if (task.isSuccessful) {

                                // Firebase registered user
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                // Registered Email
                                val registeredEmail = firebaseUser.email!!
                                val user = User(firebaseUser.uid, name, registeredEmail)
                                FirestoreClass().registerUser(this@SignUpActivity, user)
                            } else {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Registration failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
            }
        }
    }
    // go to account set up
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

    }
