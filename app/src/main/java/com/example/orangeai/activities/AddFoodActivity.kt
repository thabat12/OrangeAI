package com.example.orangeai.activities



import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.orangeai.R
import com.example.orangeai.models.FoodNutrients
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import kotlinx.android.synthetic.main.activity_add_food.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class AddFoodActivity: AppCompatActivity() {


    private var imageCapture: ImageCapture? = null
    open lateinit var imageAddress: Uri
    lateinit var mlFoodNutrients: FoodNutrients
    val API_KEY = "pq6g1TEWAUPdOLP1awmPpGfoTFJbpidQrRFhjSH1"



        private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        take_me_away.visibility = View.GONE
        button2.visibility = View.GONE



        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listener for take photo button
        camera_capture_button.setOnClickListener {

            takePhoto()
        }

        manual_search.setOnClickListener {
            intent = Intent(this@AddFoodActivity, FoodSearchActivity::class.java)
            startActivity(intent)
        }

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()


    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun takePhoto() {

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        val fileName : String = "/storage/emulated/0/Android/media/com.example.orangeai/OrangeAI/" +
                "${System.currentTimeMillis()}.jpg"
        // Create time-stamped output file to hold the image
        val photoFile = File(fileName)


//            File(
//            outputDirectory,
//            SimpleDateFormat(FILENAME_FORMAT, Locale.US
//            ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken


        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                @SuppressLint("SetTextI18n")
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)


                    val msg = "Photo capture succeeded: $savedUri"
                    val uriCompatible: String = (savedUri.toString()).substring(7)


                    //showOptions(mlFoodNutrients)


//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    createLocalModel(fileName)
                }
            })
    }



    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder()
                .build()

//            val imageAnalyzer = ImageAnalysis.Builder()
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
//                        Log.d(TAG, "Average luminosity: $luma")
//                    })
//                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {

        val EXTRA_ADD_FOODS = "extra_add_foods"

        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }


    // Tensorflow Preperation





    @SuppressLint("ShowToast", "SetTextI18n")
    fun createLocalModel(fileName: String) {

        val image: InputImage
        try {
            image = InputImage.fromFilePath(this, Uri.fromFile(File(fileName)))
        } catch (e: IOException) {
            e.printStackTrace()
            test.text = "does not work"
            return
        }

        // First let us access the model that is existing in the assets folder
        val localModel =
            LocalModel.Builder()
                .setAssetFilePath("tfhubfoods.tflite")
                .build()

        // Now we are telling this created model to:
        // A. Set the confidence threshold to 5%, meaning that this model will be 95% certain in its predictions
        // B. Return 5 predictions aka. Max result count
        val customImageLabelerOptions =
            CustomImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.1f)
                .setMaxResultCount(5)
                .build()
        // Before we set the properties of this image labeler, we must create this labeler first:
        val imageLabeler =
            ImageLabeling.getClient(customImageLabelerOptions)


        // Now pass in the image to process it into the created model
        imageLabeler.process(image)
            .addOnSuccessListener { labels ->


                // The image labeler will return these values on success:

                if (labels.size == 0) {
                    test.text = "oops. couldn't get that!"
                    return@addOnSuccessListener
                }
                for (label in labels) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index
                    test.text = "Is this a/ an " + text + "?"
                    take_me_away.visibility = View.VISIBLE
                    button2.visibility  =View.VISIBLE
                    take_me_away.setOnClickListener {
                        foodsTaskAPI(fileName, text).execute()

                    }
                    button2.setOnClickListener {
                        test.text = "Try again!"
                    }


                }

                //test.text = entireString

            }
            .addOnFailureListener { e ->
                //return@addOnFailureListener
            }



    }

    @SuppressLint("SetTextI18n")
//    fun showOptions(model: ArrayList<FoodNutrients>) {
//        test.text = "Is this " + model[0].foodName + "?"
//        take_me_away.visibility = View.VISIBLE
//        button2.visibility  =View.VISIBLE
//        take_me_away.setOnClickListener {
//            foodsTaskAPI().execute()
//        }
//    }

    inner class foodsTaskAPI(stringURI: String?, searchText: String) : AsyncTask<String, Void, String>() {
        val foodSearchText = searchText
        val uriString = stringURI
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */

        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                //https://api.nal.usda.gov/fdc/v1/foods/search?api_key=pq6g1TEWAUPdOLP1awmPpGfoTFJbpidQrRFhjSH1&query=Orange
                //    %20
                response = URL("https://api.nal.usda.gov/fdc/v1/foods/search?api_key=$API_KEY&query=$foodSearchText")
                    .readText(
                        Charsets.UTF_8
                    )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */

//                var foodNutrientsSave: ArrayList<FoodNutrients> = ArrayList()

                var stringTest = ""

                var foodNameL: String = ""
                var proteinNameL: String = ""
                var proteinValueL: Double = -1.0
                var proteinUnitsL: String = ""
                var lipidNameL: String = ""
                var lipidValueL: Double = -1.0
                var lipidUnitsL: String = ""
                var carbNameL: String = ""
                var carbValueL: Double = -1.0
                var carbUnitsL: String = ""
                var caloriesL: Int = -1
                val jsonObj = JSONObject(result)

                val foods = jsonObj.getJSONArray("foods").getJSONObject(0)
                val description = foods.getString("description")
                stringTest += description + "\n"
                foodNameL = description
                val lengthofChildren = foods.getJSONArray("foodNutrients").length()
                for ( j in 0 until lengthofChildren) {
                    val nutrientObj = foods.getJSONArray("foodNutrients").getJSONObject(j)
                    val nutrientId = nutrientObj.getInt("nutrientId")
                    if (nutrientId == 1004) {
                        // lipids
                        lipidNameL = nutrientObj.getString("nutrientName")
                        lipidValueL = nutrientObj.getDouble("value")
                        lipidUnitsL = nutrientObj.getString("unitName")
                    } else if (nutrientId == 1003) {
                        // protein
                        proteinNameL = nutrientObj.getString("nutrientName")
                        proteinValueL = nutrientObj.getDouble("value")
                        proteinUnitsL = nutrientObj.getString("unitName")
                    } else if (nutrientId == 1005) {
                        // carbs
                        carbNameL = nutrientObj.getString("nutrientName")
                        carbValueL= nutrientObj.getDouble("value")
                        carbUnitsL= nutrientObj.getString("unitName")
                    } else if (nutrientId == 1008) {
                        caloriesL = nutrientObj.getInt("value")

                    } else
                        continue


                }
                val addToArray = FoodNutrients(
                    foodNameL,
                    proteinNameL,
                    proteinValueL,
                    proteinUnitsL,
                    lipidNameL,
                    lipidValueL,
                    lipidUnitsL,
                    carbNameL,
                    carbValueL,
                    carbUnitsL,
                    caloriesL,
                    null,
                    uriString
                )
                Log.e("Check", addToArray.toString())


                val intent = Intent(this@AddFoodActivity, LogFoodActivity::class.java)
                intent.putExtra(EXTRA_ADD_FOODS, addToArray)
                startActivity(intent)

            } catch (e: Exception) {
                test.text = "This food does not exist on our database!"
            }

        }
    } // end of the inner class





}