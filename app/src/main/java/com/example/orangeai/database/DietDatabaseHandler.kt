package com.example.orangeai.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.orangeai.models.ActivityPrograms
import com.example.orangeai.models.FoodImages
import com.example.orangeai.models.FoodNutrients
import com.example.orangeai.utils.Constants.DBVERSION

class DietDatabaseHandler(context: Context) :
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


    }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_DIET_TABLE = ("CREATE TABLE " + TABLE_DIET_HISTORY + "("
                + COLUMN_ID_DIET + " INTEGER PRIMARY KEY,"
                + FOOD_NAME + " TEXT,"
                + FOOD_CALORIES + " INTEGER,"
                + FOOD_CARBS + " REAL,"
                + FOOD_PROTEINS + " REAL,"
                + FOOD_LIPIDS + " REAL,"
                + FOOD_IMAGE_URI + " TEXT,"
                + FOOD_DATE + " TEXT)")

        db?.execSQL(CREATE_DIET_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

        db?.execSQL("DROP TABLE IF EXISTS " +  TABLE_DIET_HISTORY) // It drops the existing history table
        onCreate(db)
    }




    fun addFoodToHistory(model: Array<FoodNutrients>): Long  {
        val db = this.writableDatabase
        var contentValues: ContentValues
        var result: Long = 0

        for (i in 0 until model.size) {

            contentValues = ContentValues()
            // contentValues.put(COLUMN_ID, model[i].id)
            contentValues.put(FOOD_NAME, model[i].foodName)
            contentValues.put(FOOD_CALORIES, model[i].calories)
            contentValues.put(FOOD_CARBS, model[i].carbValue)
            contentValues.put(FOOD_PROTEINS, model[i].proteinValue)
            contentValues.put(FOOD_LIPIDS, model[i].lipidValue)
            if (model[i].imageURI != null) {
                contentValues.put(FOOD_IMAGE_URI, model[i].imageURI)
            }
            contentValues.put(FOOD_DATE, model[i].date)
            result = db.insert(TABLE_DIET_HISTORY, null, contentValues)

        }
        db.close()
        return result
    }

    fun getFoodData() : ArrayList<FoodImages> {
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_DIET_HISTORY"

        var something: ArrayList<FoodImages> = ArrayList()

        val cursor: Cursor = db.rawQuery(selectQuery, null)
        while (cursor.moveToNext()) {
            val foodName = cursor.getString(cursor.getColumnIndex(FOOD_NAME))
            val foodCalories = cursor.getInt(cursor.getColumnIndex(FOOD_CALORIES))
            val foodDate = cursor.getString(cursor.getColumnIndex(FOOD_DATE))
            val foodImageUri = cursor.getString(cursor.getColumnIndex(FOOD_IMAGE_URI))

            val somethingModel = FoodImages(foodName, foodCalories, foodDate, foodImageUri)
            something.add(somethingModel)
        }

        return something
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