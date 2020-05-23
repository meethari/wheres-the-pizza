package com.hari.wheresthepizza.data

import com.hari.wheresthepizza.adapter.FoodEventAdapter
import java.io.Serializable

// important, make sure each field has a default argument

data class FoodEvent(
    var uid: String = "",
    var title: String = "",
    var locationName: String = "",
    var eventType: Int = FoodEventAdapter.EVENT_REGULAR,
    var eventDateYear: Int = 0,
    var eventDateMonth: Int = 0,
    var eventDateDay: Int = 0,
    var startTimeHour: Int = 0,
    var startTimeMinute: Int = 0,
    var endTimeHour: Int = 0,
    var endTimeMinute: Int = 0,
    var description: String = "",
    var imgUrl: String = ""
) : Serializable
