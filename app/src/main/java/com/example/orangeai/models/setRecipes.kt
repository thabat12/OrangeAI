package com.example.orangeai.models

import android.content.Context
import com.example.orangeai.R

class setRecipes(context: Context) {

    fun createModels(): ArrayList<Recipes> {
        val dataModels: ArrayList<Recipes> = ArrayList()

        val food1 = Recipes(
            1, "Yum", "is something", 4, "@drawable/pepper_dish"
        )
        dataModels.add(food1)

        val food2 = Recipes(
            2, "Yum", "is something", 4, "gay"
        )
        dataModels.add(food2)

        val food3 = Recipes(
            3, "Yum", "is something", 4, "gay"
        )
        dataModels.add(food3)


        val food4 = Recipes(
            4, "Yum", "is something", 4, "gay"
        )
        dataModels.add(food4)

        val food5 = Recipes(
            5, "Yum", "is something", 4, "gay"
        )
        dataModels.add(food5)

        val food6 = Recipes(
            6, "Yum", "is something", 4, "gay"
        )
        dataModels.add(food6)

        return dataModels
    }

}

