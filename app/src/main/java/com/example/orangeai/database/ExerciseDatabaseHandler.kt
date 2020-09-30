package com.example.orangeai.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.orangeai.models.ActivityPrograms
import com.example.orangeai.models.FoodNutrients
import com.example.orangeai.utils.Constants.DBVERSION

class ExerciseDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {

        // THIS IS THE SAME DATABASE VERSION AS THE DIET DATABASE HANDLER. BOTH VERSIONS MUST BE SAME


        private const val DATABASE_VERSION = DBVERSION // This DATABASE Version
        private const val DATABASE_NAME = "OrangeAILogs.db" // Name of the DATABASE

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






    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_EXERCISE_TABLE = ("CREATE TABLE " + TABLE_ACTIVE_EXERCISES + "("
                + COLUMN_ID_EXERCISES + " INTEGER PRIMARY KEY,"
                + EXERCISE_TAG + " INTEGER,"
                + EXERCISE_TITLE + " TEXT,"
                + EXERCISE_IMAGE + " TEXT,"
                + CAL_PER_MIN_LIGHT + " REAL,"
                + CAL_PER_MIN_MEDIUM + " REAL,"
                + CAL_PER_MIN_HEAVY + " REAL)")


        // Create History Table Query.
        db?.execSQL(CREATE_EXERCISE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " +  TABLE_ACTIVE_EXERCISES) // It drops the existing history table
        onCreate(db) // Calls the onCreate function so all the updated table will be created.
    }



    fun getExercisesList(): ArrayList<ActivityPrograms> {

        // A list is initialize using the data model class in which we will add the values from cursor.
        val exerciseList: ArrayList<ActivityPrograms> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_ACTIVE_EXERCISES" // Database select query

        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = ActivityPrograms(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID_EXERCISES)),
                        cursor.getInt(cursor.getColumnIndex(EXERCISE_TAG)),
                        cursor.getString(cursor.getColumnIndex(EXERCISE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(EXERCISE_IMAGE)),
                        cursor.getDouble(cursor.getColumnIndex(CAL_PER_MIN_LIGHT)),
                        cursor.getDouble(cursor.getColumnIndex(CAL_PER_MIN_MEDIUM)),
                        cursor.getDouble(cursor.getColumnIndex(CAL_PER_MIN_HEAVY))
                    )
                    exerciseList.add(place)

                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            return ArrayList()
        }
        return exerciseList
    }




    fun addExerciseToList(model: ArrayList<ActivityPrograms>): Long  {
        val db = this.writableDatabase
        var contentValues: ContentValues
        var result: Long = 0

        for (i in 0 until model.size) {

            contentValues = ContentValues()
            // contentValues.put(COLUMN_ID, model[i].id)
            contentValues.put(EXERCISE_TAG, model[i].tag)
            contentValues.put(EXERCISE_TITLE, model[i].title)
            contentValues.put(EXERCISE_IMAGE, "something")
            contentValues.put(CAL_PER_MIN_LIGHT, model[i].calPerMinL)
            contentValues.put(CAL_PER_MIN_MEDIUM, model[i].calPerMinM)
            contentValues.put(CAL_PER_MIN_HEAVY, model[i].calPerMinH)
            result = db.insert(TABLE_ACTIVE_EXERCISES, null, contentValues)

        }
        db.close()
        return result
    }

    fun deleteExerciseFromList(happyPlace: ActivityPrograms): Int {
        val db = this.writableDatabase
        // Deleting Row
        val success = db.delete(TABLE_ACTIVE_EXERCISES, COLUMN_ID_EXERCISES + "=" + happyPlace.id, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }
}