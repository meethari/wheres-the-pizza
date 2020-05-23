package com.hari.wheresthepizza

import android.os.Build
import androidx.annotation.RequiresApi
import com.hari.wheresthepizza.adapter.FoodEventAdapter
import com.hari.wheresthepizza.data.FoodEvent
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun returnTimeRangeString(post: FoodEvent) : String {

    val dateString = String.format("%d/%d/%d", post.eventDateDay, post.eventDateMonth, post.eventDateYear)
    val startTimeString = String.format("%02d:%02d", post.startTimeHour,  post.startTimeMinute)
    val endTimeString = String.format("%02d:%02d", post.endTimeHour, post.endTimeMinute)

    if (post.eventType == FoodEventAdapter.EVENT_REGULAR) {
        return "$dateString $startTimeString - $endTimeString"
    } else {
        return "$dateString $startTimeString"
    }
}