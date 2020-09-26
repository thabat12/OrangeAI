package com.example.orangeai.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.orangeai.R
import com.example.orangeai.models.User
import com.example.orangeai.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.projemanag.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_diet.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.util.*

class ProfileActivity : BaseActivity() {

    companion object {
        //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
        private const val READ_STORAGE_PERMISSION_CODE = 1

        // TODO (Step 6: Add a constant for image selection from phone storage)
        // START
        private const val PICK_IMAGE_REQUEST_CODE = 2
        // END
    }

    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageURL: String = ""
    private lateinit var mUserDetails: User



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        FirestoreClass().loadUserData(this@ProfileActivity)
        profile_home.setOnClickListener {
            intent = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        profile_move.setOnClickListener {
            intent = Intent(this@ProfileActivity, ExerciseActivity::class.java)
            startActivity(intent)
            finish()
        }
        profile_diet.setOnClickListener {
            intent = Intent(this@ProfileActivity, DietActivity::class.java)
            startActivity(intent)
            finish()
        }
        addFoodTab_profile.setOnClickListener {
            intent = Intent(this@ProfileActivity, AddFoodActivity::class.java)
            startActivity(intent)
            //finish()
        }
        btn_change_profile.setOnClickListener {
            startActivity(Intent(this, ProfileSetupActivity::class.java))
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        iv_profile_pic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                // TODO (Step 8: Call the image chooser function.)
                // START
                showImageChooser()
                // END
            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        log_out_profile.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            intent = Intent(this, IntroActivity::class.java)
            startActivity(intent)
        }
        val TAG = javaClass.simpleName
        pFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(TAG, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val userProfile = document.toObject(User::class.java)!!
            }
            .addOnFailureListener { e ->
                // END
                Log.e(
                    TAG, "Error while getting loggedIn user details", e)
            }


    }

    fun setUserDataInUI(user: User) {

        // TODO (Step 7: Initialize the user details variable)
        // START
        // Initialize the user details variable
        mUserDetails = user
        // END

        Glide
            .with(this@ProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.add_shape_gradient_background_circle)
            .into(iv_profile_pic)

        tv_profile_name.setText(user.name)
        tv_profile_email.setText(user.email)
        //tv_profile_weight.setText(user.weight)
        var BMI = calculateBMI(user.weight, user.height)
        //tv_profile_bmi.setText(BMI)
         lateinit var programName: String
        if (user.program == 1) {
            programName = "Lose Weight"
        } else if (user.program == 2) {
            programName = "Become Fit"
        } else
            programName = "Build Muscle"

        lateinit var heightString: String
        val feet = user.height / 12
        val inches = user.height % 12
        heightString = "$feet'$inches\""

        tv_my_settings.text = "Age: ${user.age}\nGender: ${user.gender}\nWeight: ${user.weight}\nHeight: ${heightString}\nProgram: $programName"

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data

            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this@ProfileActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_baseline_account_circle_24) // A default place holder
                    .into(iv_profile_pic) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        uploadUserImage()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO (Step 9: Call the image chooser function.)
                // START
                showImageChooser()
                // END
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun showImageChooser() {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
    private fun getFileExtension(uri: Uri?): String? {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun uploadUserImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                    mSelectedImageFileUri
                )
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            // Call a function to update user details in the database.
                            updateUserProfileData()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this@ProfileActivity,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
        }
    }

    private fun updateUserProfileData() {

        val userHashMap = HashMap<String, Any>()

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }



        // Update the data in the database.
        FirestoreClass().updateUserProfileData(this@ProfileActivity, userHashMap)
    }
    fun profileUpdateSuccess() {

        hideProgressDialog()


    }




    private fun signOutFirebase(){
        FirebaseAuth.getInstance().signOut()
        finish()
        intent = Intent(this@ProfileActivity, IntroActivity::class.java)
        startActivity(intent)
    }

}