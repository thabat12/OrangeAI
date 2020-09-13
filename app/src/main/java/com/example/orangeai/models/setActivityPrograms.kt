package com.example.orangeai.models

import android.content.Context
import com.example.orangeai.R

class setActivityPrograms(context: Context) {

    fun createModels(): ArrayList<ActivityPrograms> {
        val dataModels : ArrayList<ActivityPrograms> = ArrayList()

        val walking = ActivityPrograms(
            1, 1, "Walking", "something", 4.0, 4.96, 5.93
        )
        dataModels.add(walking)

        val running = ActivityPrograms(
            1, 1,"Running", "something",8.0, 9.93, 11.8

        )
        dataModels.add(running)

        val cycling = ActivityPrograms(
            1, 1,"Cycling", "something", 7.0, 8.6, 10.36

        )
        dataModels.add(cycling)


        val swimming = ActivityPrograms(
            1, 1,"Swimming", "something",6.0, 7.43, 8.86

        )
        dataModels.add(swimming)

        val squats = ActivityPrograms(
            1,2, "Squats", "something",0.32, 0.40, 0.48

        )
        dataModels.add(squats)

        val burpees = ActivityPrograms(
            1, 2,"Burpees", "something",1.1, 1.1, 1.1

        )
        dataModels.add(burpees)

        val situps = ActivityPrograms(
            1, 2,"Situps", "something",0.225, 0.385, 0.3

        )
        dataModels.add(situps)


        val jumpingJacks = ActivityPrograms(
            1, 2,"Jumping jacks", "something",0.2, 0.32, 0.4

        )
        dataModels.add(jumpingJacks)


        val plank = ActivityPrograms(
            1, 1,"Plank", "something",2.0, 3.5, 4.5

        )
        dataModels.add(plank)

        val pullUps = ActivityPrograms(
            1, 2,"Pull-Ups", "something",1.1, 1.1, 1.1

        )
        dataModels.add(pullUps)

        val pushUps = ActivityPrograms(
            1, 2,"Push-Ups", "something",0.325, 0.325, 0.325

        )
        dataModels.add(pushUps)

        return dataModels
    }

}