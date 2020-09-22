package com.example.orangeai.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.orangeai.models.ExerciseHistory
import com.example.orangeai.utils.Constants.DBVERSION

class ExerciseHistoryDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {

        // THIS IS THE SAME DATABASE VERSION AS THE EXERCISE DATABASE HANDLER. BOTH VERSIONS MUST BE SAME

        private const val DATABASE_VERSION = DBVERSION // This DATABASE Version
        private const val DATABASE_NAME = "OrangeAILogs.db" // Name of the DATABASE


        // Exercise History Activity
        private const val TABLE_EXERCISE_HISTORY = "exercise_history" // Table Name
        private const val COLUMN_ID_EXERCISE = "_id" // Column Id
        private const val EXERCISE_NAME = "food_name"
        private const val CALORIES_BURNED = "calories_burned"
        private const val EXERCISE_DURATION = "exercise_duration"
        private const val TIME_OF_ACTIVITY = "time_of_activity"

    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_EXERCISE_HISTORY_TABLE = ("CREATE TABLE " + TABLE_EXERCISE_HISTORY + "("
                + COLUMN_ID_EXERCISE + " INTEGER PRIMARY KEY,"
                + EXERCISE_NAME + " TEXT,"
                + CALORIES_BURNED + " REAL,"
                + EXERCISE_DURATION + " TEXT,"
                + TIME_OF_ACTIVITY + " TEXT)")

        db?.execSQL(CREATE_EXERCISE_HISTORY_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

        db?.execSQL("DROP TABLE IF EXISTS " +  TABLE_EXERCISE_HISTORY) // It drops the existing history table
        onCreate(db)
    }

    fun getExerciseHistory(): ArrayList<ExerciseHistory> {

        // A list is initialize using the data model class in which we will add the values from cursor.
        val historyList: ArrayList<ExerciseHistory> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_EXERCISE_HISTORY" // Database select query

        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = ExerciseHistory(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID_EXERCISE)),
                        cursor.getString(cursor.getColumnIndex(EXERCISE_NAME)),
                        cursor.getDouble(cursor.getColumnIndex(CALORIES_BURNED)),
                        cursor.getString(cursor.getColumnIndex(EXERCISE_DURATION)),
                        cursor.getString(cursor.getColumnIndex(TIME_OF_ACTIVITY))
                    )
                    historyList.add(place)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return historyList
    }




    fun addExerciseToHistory(model: ExerciseHistory) {
        val db = this.writableDatabase
        var contentValues: ContentValues = ContentValues()


        // contentValues.put(COLUMN_ID, model[i].id)
        contentValues.put(EXERCISE_NAME, model.exerciseName)
        contentValues.put(CALORIES_BURNED, model.caloriesBurned)
        contentValues.put(EXERCISE_DURATION, model.duration)
        contentValues.put(TIME_OF_ACTIVITY, model.timeOfActivity)

        db.insert(TABLE_EXERCISE_HISTORY, null, contentValues)


        var result: Long = 0

        db.close()
    }
    /*
    *
    *
        private const val COLUMN_ID_DIET = "_id" // Column Id
        private const val FOOD_CALORIES = "food_calories"
        private const val FOOD_CARBS = "food_carbs"
        private const val FOOD_PROTEINS = "food_proteins"
        private const val FOOD_LIPIDS = "food_lipids"
        private const val FOOD_IMAGE_URI = "food_image_uri"
        private const val FOOD_DATE = "food_date"*/

}