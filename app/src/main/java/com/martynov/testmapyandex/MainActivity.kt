package com.martynov.testmapyandex

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.PagerSnapHelper

import androidx.recyclerview.widget.SnapHelper
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PlacemarkMapObject


class MainActivity : AppCompatActivity() {
    private val MAPKIT_API_KEY = "84d57f4c-95cc-4ea2-bc43-037ccc559c0a"
    private val TARGET_LOCATION = Point(55.75370903771494, 37.61981338262558)
    lateinit var view: TextView
    val myAdapter = Adapter()
    val listPlacemarkMapObject = arrayListOf<PlacemarkMapObject>()
    private val listView = arrayListOf<UserDataTemplase>()
    @SuppressLint("ResourceAsColor")
    private val listener = MapObjectTapListener { mapObject, point ->
        val data = mapObject.userData as UserDataTemplase
        clickPoint(data)
        false
    }


    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        addView()
        setContentView(R.layout.activity_main)
        myAdapter.add(listView)
        Log.d("MyLogS", "${rcw != null}")
        val mSnapHelper: SnapHelper = PagerSnapHelper()
        mSnapHelper.attachToRecyclerView(rcw)
        rcw.apply {
            adapter = myAdapter
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

        }
        rcw.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lm = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItem: Int = lm.findFirstVisibleItemPosition() + 1
                if(firstVisibleItem < listPlacemarkMapObject.size){
                    val data = listPlacemarkMapObject[firstVisibleItem].userData as UserDataTemplase
                    clickPoint(data, false)
                }
            }


        })

        for (i in listView) {
            listPlacemarkMapObject.add(mapview.map.mapObjects.addPlacemark(
                i.point,
                ViewProvider(i.textView)
            ).apply { userData = i })
        }
        mapview.map.move(
            CameraPosition(
                TARGET_LOCATION, 15F, 0F, 0F
            )
        )
        mapview.map.addCameraListener { map, cameraPosition, cameraUpdateReason, b ->
            if (b) {
                Log.d("MyLogS", "движение")
            }

        }
        button.setOnClickListener {

            val leftCornerLatitude = mapview.map.visibleRegion.topLeft.latitude
            val leftCornerLongitude = mapview.map.visibleRegion.topLeft.longitude
            Log.d(
                "MyLogS",
                "leftCornerLatitude ${leftCornerLatitude}  leftCornerLongitude ${leftCornerLongitude}"
            )
            val rightCornerLatitude = mapview.map.visibleRegion.bottomRight.latitude
            val rightCornerLongitude = mapview.map.visibleRegion.bottomRight.longitude
            Log.d(
                "MyLogS",
                "rightCornerLatitude ${rightCornerLatitude}  rightCornerLongitude ${rightCornerLongitude}"
            )
            val x = UserDataTemplase(
                Point(
                    mapview.map.cameraPosition.target.latitude,
                    mapview.map.cameraPosition.target.longitude
                ), TextView(this).apply {
                    background = getDrawable(R.drawable.bg_edit_text_white_normal)
                    text = "X"
                }, "X", false
            )
            val y = UserDataTemplase(
                Point(leftCornerLatitude, leftCornerLongitude),
                TextView(this).apply {
                    background = getDrawable(R.drawable.bg_edit_text_white_normal)
                    text = "Y"
                },
                "Y",
                false
            )
            val z = UserDataTemplase(
                Point(rightCornerLatitude, rightCornerLongitude),
                TextView(this).apply {
                    background = getDrawable(R.drawable.bg_edit_text_white_normal)
                    text = "Z"
                },
                "Z",
                false
            )

            mapview.map.mapObjects.addPlacemark(
                x.point,
                ViewProvider(x.textView)
            ).apply {
                userData = x
            }
            mapview.map.mapObjects.addPlacemark(
                y.point,
                ViewProvider(y.textView)
            ).userData = y

            mapview.map.mapObjects.addPlacemark(
                z.point,
                ViewProvider(z.textView)
            ).userData = z

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

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    fun addView() {
        listView.add(
            UserDataTemplase(
                Point(55.7534898899848, 37.61989104857838),
                TextView(this).apply {
                    text = "1900 р"
                    background = getDrawable(R.drawable.bg_edit_text_white_normal)
                    setPadding(16)

                },
                "1900 р",
                false
            )
        )
        listView.add(
            UserDataTemplase(
                Point(55.76195451599907, 37.611245953647256),
                TextView(this).apply {
                    text = "2000 р"
                    background = getDrawable(R.drawable.bg_edit_text_white_normal)
                    setPadding(16)

                },
                "2000 р",
                false
            )
        )


        listView.add(
            UserDataTemplase(
                Point(55.75443135632715, 37.62129520785743),
                TextView(this).apply {
                    text = "3000 р"
                    background = getDrawable(R.drawable.bg_edit_text_white_normal)
                    setPadding(16)

                },
                "3000 р",
                false
            )
        )

        listView.add(
            UserDataTemplase(
                Point(55.74955563741445, 37.616863105107456),
                TextView(this).apply {
                    text = "4000 р"
                    background = getDrawable(R.drawable.bg_edit_text_white_normal)
                    setPadding(16)

                },
                "4000 р",
                false
            )
        )

        listView.add(
            UserDataTemplase(
                Point(55.74955563741445, 37.623121129264206),
                TextView(this).apply {
                    text = "5000 р"
                    background = getDrawable(R.drawable.bg_edit_text_white_normal)
                    setPadding(16)

                },
                "5000 р",
                false
            )
        )

        listView.add(
            UserDataTemplase(
                Point(55.7524166262935, 37.623776403594405),
                TextView(this).apply {
                    text = "6000 р"
                    background = getDrawable(R.drawable.bg_edit_text_white_normal)
                    setPadding(16)

                },
                "6000 р",
                false
            )
        )

    }
    fun allNoClick(){
        for(i in listPlacemarkMapObject){
            val data = (i.userData as UserDataTemplase)
            data.isClicked = false
            i.setView(ViewProvider(TextView(this).apply {
                text = data.text
                setPadding(16)
                background = getDrawable(R.drawable.bg_edit_text_white_normal)
            }))
        }
    }

    private fun clickPoint(
        data: UserDataTemplase,
        isHide: Boolean = true
    ) {
        val index = listView.indexOfFirst { it.equals(data) }
        Log.d("MyLogS", "${index}")
        if (!data.isClicked) {
            allNoClick()
            data.isClicked = true
            if (isHide) LL.visibility = View.VISIBLE
            listPlacemarkMapObject[index].setView(ViewProvider(TextView(this).apply {
                text = data.text
                setPadding(16)
                background = getDrawable(R.color.purple_700)
            }))
        } else {
            allNoClick()
            data.isClicked = false
            if (isHide) LL.visibility = View.GONE
            listPlacemarkMapObject[index].setView(ViewProvider(TextView(this).apply {
                text = data.text
                setPadding(16)
                background = getDrawable(R.drawable.bg_edit_text_white_normal)
            }))
        }
    }


}