package com.martynov.testmapyandex

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.PagerSnapHelper

import androidx.recyclerview.widget.SnapHelper
import com.yandex.mapkit.Animation
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.view_count.*
import kotlinx.android.synthetic.main.view_count.view.*


class MainActivity : AppCompatActivity() {
    private val MAPKIT_API_KEY = "84d57f4c-95cc-4ea2-bc43-037ccc559c0a"
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private val TARGET_LOCATION = Point(55.75370903771494, 37.61981338262558)
    val myAdapter = Adapter()
    val listPlacemarkMapObject = arrayListOf<PlacemarkMapObject>()
    private val listView = arrayListOf<UserDataTemplase>()
    @SuppressLint("ResourceAsColor")
    private val listener = MapObjectTapListener { mapObject, point ->
        val data = mapObject.userData as UserDataTemplase
        clickPoint(data)
        false
    }
    lateinit var userLocationLayer: UserLocationLayer
    private val listenerLocationObjectListener = object : UserLocationObjectListener {
        override fun onObjectAdded(userLocationView: UserLocationView) {
            userLocationLayer.setAnchor(
                PointF(
                    (mapView.width * 0.5).toFloat(),
                    (mapView.height * 0.5).toFloat()
                ),
                PointF(
                    (mapView.width * 0.5).toFloat(),
                    (mapView.height * 0.83).toFloat()
                )
            )

            userLocationView.arrow.setIcon(
                ImageProvider.fromResource(
                    applicationContext, R.drawable.user_position
                )
            )
            userLocationView.pin.setIcon(ImageProvider.fromResource(applicationContext,  R.drawable.user_position))



        }

        override fun onObjectRemoved(p0: UserLocationView) {
        }

        override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
        }

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
        user_location_fab.setImageResource(R.drawable.ic_polygon_1)
        rcw.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lm = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItem: Int = lm.findFirstVisibleItemPosition() + 1
                if (firstVisibleItem < listPlacemarkMapObject.size) {
                    val data = listPlacemarkMapObject[firstVisibleItem].userData as UserDataTemplase
                    clickPoint(data, false)
                }
            }


        })

        for (i in listView) {
            listPlacemarkMapObject.add(mapView.map.mapObjects.addPlacemark(
                i.point,
                ViewProvider(i.textView)
            ).apply { userData = i })
        }
        mapView.map.move(
            CameraPosition(
                TARGET_LOCATION, 15F, 0F, 0F
            )
        )
        mapView.map.addCameraListener { map, cameraPosition, cameraUpdateReason, b ->
            if (b) {
                Log.d("MyLogS", "движение")
            }

        }
        button.setOnClickListener {


            val leftCornerLatitude = mapView.map.visibleRegion.topLeft.latitude
            val leftCornerLongitude = mapView.map.visibleRegion.topLeft.longitude


            Log.d(
                "MyLogS",
                "leftCornerLatitude ${leftCornerLatitude}  leftCornerLongitude ${leftCornerLongitude}"
            )
            val rightCornerLatitude = mapView.map.visibleRegion.bottomRight.latitude
            val rightCornerLongitude = mapView.map.visibleRegion.bottomRight.longitude
            Log.d(
                "MyLogS",
                "rightCornerLatitude ${rightCornerLatitude}  rightCornerLongitude ${rightCornerLongitude}"
            )
            val x = UserDataTemplase(
                Point(
                    mapView.map.cameraPosition.target.latitude,
                    mapView.map.cameraPosition.target.longitude
                ), newView().apply {
                    textView.text = "X"
                }, "X", false
            )
            val y = UserDataTemplase(
                Point(leftCornerLatitude, leftCornerLongitude),
                newView().apply {
                    textView.text = "Y"
                },
                "Y",
                false
            )
            val z = UserDataTemplase(
                Point(rightCornerLatitude, rightCornerLongitude),
                newView().apply {
                    textView.text = "Z"
                },
                "Z",
                false
            )

            mapView.map.mapObjects.addPlacemark(
                x.point,
                ViewProvider(x.textView)
            ).apply {
                userData = x
            }
            mapView.map.mapObjects.addPlacemark(
                y.point,
                ViewProvider(y.textView)
            ).userData = y

            mapView.map.mapObjects.addPlacemark(
                z.point,
                ViewProvider(z.textView)
            ).userData = z

        }

        mapView.map.mapObjects.addTapListener(listener)
        ///////////////////////////////////////////

        permisionposicion()


        //////////////////////////////////////////
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        //userLocationLayer.setObjectListener(listenerLocationObjectListener)
        button2.setOnClickListener {
            cameraUserPosition()
        }


    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    fun addView() {
        listView.add(
            UserDataTemplase(
                Point(55.7534898899848, 37.61989104857838),
                newView().apply {
                    textView.text = "1900 р"
                    setPadding(16)

                },
                "1900 р",
                false
            )
        )
        listView.add(
            UserDataTemplase(
                Point(55.76195451599907, 37.611245953647256),
                newView().apply {
                    textView.text = "2000 р"
                    setPadding(16)

                },
                "2000 р",
                false
            )
        )


        listView.add(
            UserDataTemplase(
                Point(55.75443135632715, 37.62129520785743),
                newView().apply {
                    textView.text = "3000 р"
                    setPadding(16)

                },
                "3000 р",
                false
            )
        )

        listView.add(
            UserDataTemplase(
                Point(55.74955563741445, 37.616863105107456),
                newView().apply {
                    textView.text = "4000 р"
                    setPadding(16)

                },
                "4000 р",
                false
            )
        )

        listView.add(
            UserDataTemplase(
                Point(55.74955563741445, 37.623121129264206),
                newView().apply {
                    textView.text = "5000 р"
                    setPadding(16)

                },
                "5000 р",
                false
            )
        )

        listView.add(
            UserDataTemplase(
                Point(55.7524166262935, 37.623776403594405),
                newView().apply {
                    textView.text = "6000 р"
                    setPadding(16)

                },
                "6000 р",
                false
            )
        )

    }

    fun allNoClick() {
        for (i in listPlacemarkMapObject) {
            val data = (i.userData as UserDataTemplase)
            data.isClicked = false
            Log.d("MyLogS","${data.text}")
            i.setView(ViewProvider(newView().apply {
                textView.text = data.text
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
            listPlacemarkMapObject[index].setView(ViewProvider(selectNewVIew().apply {
                textView.text = data.text
            }))
        } else {
            allNoClick()
            data.isClicked = false
            if (isHide) LL.visibility = View.GONE
            listPlacemarkMapObject[index].setView(ViewProvider(newView().apply {
                textView.text = data.text
            }))
        }
    }

    private fun permisionposicion() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    private fun cameraUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            val routeStartLocation = userLocationLayer.cameraPosition()!!.target
            mapView.map.move(
                CameraPosition(routeStartLocation, 16f, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        } else {
            mapView.map.move(CameraPosition(Point(0.0, 0.0), 16f, 0f, 0f))
        }
    }

    fun newView():View{
        return  LayoutInflater.from(this).inflate(R.layout.view_count, null)
    }
    fun selectNewVIew():View{
        return  LayoutInflater.from(this).inflate(R.layout.view_count_select, null)
    }


}