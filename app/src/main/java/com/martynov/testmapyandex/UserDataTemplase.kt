package com.martynov.testmapyandex

import android.widget.TextView
import com.yandex.mapkit.geometry.Point

data class UserDataTemplase (
        val point: Point,
        val textView: TextView,
        val text: String,
        var isClicked : Boolean
        )