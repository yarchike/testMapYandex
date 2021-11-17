package com.martynov.testmapyandex

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val MAPKIT_API_KEY = "84d57f4c-95cc-4ea2-bc43-037ccc559c0a"
    private val TARGET_LOCATION = Point(59.945933, 30.320045)
    private val TARGET_LOCATION2 = Point(59.940000, 30.32000)
    private val CLUSTER_CENTERS = arrayListOf<Point>(
        Point(55.756, 37.618)
    )


    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)
        mapview.map.move(
            CameraPosition(
                TARGET_LOCATION, 15F, 0F, 0F
            )
        )
        val view = TextView(this)
        view.text = "1900 р"
        view.background = getDrawable(R.drawable.bg_edit_text_white_normal)
        view.setPadding(16)

//        val view2 = TextView(this)
//        view2.text = "2000 р"
//        view2.background = getDrawable(R.drawable.bg_edit_text_white_normal)
//        view2.setPadding(16)

        val map = mapview.map.mapObjects.addPlacemark(
            TARGET_LOCATION,
            ViewProvider(view)
        )
//        mapview.map.mapObjects.addPlacemark(
//            TARGET_LOCATION2,
//            ViewProvider(view2)
//        )
        val textView = findViewById<TextView>(R.id.search_edit)
        val ll = findViewById<LinearLayout>(R.id.LL)
        val listener = MapObjectTapListener { mapObject, point ->
            Log.d("MyLogS", "mapObject ${mapObject}")
            Log.d("MyLogS", "point ${point}")
            ll.visibility = View.VISIBLE
            textView.text = view.text
            false
        }

        mapview.map.mapObjects.addTapListener(listener)
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview.onStart()
    }

    override fun onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }


}