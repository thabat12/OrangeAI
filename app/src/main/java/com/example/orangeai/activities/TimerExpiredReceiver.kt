package com.example.orangeai.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.orangeai.utils.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //TODO: show notification

        PrefUtil.setTimerState(ExerciseActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
