package com.martynov.testmapyandex

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.cluster.*
import kotlinx.android.synthetic.main.view_count.*
import kotlinx.android.synthetic.main.view_count.view.*


class MainActivity : AppCompatActivity() {
    private val MAPKIT_API_KEY = "84d57f4c-95cc-4ea2-bc43-037ccc559c0a"
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private val TARGET_LOCATION = Point(55.75370903771494, 37.61981338262558)
    val myAdapter = Adapter()
    val listPlacemarkMapObject = arrayListOf<PlacemarkMapObject>()
    private val listView = arrayListOf<UserDataTemplase>()
    private lateinit var searchSession: Session
    private lateinit var searchManager: SearchManager

    @SuppressLint("ResourceAsColor")
    private val listener = MapObjectTapListener { mapObject, point ->
        val data = mapObject.userData as UserDataTemplase
        Log.d("MyLogS", "data ${data}")
        clickPoint(data)
        false
    }
    var clusterListener = ClusterListener { cluster ->
        // We setup cluster appearance and tap handler in this method
        cluster.appearance.setView(ViewProvider(newView().apply {
            textView.text = cluster.size.toString()
        }))
    }
    lateinit var clusterizedCollection: ClusterizedPlacemarkCollection
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
            userLocationView.pin.setIcon(
                ImageProvider.fromResource(
                    applicationContext,
                    R.drawable.user_position
                )
            )


        }

        override fun onObjectRemoved(p0: UserLocationView) {
        }

        override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {
        }

    }
    private val searchListener = object : Session.SearchListener {
        override fun onSearchResponse(response: Response) {
            val mapObjects = mapView.map.mapObjects
            mapObjects.clear()
            response.collection.children.forEach {
                val resultLocation: Point? = it.obj?.geometry?.get(0)?.point
                Log.d("MyLogS", "${resultLocation?.latitude} ${resultLocation?.longitude}")
                if (resultLocation != null) {
                    Log.d("MyLogS", "${resultLocation}")
                    addHome(resultLocation)
                }

            }
        }

        override fun onSearchError(p0: Error) {
        }


    }

    fun addHome(point: Point) {
        mapView.map.mapObjects.addPlacemark(
            point,
            ImageProvider.fromResource(applicationContext, R.drawable.ic_home_ad)
        ).apply {
            userData = "asdas"
        }

    }


    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)
        addView()
        setContentView(R.layout.activity_main)
        clusterizedCollection =
            mapView.getMap().getMapObjects().addClusterizedPlacemarkCollection(clusterListener)

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
                if (firstVisibleItem < listPlacemarkMapObject.size) {
                    val data = listPlacemarkMapObject[firstVisibleItem].userData as UserDataTemplase
                    clickPoint(data, false)
                }
            }


        })


        for (i in listView) {
//            listPlacemarkMapObject.add(mapView.map.mapObjects.addPlacemark(
//                i.point,
//                ViewProvider(i.textView)
//            ).apply { userData = i })
            listPlacemarkMapObject.add(
                clusterizedCollection.addPlacemark(
                    i.point,
                    ViewProvider(i.textView)
                ).apply { userData = i })

        }
        clusterizedCollection.clusterPlacemarks(60.0, 15)




        mapView.map.move(
            CameraPosition(
                Point(55.919483, 37.869954), 15F, 0F, 0F
            )
        )
        mapView.map.addCameraListener { map, cameraPosition, cameraUpdateReason, b ->
            if (b) {
                userLocationLayer.resetAnchor()
                Log.d("MyLogS", "движение")
            }

        }
        button.setOnClickListener {
            coordinate()
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
            if (listPlacemarkMapObject.isNotEmpty()) {
                val iterator = listPlacemarkMapObject.iterator()
                while (iterator.hasNext()) {
                    mapView.map.mapObjects.remove(iterator.next())
                    iterator.remove()


                }
//                Log.d("MyLogS","${listPlacemarkMapObject}")
//                listPlacemarkMapObject.forEach {
//                    listPlacemarkMapObject.remove(it)
//                    mapView.map.mapObjects.remove(it)
//
//                }
            }
            //cameraUserPosition()
        }
        user_location_fab.setOnClickListener {
            cameraUserPosition()
        }
        /////

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        val homeAdress = "королев пр-т космонавтов д 9"
        submitQuery(homeAdress)

    }

    private fun coordinate() {
        val leftCornerLatitude = mapView.map.visibleRegion.topLeft.latitude
        val leftCornerLongitude = mapView.map.visibleRegion.topLeft.longitude


        val rightCornerLatitude = mapView.map.visibleRegion.bottomRight.latitude
        val rightCornerLongitude = mapView.map.visibleRegion.bottomRight.longitude

        val otstupX = (leftCornerLatitude - rightCornerLatitude) / 2
        val otstupY = (rightCornerLongitude - leftCornerLongitude) / 2


        val yPlus = UserDataTemplase(
            Point(leftCornerLatitude + otstupX, leftCornerLongitude - otstupY),
            newView().apply {
                textView.text = "YPLUS"
            }, "YYPLUS", false
        )
        val xPlus = UserDataTemplase(
            Point(rightCornerLatitude - otstupX, rightCornerLongitude + otstupY),
            newView().apply {
                textView.text = "XPLUS"
            }, "XPLUS", false
        )

        val z = UserDataTemplase(
            Point(
                mapView.map.cameraPosition.target.latitude,
                mapView.map.cameraPosition.target.longitude
            ), newView().apply {
                textView.text = "Z"
            }, "Z", false
        )
        val y = UserDataTemplase(
            Point(leftCornerLatitude, leftCornerLongitude),
            newView().apply {
                textView.text = "Y"
            },
            "Y",
            false
        )
        val x = UserDataTemplase(
            Point(rightCornerLatitude, rightCornerLongitude),
            newView().apply {
                textView.text = "X"
            },
            "X",
            false
        )

        Log.d(
            "MyLogS",
            "otstupX ${otstupX}  otstupY ${otstupY}"
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

        mapView.map.mapObjects.addPlacemark(
            xPlus.point,
            ViewProvider(xPlus.textView)
        ).userData = xPlus

        mapView.map.mapObjects.addPlacemark(
            yPlus.point,
            ViewProvider(yPlus.textView)
        ).userData = yPlus

        val input = entry(
            Point(leftCornerLatitude + otstupX, leftCornerLongitude - otstupY),
            Point(rightCornerLatitude - otstupX, rightCornerLongitude + otstupY),
            Point(56.332265, 36.808615)
        )
        Log.d("MyLogS", "${input}")
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

    private fun allNoClick() {
        for (i in listPlacemarkMapObject) {
            val data = (i.userData as UserDataTemplase)
            data.isClicked = false
            i.setView(ViewProvider(newView().apply {
                textView.text = data.text
            }))
        }
    }

    private fun clickPoint(
        data: UserDataTemplase,
        isHide: Boolean = true
    ) {
        val index =
            listPlacemarkMapObject.indexOfFirst { (it.userData as UserDataTemplase) == data }
        if (!data.isClicked) {
            allNoClick()
            data.isClicked = true
            if (isHide) LL.visibility = View.VISIBLE
            listPlacemarkMapObject[index].setView(ViewProvider(selectNewView().apply {
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

    private fun newView(): View {
        return LayoutInflater.from(this).inflate(R.layout.view_count, null)
    }

    private fun selectNewView(): View {
        return LayoutInflater.from(this).inflate(R.layout.view_count_select, null)
    }

    fun newClusterVIew(): View {
        return LayoutInflater.from(this).inflate(R.layout.cluster, null)
    }

    private fun entry(leftTopPoint: Point, rightBotPoint: Point, point: Point): Boolean {
        if (leftTopPoint.latitude > point.latitude && leftTopPoint.longitude < point.longitude) {
            if (rightBotPoint.latitude < point.latitude && rightBotPoint.longitude > point.longitude) {
                return true
            }
        }
        return false
    }


    private fun submitQuery(query: String) {
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            searchListener
        )
    }


}