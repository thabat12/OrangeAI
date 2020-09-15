package com.example.orangeai.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.airbnb.lottie.animation.content.Content
import com.example.orangeai.models.FoodNutrients
import com.example.orangeai.models.Today
import com.example.orangeai.models.User
import com.example.orangeai.utils.Constants.DBVERSION
import com.projemanag.firebase.FirestoreClass
import java.util.*

class MainDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {

        // THIS IS THE SAME DATABASE VERSION AS THE EXERCISE DATABASE HANDLER. BOTH VERSIONS MUST BE SAME

        private const val DATABASE_VERSION = DBVERSION // This DATABASE Version
        private const val DATABASE_NAME = "OrangeAILogs.db" // Name of the DATABASE




        // Diet History Activity
        private const val TABLE_DIET_HISTORY = "activity_diet" // Table Name
        private const val COLUMN_ID_DIET = "_id" // Column Id
        private const val FOOD_NAME = "food_name"
        private const val FOOD_CALORIES = "food_calories"
        private const val FOOD_CARBS = "food_carbs"
        private const val FOOD_PROTEINS = "food_proteins"
        private const val FOOD_LIPIDS = "food_lipids"
        private const val FOOD_IMAGE_URI = "food_image_uri"
        private const val FOOD_DATE = "food_date"

        // Main Database
        private const val TABLE_MAIN_DATABASE = "main_database"
        private const val COLUMN_ID_MAIN = "_id"
        private const val PROGRAM = "program_name"
        private const val CALORIES_BURNED = "calories_burned"
        private const val CALORIES_BURNED_GOAL = "calories_burned_goal"
        private const val CALORIES_GAINED = "calories_gained"
        private const val CALORIES_GAINED_GOAL = "calories_gained_goal"
        private const val WATER_INTAKE = "water_intake"
        private const val STEPS_TAKEN = "steps_taken"
        private const val EXERCISE_AWARD = "exercise_award"
        private const val DIET_AWARD = "diet_award"
        private const val DATE = "date"

        // Exercise Type Activity
        private const val TABLE_ACTIVE_EXERCISES = "active_exercises" // Table Name
        private const val COLUMN_ID_EXERCISES = "_id" // Column Id
        private const val EXERCISE_TAG = "exercise_tag"
        private const val EXERCISE_TITLE = "exercise_title" // Column for Completed Date
        private const val EXERCISE_IMAGE = "exercise_image"
        private const val CAL_PER_MIN_LIGHT = "cal_per_min_light"
        private const val CAL_PER_MIN_MEDIUM = "cal_per_min_medium"
        private const val CAL_PER_MIN_HEAVY = "cal_per_min_heavy"
        private const val IS_SELECTED = "is_selected"

