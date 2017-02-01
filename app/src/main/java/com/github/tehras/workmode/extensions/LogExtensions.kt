package com.github.tehras.workmode.extensions

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent

fun logError(e: Exception) {
    Answers.getInstance().logCustom(CustomEvent("Error").putCustomAttribute("Exception", e.message))
}

fun logEvent(eventType: EventType, action: String) {
    Answers.getInstance().logCustom(CustomEvent(eventType.event).putCustomAttribute("Action", action))
}

enum class EventType(val event: String) {
    TILE_EVENT("tile_event"), LOCATION_EVENT("location_event")
}