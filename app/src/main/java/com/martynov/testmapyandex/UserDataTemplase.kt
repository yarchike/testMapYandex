package com.martynov.testmapyandex

import android.view.View
import android.widget.TextView
import com.yandex.mapkit.geometry.Point

data class UserDataTemplase (
        val point: Point,
        val textView: View,
        val text: String,
        var isClicked : Boolean
        )