package com.example.orangeai.models

import android.content.Context
import android.graphics.drawable.Drawable
import com.example.orangeai.R

class setRecipes(context: Context) {

    fun createModels(): ArrayList<Recipes> {
        val dataModels: ArrayList<Recipes> = ArrayList()

        val food1 = Recipes(
            1, "Chicken Parm Peppers", "Halved bell peppers filled and cooked with \r\na cheesy chicken filling. Takes around 1 hour \r\nto make. Gives 4 servings of 2 halves each.", 354, R.drawable.pepper_dish, "https://www.delish.com/cooking/recipe-ideas/recipes/a51054/chicken-parm-stuffed-peppers-recipe/"
        )
        dataModels.add(food1)

        val food2 = Recipes(
            2, "Skinny Alfredo Pasta", "Replaces heavy cream with greek yogurt, \r\nputting a healthy twist on tradition. Takes \r\naround 30 minutes and gives 4 servings. ", 0, R.drawable.alfredo_dish, "https://www.delish.com/cooking/recipe-ideas/recipes/a45568/skinny-alfredo-recipe/"
        )
        dataModels.add(food2)

        val food3 = Recipes(
            3, "Caprese Zoodles",     "Zoodles are a healthy, keto-based snack made \r\nfrom zucchini that imitate noodles. Takes \r\njust 30 minutes and yields 4 servings.", 260, R.drawable.zoodle_dish, "https://www.delish.com/cooking/recipe-ideas/recipes/a47336/caprese-zoodles-recipe/"
        )
        dataModels.add(food3)


        val food4 = Recipes(
            4, "Fava Bean Spring Risotto",      "Favoring health, Spring Risotto brings multiple \r\nfood items together for a quality meal. Takes\r\naround an hour to prepare, giving 8 servings.", 238, R.drawable.risotto_dish, "https://www.myrecipes.com/recipe/spring-risotto-0"
        )
        dataModels.add(food4)

        val food5 = Recipes(
            5, "Garlic Turkey Stir-Fry","Quick and simple, this recipe merges veggies \r\nand meat to give you a small, tasty dish. Takes \r\naround 15 minutes and gives 4 servings.", 262, R.drawable.turkey_dish, "https://www.myrecipes.com/recipe/garlic-turkey-broccoli-stir-fry"
        )
        dataModels.add(food5)

        val food6 = Recipes(
            6, "Cheesy Chicken Cutlets", "A more straightforward take on Cordon Bleu, \r\nthis recipe offers a convenient exotic meal. \r\nTakes around 30 minutes, giving 4 servings \r\nof one cutlet each.", 266, R.drawable.cutlets_dish, "https://www.myrecipes.com/recipe/cheesy-chicken-cutlets-ham-jam"
        )
        dataModels.add(food6)

        return dataModels
    }

}