        // Exercise History Activity
        private const val TABLE_EXERCISE_HISTORY = "exercise_history" // Table Name
        private const val COLUMN_ID_EXERCISE = "_id" // Column Id
        private const val EXERCISE_NAME = "food_name"
        private const val CALORIES_BURNED_H = "calories_burned"
        private const val EXERCISE_DURATION = "exercise_duration"
        private const val TIME_OF_ACTIVITY = "time_of_activity"






    }

    override fun onCreate(db: SQLiteDatabase?) {



        val CREATE_MAIN_TABLE = ("CREATE TABLE " + TABLE_MAIN_DATABASE + "("
                + COLUMN_ID_MAIN + " INTEGER PRIMARY KEY,"
                + PROGRAM + " TEXT,"
                + STEPS_TAKEN + " INTEGER,"
                + CALORIES_BURNED + " INTEGER,"
                + CALORIES_BURNED_GOAL + " INTEGER,"
                + CALORIES_GAINED + " INTEGER,"
                + CALORIES_GAINED_GOAL + " INTEGER,"
                + WATER_INTAKE + " INTEGER,"
                + EXERCISE_AWARD + " INTEGER,"
                + DIET_AWARD + " INTEGER,"
                + DATE + " TEXT)")

        db?.execSQL(CREATE_MAIN_TABLE)




//        val CREATE_DIET_TABLE = ("CREATE TABLE " + TABLE_DIET_HISTORY + "("
//                + COLUMN_ID_DIET + " INTEGER PRIMARY KEY,"
//                + FOOD_NAME + " TEXT,"
//                + FOOD_CALORIES + " INTEGER,"
//                + FOOD_CARBS + " REAL,"
//                + FOOD_PROTEINS + " REAL,"
//                + FOOD_LIPIDS + " REAL,"
//                + FOOD_IMAGE_URI + " TEXT,"
//                + FOOD_DATE + " TEXT)")
//
//        db?.execSQL(CREATE_DIET_TABLE)
//
//
//
//        val CREATE_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_ACTIVE_EXERCISES + "("
//                + COLUMN_ID_EXERCISES + " INTEGER PRIMARY KEY,"
//                + EXERCISE_TAG + " INTEGER,"
//                + EXERCISE_TITLE + " TEXT,"
//                + EXERCISE_IMAGE + " TEXT,"
//                + CAL_PER_MIN_LIGHT + " REAL,"
//                + CAL_PER_MIN_MEDIUM + " REAL,"
//                + CAL_PER_MIN_HEAVY + " REAL)")
//
//
//        // Create History Table Query.
//        db?.execSQL(CREATE_EXERCISE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " +  TABLE_MAIN_DATABASE) // It drops the existing history table
        onCreate(db)
    }

    fun initializeDatabase(date: String, user: User) {

        Log.e("Initialize", "works")
        // Get the details age/ weight/ height and convert to BMR
        Log.e("the string one", user.toString())

        // TODO: make initializing the database take into account details in Firebase as well
        var calBGoal = 0
        var calGGoal = 0
        var programName = ""
        if (user.program == 1) {
            calBGoal = (user.goal * 1.02).toInt()
            calGGoal = (user.goal * 0.96).toInt()
            programName = "Lose Weight"
        } else if (user.program == 2) {
            calBGoal = (user.goal).toInt()
            calGGoal = (user.goal).toInt()
            programName = "Become Fit"
        } else {
            calBGoal = (user.goal + 1.02).toInt()
            calGGoal = (user.goal * 1.08).toInt()
            programName = "Build Muscle"
        }




        // Prepare Database
        val db = this.readableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_MAIN_DATABASE" // Database select query
        var addNewEntry = true

        try { // Go through to see if new values need to be added
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val existingDate = cursor.getString(cursor.getColumnIndex(DATE))
                    val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_MAIN))
                    if (existingDate == date) {
                        addNewEntry = false
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }
        if (addNewEntry) {
            val contentValues: ContentValues
            contentValues = ContentValues()
            // contentValues.put(COLUMN_ID, model[i].id)
            contentValues.put(PROGRAM, programName)
            contentValues.put(STEPS_TAKEN, 0)
            contentValues.put(CALORIES_BURNED, 0)
            contentValues.put(CALORIES_BURNED_GOAL, calBGoal)
            contentValues.put(CALORIES_GAINED, 0)
            contentValues.put(CALORIES_GAINED_GOAL, calGGoal)
            contentValues.put(WATER_INTAKE, 0)
            contentValues.put(EXERCISE_AWARD, 0)
            contentValues.put(DIET_AWARD, 0)
            contentValues.put(DATE, date)

            db.insert(TABLE_MAIN_DATABASE, null, contentValues)
            db.close()
        }
    }

    fun getTodayData() : Today {
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_MAIN_DATABASE" // Database select query

        val cursor: Cursor = db.rawQuery(selectQuery, null)
        cursor.moveToLast()

        val index = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_MAIN))
        val programName = cursor.getString(cursor.getColumnIndex(PROGRAM))
        val stepsTaken = cursor.getString(cursor.getColumnIndex(STEPS_TAKEN))
        val calG = cursor.getInt(cursor.getColumnIndex(CALORIES_GAINED))
        val calGG = cursor.getInt(cursor.getColumnIndex(CALORIES_GAINED_GOAL))
        val calB = cursor.getInt(cursor.getColumnIndex(CALORIES_BURNED))
        val calBG = cursor.getInt(cursor.getColumnIndex(CALORIES_BURNED_GOAL))
        val water = cursor.getInt(cursor.getColumnIndex(WATER_INTAKE))
        val awardE = cursor.getInt(cursor.getColumnIndex(EXERCISE_AWARD))
        val awardD = cursor.getInt(cursor.getColumnIndex(DIET_AWARD))
        val date = cursor.getString(cursor.getColumnIndex(DATE))
        val currentDay = cursor.position + 1


        val todayModel: Today = Today(index, currentDay, programName, stepsTaken, calB, calBG, calG,
            calGG, water, awardE, awardD, date)

        return todayModel


    }


    fun addStepsTaken(stepsTaken: Int)  {
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_MAIN_DATABASE" // Database select query
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        cursor.moveToLast()



        val totalSteps = stepsTaken


        val contentValues = ContentValues()
        val index = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_MAIN))
        contentValues.put(STEPS_TAKEN, totalSteps)


        db.update(TABLE_MAIN_DATABASE, contentValues, COLUMN_ID_MAIN + "=" + index, null)
        db.close()

    }

    @SuppressLint("Recycle")
    fun addCaloriesGainMain(caloriesGain: Int)  {
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_MAIN_DATABASE" // Database select query
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        cursor.moveToLast()

        val calG = cursor.getInt(cursor.getColumnIndex(CALORIES_GAINED)) + caloriesGain

        val index = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_MAIN))
        val steps = cursor.getInt(cursor.getColumnIndex(STEPS_TAKEN))
        val calB = cursor.getInt(cursor.getColumnIndex(CALORIES_BURNED))
        val water = cursor.getInt(cursor.getColumnIndex(WATER_INTAKE))
        val awardE = cursor.getInt(cursor.getColumnIndex(EXERCISE_AWARD))
        val awardD = cursor.getInt(cursor.getColumnIndex(DIET_AWARD))
        val date = cursor.getString(cursor.getColumnIndex(DATE))

        val contentValues = ContentValues()
        contentValues.put(STEPS_TAKEN, steps)
        contentValues.put(CALORIES_BURNED, calB)
        contentValues.put(CALORIES_GAINED, calG)
        contentValues.put(WATER_INTAKE, water)
        contentValues.put(EXERCISE_AWARD, awardE)
        contentValues.put(DIET_AWARD, awardD)
        contentValues.put(DATE, date)

        db.update(TABLE_MAIN_DATABASE, contentValues, COLUMN_ID_MAIN + "=" + index, null)
        db.close()

    }
    fun addCaloriesBurnMain(extraCal: Int)  {
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_MAIN_DATABASE" // Database select query
        val cursor: Cursor = db.rawQuery(selectQuery, null)



        cursor.moveToLast()
        val previousSteps = cursor.getInt(cursor.getColumnIndex(STEPS_TAKEN)) - 1
        val stepCal = (cursor.getInt(cursor.getColumnIndex(STEPS_TAKEN)) * 0.04).toInt()
        val calBurnedTotal = cursor.getInt(cursor.getColumnIndex(CALORIES_BURNED)) - (previousSteps * 0.04).toInt() + stepCal + extraCal
        val index = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_MAIN))


        val contentValues = ContentValues()
        contentValues.put(CALORIES_BURNED, calBurnedTotal)

        db.update(TABLE_MAIN_DATABASE, contentValues, COLUMN_ID_MAIN + "=" + index, null)
        db.close()

    }

    fun updateGoals(bmr: Double, program: Int, weight: Int) {
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_MAIN_DATABASE" // Database select query
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        cursor.moveToLast()
        val index = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_MAIN))
        val contentValues = ContentValues()
        val caloriesChangeAvg = bmr * 1.55
        var caloriesBGoal = 0
        var caloriesGGoal = 0


        // programs 1. loseWeight  2. buildMuscle  3. becomeFit
        if (program == 1) {
            caloriesBGoal = (caloriesChangeAvg * 1.02).toInt()
            caloriesGGoal = (caloriesChangeAvg * 0.96).toInt()
        } else if (program == 2) {
            caloriesBGoal = (caloriesChangeAvg).toInt()
            caloriesGGoal = (caloriesChangeAvg).toInt()
        } else if (program == 3) {
            caloriesBGoal = (caloriesChangeAvg + 1.02).toInt()
            caloriesGGoal = (caloriesChangeAvg * 1.08).toInt()
        }

        contentValues.put(CALORIES_GAINED_GOAL, caloriesGGoal)
        contentValues.put(CALORIES_BURNED_GOAL, caloriesBGoal)
        db.update(TABLE_MAIN_DATABASE, contentValues, COLUMN_ID_MAIN + "=" + index, null)
        db.close()

    }

}
