package com.rider.afeezo.activity

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.rider.afeezo.R
import com.rider.afeezo.adapter.DisplayVehiclesAdapter
import com.rider.afeezo.adapter.FavLocationSearchAdapter
import com.rider.afeezo.adapter.GooglePlacesAutocompleteAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Genric
import com.rider.afeezo.generic.SlideToActView
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showMapScreenDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.OnCardRideSelected
import com.rider.afeezo.interfaces.OnSelectedPlaceListener
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.interfaces.onFindLocation
import com.rider.afeezo.model.*
import com.rider.afeezo.service.MyFirebaseMessagingService
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.car_layout.view.*
import kotlinx.android.synthetic.main.dialog_end_ride.view.*
import kotlinx.android.synthetic.main.dialog_ride_cancel.view.*
import kotlinx.android.synthetic.main.dialog_ride_fare_detail.view.*
import kotlinx.android.synthetic.main.dialog_sos.view.*
import kotlinx.android.synthetic.main.dialog_split_fare_detail.view.*
import kotlinx.android.synthetic.main.include_toolbar_main.*
import kotlinx.android.synthetic.main.include_toolbar_search.view.*
import kotlinx.android.synthetic.main.layout_dest_place_autocomplete.*
import kotlinx.android.synthetic.main.layout_main_toolbar.*
import kotlinx.android.synthetic.main.layout_src_place_autocomplete.*
import kotlinx.android.synthetic.main.layout_src_place_autocomplete.favouriteSrcBtn
import kotlinx.android.synthetic.main.lets_go_layout.*
import kotlinx.android.synthetic.main.selected_address_view.view.*
import kotlinx.android.synthetic.main.view_toolbar_search.view.*
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.atan


class MapFrontActivity : MainActivity(), OnMapReadyCallback, LocationListener,
    OnMapClickListener, ValueEventListener, ResponseHandler, OnMyLocationButtonClickListener,
    View.OnClickListener, OnSelectedPlaceListener, onFindLocation,
    SlideToActView.OnSlideCompleteListener, OnCardRideSelected {
    var SEARCH_TYPE = Constant.SEARCH_SOURCE
    private var FAV_TYPE = "FAV_TYPE"
    private var content: ViewGroup? = null
    private var tempDetails: RideDetail? = null
    private var longitude = 0.0
    private var latitude = 0.0
    private var coupon: String? = ""
    private var vehicles: Vehicles? = null
    private var mapFragment: MapFragment? = null
    private var markerPoints: ArrayList<LatLng>? = null
    private var genric: Genric? = null
    private var filter: IntentFilter? = null
    private var gpsFilter: IntentFilter? = null
    private var receiver: BroadcastReceiver? = null //Receiver for ride data and notifications
    private var mGpsSwitchStateReceiver: BroadcastReceiver? =
        null // Receiver for GPS state to load map
    private var bundle: Bundle? = null
    private var startPoint: LatLng? = null
    private var endPoint: LatLng? = null
    private var driverPos: LatLng? = null
    private var endPosition: LatLng? = null
    private var driverimage: String? = null
    private var endLocName: String? = null
    private var cancelCharge: String? = null
    private var cancelText: String? = null
    private var endLocAddress: String? = null
    private var startLocName: String? = null
    private var wallet = "0"
    private var locationData: ArrayList<Locations> = ArrayList()
    private var rideStatus = ArrayList<RideStatus>()
    var selectedVehicleId: String? = "" // Selection from vehicle adapter
    var selectedFinalFare: String? = "" // Selection from vehicle adapter
    private var driverMobile: String? = null
    private var locationTypeFav: String? = null
    private var markers: ArrayList<Marker>? = ArrayList()
    private var isFavClick = false
    private var lastCarSelectedCarId = -1
    private var startLocAddress: String? = null
    private var firstTimeSameLoc = false
    private var rideAccepted = false

    private var greyPolyLine: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var rideCancelTimer: CountDownTimer? = null
    private var requestCancelFrom: String? = null
    private var isSrcFilled = false
    var cabType: String? = ""
    private var estimatedTime = ""
    private var carMarker: Marker? = null
    private var statusValue: String? = ""
    private var isSelectedLocManually = false
    private var payment_mode: String? = null
    private var payment_card: String? = null
    private var mMap: GoogleMap? = null
    private var mFirebaseDatabaseRef: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null
    private var mLastLocation: Location? = null
    private var mLocationRequest: LocationRequest? = null
    var isFirstTime = true
    private var isSrcFav = false
    private var isDestFav = false
    private var favSrcId: String? = ""
    private var favDestId: String? = ""
    private var toolbarSearchDialog: Dialog? = null
    private lateinit var view: View
    private var searchAdapter: FavLocationSearchAdapter? = null
    private var dataAdapter: GooglePlacesAutocompleteAdapter? = null
    private var vehicleAdapter: DisplayVehiclesAdapter? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var recentClicked = false
    var number = ""
    private var vehicleType = "TAXI"
    private var fromRideOrPackage = ""
    var viewmarker: View? = null
    private var mIsCameraIdle = true

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                60
            )
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
        content = findViewById(R.id.relLayout)

        checkPermissions()
        number = instance.getStore(this).getString("number")
        println("number = [${number}]")
        layoutInflater.inflate(R.layout.activity_main, content, true)
        viewmarker = LayoutInflater.from(this).inflate(R.layout.map_marker_layout, null)
        locationData = ArrayList()
        utility = Utility(this)
        if (!instance.getStore(this).getString(Constant.AUTHORIZE).contentEquals("1")) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        mLocationRequest = LocationRequest.create()
        filter = IntentFilter(Constant.RIDE_DETAIL_INTENT)
        gpsFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        initView()
        setMylocationBtn() // The location button on bottom of map to move to current location
        bundle = intent.extras
        try {
            bundle?.let {
                if (nearByFetchTimer != null) nearByFetchTimer?.cancel()
                callRiderDetail(it)

                val mBody = bundle?.getString("Body", "")
                val mTitle = bundle?.getString("Title", "")
                showMapScreenDialog(this, mTitle, mBody)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val bundleReceived = intent.extras
                if (nearByFetchTimer != null) nearByFetchTimer?.cancel()
                if (MyFirebaseMessagingService.notificationManager != null) {
                    MyFirebaseMessagingService.notificationManager?.cancelAll()
                }
                if (bundleReceived != null) {
                    when (bundleReceived.getString("type")) {
                        Constant.RIDE_REQUEST_DECLINED_ALL_DRIVERS -> {
                            showDialog(
                                this@MapFrontActivity,
                                bundleReceived.getString("message"),
                                true
                            )
                        }
                        Constant.RIDE_CANCELLED -> {
                            rideAccepted = false
                            showDialog(
                                this@MapFrontActivity,
                                bundleReceived.getString("message"),
                                false
                            )

                        }
                        else -> callRiderDetail(bundleReceived)
                    }
                }
            }
        }
        mGpsSwitchStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action?.matches("android.location.PROVIDERS_CHANGED".toRegex()) == true) {
                    if (utility.isGpsEnabled) {
                        instance.getStore(this@MapFrontActivity)
                            .saveBoolean(Constant.oneTimeCall, true)
                        initialView()
                    }
                }
            }
        }
        registerReceiver(receiver, filter)
        registerReceiver(mGpsSwitchStateReceiver, gpsFilter)
        rideStatuses

        fbBtn.setOnClickListener {
            activeRide
        }
    }

    var isLocOperatable = false

    @SuppressLint("MissingPermission")
    override fun onBackPressed() {
        mIsCameraIdle = true
        if (instance.rideRequestId.isNotEmpty()) { // The case where ride was requested and then cancelled
            mMap?.uiSettings?.setAllGesturesEnabled(true)
            Log.e("second ", " ")
            cancelRideRequest("Back")
            lnRideData.visibility = View.VISIBLE
            findingRideLt.visibility = View.GONE
        } else if (hsvLayout.visibility == View.VISIBLE) {
            // The case where we click back from location selection screen
            if (mMap != null) mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.setAllGesturesEnabled(false)
            hsvLayout.visibility = View.GONE
            imgBackRide.visibility = View.GONE
            coupon = ""
            mMap?.clear()

            nearByVehicles

            if (recentClicked) { // The case when user goes forward by clicking on recent travel --> We load the location selection screen on back press
                recentClicked = false
                view.edtPickupSearch?.setText(startLocAddress)
                view.edtDestSearch?.setText(endLocAddress)
            }
            loadToolBarSearch()
            manageVisibilityAndText(View.VISIBLE)

//            getCurrentLoc()
        } /*else if (view != null && !isLocOperatable) {
            view?.edt_tool_search?.setText("")
            view?.edtDestSearch?.setText("")ƒ
            view?.edt_tool_search?.requestFocus()
            toolbarSearchDialog?.show()
            getCurrentLoc()
        } */
        /** show home screen when user click on back button */
        else if (instance.rideId!="" && fbBtn.visibility==View.GONE){
            driverDataLayout.visibility = View.GONE
            letsGolayout.visibility = View.VISIBLE
            mainToolbarLayout.visibility = View.VISIBLE
            hsvLayout.visibility = View.GONE
            imgBackRide.visibility = View.GONE
            findingRideLt.visibility = View.GONE
            searchLayout.visibility = View.VISIBLE
            fbBtn.visibility = View.VISIBLE
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint!!, 18f))
            mMap?.clear()

        }
        else { // Default case
            mMap?.uiSettings?.setAllGesturesEnabled(true)
            super.onBackPressed()
        }
    }

    /**
     * Dilaog if the notification is displayed of ride cancel
     * */
    fun showDialog(context: Context, msg: String?, decline: Boolean) {
        val b = AlertDialog.Builder(context, R.style.MaterialThemeDialog)
        b.setTitle(context.getString(R.string.message))
        b.setMessage(msg)
        b.setCancelable(false)
        b.setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
            try {
                if (MyFirebaseMessagingService.notificationManager != null) MyFirebaseMessagingService.notificationManager?.cancelAll()
                if (decline) {
                    cancelRideRequest("Dialog")
                }
                if (mFirebaseDatabaseRef != null) mFirebaseDatabaseRef?.removeEventListener(this)
                instance.rideId = ""
                instance.rideRequestId = ""
                finish()
                instance.getStore(this).saveBoolean(Constant.oneTimeCall, true)
                startActivity(intent)
                overridePendingTransition(0, 0)
                dialog.cancel()
            } catch (ex: Exception) {

            }
        }
        val alert11 = b.create()
        alert11.show()
    }

    private fun callRiderDetail(bundle: Bundle) {
        println("bundle.getString(\"type\") = " + bundle.getString("type"))
        try {
//            callDriverLt.visibility = View.VISIBLE
            when {
                bundle.getString("type").equals(Constant.RIDE_STARTED, ignoreCase = true) -> {
                    cancelRideLt.visibility = View.GONE
                    endRideLt.visibility = View.VISIBLE
                    callDriverLt.visibility = View.GONE
                    otpLayout.visibility = View.INVISIBLE
                    statusValue = "STARTED"
//                    firstTimeSameLoc = false
                    getRideDetail(bundle.getString("recordId"))
                    sosBtn.visibility = View.VISIBLE

                }
                bundle.getString("type")
                    .equals(Constant.RIDE_REQUEST_COMPLETED, ignoreCase = true) -> {
                    statusValue = "BOOKED"
                    getRideDetail(bundle.getString("recordId"))
                }
                bundle.getString("type")
                    .equals(Constant.RIDE_COMPLETED, ignoreCase = true) -> {
                    rideAccepted = false

                    statusValue = "COMPLETED"
                    startActivity(
                        Intent(
                            this,
                            RideCompleteActivity::class.java
                        ).putExtra(Constant.RIDE_ID, bundle.getString("recordId"))
                    )
                }
                bundle.getString("type")
                    .equals(Constant.RIDE_READY_FOR_PICKUP, ignoreCase = true) -> {
                    if (mMap != null) mMap?.clear()
                    statusValue = "PICKUP_READY"
                    getRideDetail(bundle.getString("recordId"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val addressData: Unit
        get() {
            if (locationData != null && locationData.size > 0) locationData.clear()
            val type = object : TypeToken<List<Locations?>?>() {}.type
            val gson = Gson()
            val data = instance.getStore(this).getString(Constant.FAV_LOCATION)
            if (data != "") {
                val arrayList = gson.fromJson<ArrayList<Locations>>(data, type)
                locationData.addAll(arrayList)
            }
            loadToolBarSearch()
        }

    private fun shareRideDetails() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.getRideShareUrl(
            Constant.SHARE_RIDE_DETAIL, instance.token,
            tempDetails?.ride_id ?: "", this
        )
    }

    @SuppressLint("MissingPermission")
    override fun onSuccess(tag: Int, response: Response<*>) {
        super.onSuccess(tag, response)
        hideProgress()
        if (tag == Constant.GET_NEARBY_DRIVERS) {
            val driverData = response.body() as Drivers?
            val carIcon: Bitmap?
            if (driverData != null) {
                when (driverData.status) {
                    "1" -> {
                        if (mMap != null) {
                            mMap?.clear()
                            if (markers != null) markers?.clear()
                        }

//                        carIcon = genric?.getBitmapFromVectorDrawable(this, R.drawable.icon_car)
                        carIcon =
                            AppCompatResources.getDrawable(this, R.drawable.icon_car)?.toBitmap()

                        val smallMarker = carIcon?.let {
                            Bitmap.createScaledBitmap(
                                it,
                                Constant.width,
                                Constant.height,
                                false
                            )
                        }
                        if (driverData.vehicles != null) {
                            setBookBtn(true)
                            for (i in driverData.vehicles.indices) {
                                if (mMap != null) {
                                    val marker = mMap?.addMarker(
                                        MarkerOptions().position(
                                            LatLng(
                                                driverData.vehicles[i].latitude?.toDouble() ?: 0.0,
                                                driverData.vehicles[i].longitude?.toDouble() ?: 0.0
                                            )
                                        )
                                            .icon(smallMarker?.let {
                                                BitmapDescriptorFactory.fromBitmap(
                                                    it
                                                )
                                            })
                                    )
                                    val markerOptions = MarkerOptions()
                                    markerOptions.position(
                                        LatLng(
                                            driverData.vehicles[i].latitude?.toDouble() ?: 0.0,
                                            driverData.vehicles[i].longitude?.toDouble() ?: 0.0
                                        )
                                    )
                                    marker?.let { markers?.add(it) }

                                }
                            }

                        } else {
                            setBookBtn(false)
                        }

                        //         * set polyline  */
                        if (endPoint != null) {
                            mMap?.addPolyline(
                                PolylineOptions().add(startPoint, endPoint)
                                    .width // below line is use to specify the width of poly line.
                                        (10f) // below line is use to add color to our poly line.
                                    .color(Color.BLACK) // below line is to make our poly line geodesic.

                            )
//        // on below line we will be starting the drawing of polyline.
//                            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint!!, 8f))
                        }
                    }
                    Constant.SESSION_EXPIRED -> {
                        utility.sessionExpire(this)
                    }
                    "-1" -> {
                        utility.showSnackBar(driverData.msg ?: "")
                    }
                }
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.GET_VEHICLES) {
            vehicles = response.body() as Vehicles?
            if (vehicles != null) {
                when {
                    vehicles?.status.contentEquals("1") -> {
                        hsvLayout.visibility = View.GONE
                        imgBackRide.visibility = View.GONE
                        editSrcLocation.isEnabled = false
                        isLocOperatable = true
//                        cabType = vehicles?.vehiclesTypes!![0].name
                        setVehiclesAdapter(vehicles)
                    }
                    vehicles?.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    vehicles?.status.contentEquals("-1") -> {
                        letsGolayout.visibility = View.VISIBLE
                        mainToolbarLayout.visibility = View.VISIBLE
                        utility.showSnackBar(vehicles?.msg ?: "")
                        SEARCH_TYPE = Constant.SEARCH_SOURCE
                        /** remove polyline from here , hence  make end point null*/
                        endPoint = null
                        getCurrentLoc()
                        startPoint = LatLng(
                            mMap?.myLocation?.latitude ?: 0.0,
                            mMap?.myLocation?.longitude ?: 0.0
                        )
                        getAddress(startPoint!!.latitude, startPoint!!.longitude)
                    }
                    else -> {
                        utility.showSnackBar(vehicles?.msg ?: "")

                    }
                }
            } else {
                showToast(this, getString(R.string.error_something_wrong))
            }
        } else if (tag == Constant.ADD_RIDE_REQUEST) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        val rideRequestId = common.rideRequestId
                        instance.rideRequestId = rideRequestId ?: ""
                        lnRideData.visibility = View.GONE
                        findingRideLt.visibility = View.VISIBLE
                        common.rideRequestAutoCancelledInterval?.let {
                            val intervalTime = it.toInt().times(60)
                            setRideRespondTime(intervalTime)
                        }
                    }

                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    common.requestId != null -> {
                        instance.rideRequestId = common.requestId ?: ""
                        cancelRideRequest("AddRide")
                        addRideRequest()
                    }
                    else -> showToast(this, common.msg ?: "")
                }
            } else {
                showToast(this, getString(R.string.error_something_wrong))
            }
        } else if (tag == Constant.RIDE_DETAIL) {
            val rideDetails = response.body() as RideDetails?
            if (rideDetails != null) {
                if (rideDetails.status.contentEquals("1")) {
                    isDriverMarkerMoving = false
                    instance.rideId = rideDetails.rideDetail?.ride_id ?: ""
                    endLocName = rideDetails.rideDetail?.ride_end_location
                    driverimage = rideDetails.rideDetail?.driver_image
                    cancelCharge = rideDetails.rideDetail?.cancellation_charges
                    cancelText = rideDetails.rideDetail?.ride_cancel_msg_text
                    Constant.CURRENCY = rideDetails.currencySymbol.toString()
                    ivLocationPin.visibility = View.GONE
                    if (rideCancelTimer != null) rideCancelTimer?.cancel()

                    setUpDriverProfile(rideDetails.rideDetail ?: RideDetail())
                    if (statusValue == "BOOKED") {
                        if (mMap != null) mMap?.clear()
                        startPoint = LatLng(
                            rideDetails.rideDetail?.src_lat?.toDouble() ?: 0.0,
                            rideDetails.rideDetail?.src_lng?.toDouble() ?: 0.0
                        )
//                        drawPathPolyline(startPoint, rideDetails.rideDetail?.ride_start_location, driverPos
                        drawPathPolyline(
                            driverPos, rideDetails.rideDetail?.ride_start_location, startPoint
                        )
                    } else if (statusValue == "PICKUP_READY" || statusValue == "STARTED") {
                        if (mMap != null) mMap?.clear()
                        endPosition = LatLng(
                            rideDetails.rideDetail?.dest_lat?.toDouble() ?: 0.0,
                            rideDetails.rideDetail?.dest_lng?.toDouble() ?: 0.0
                        )
                        drawPathPolyline(
                            driverPos, rideDetails.rideDetail?.ride_end_location, endPosition
                        )
                    }
                    if (mMap != null) mMap?.isMyLocationEnabled = false
                } else if (rideDetails.status.contentEquals(Constant.SESSION_EXPIRED)) {
                    utility.sessionExpire(this)
                } else if (rideDetails.status.contentEquals("-1")) {
                    Utility(this).showSnackBar(rideDetails.msg)
                }
            } else {
                showToast(this, getString(R.string.error_something_wrong))
            }
        } else if (tag == Constant.SAVE_FIREBASE_TOKEN) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        instance.getStore(this).saveBoolean(Constant.ONE_TIME_CALL, false)
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    else -> showToast(this, common.msg ?: "")
                }
            } else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        } else if (tag == Constant.RIDE_STATUSES) {
            val statuses = response.body() as RideStatuses?
            if (statuses != null) {
                when {
                    statuses.status.contentEquals("1") -> {
                        rideStatus.addAll(statuses.rideStatus ?: arrayListOf())
                        activeRide
                    }
                    statuses.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    else -> showToast(this, getString(R.string.error_something_wrong))
                }
            } else {
                showToast(this, getString(R.string.error_something_wrong))
            }
        } else if (tag == Constant.CANCEL_RIDE_REQUEST) {
            val common = response.body() as Common?
            lastCarSelectedCarId = -1
            editSrcLocation.isEnabled = false
            editDestLocation.isEnabled = false
            if (mMap != null) mMap?.clear()
            ivLocationPin.visibility = View.VISIBLE
            SEARCH_TYPE = Constant.SEARCH_DESTINATION
            if (common?.status != null && common.status.contentEquals("1")) {
                instance.rideRequestId = ""
                if (requestCancelFrom != null) {
                    if (requestCancelFrom.equals("Timer", ignoreCase = true)) {
                        showReTryDialog()
                    } else if (!requestCancelFrom.equals("AddRide", ignoreCase = true)) {
                        if (rideCancelTimer != null) rideCancelTimer?.cancel()
                        findingRideLt.visibility = View.GONE
                        lnRideData.visibility = View.VISIBLE
                    }
                }
            } else if (common?.status != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (common?.status != null && common.status.contentEquals("-1")) {
                activeRide
            }
        } else if (tag == Constant.END_RIDE) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        instance.rideRequestId = ""
                        instance.getStore(this).saveString(
                            Constant.COMPLETE_RIDE,
                            instance.rideId
                        )
                        statusValue = "COMPLETED"
                        startActivity(
                            Intent(
                                this,
                                RideCompleteActivity::class.java
                            ).putExtra(Constant.RIDE_ID, instance.rideId)
                        )
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    else -> showToast(this, common.msg ?: "")
                }
            } else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        } else if (tag == Constant.ADD_FAV_LOCATION) {
            val addFavLocation = response.body() as AddFavLocation?
            if (addFavLocation != null && addFavLocation.status.contentEquals("1")) {
                val locations = Locations()
                locations.id = addFavLocation.data?.ufavpointId
                locations.date_added = addFavLocation.data?.ufavpointAddedDate
                locations.google_loc_name = addFavLocation.data?.ufavpointGoogleName
                locations.latitude = addFavLocation.data?.ufavpointLatitude
                locations.longitude = addFavLocation.data?.ufavpointLongitude
                locations.name = addFavLocation.data?.ufavpointName
                var arrayList = ArrayList<Locations>()
                val type = object : TypeToken<List<Locations?>?>() {}.type
                val gson = Gson()
                val data = instance.getStore(this).getString(Constant.FAV_LOCATION)
                if (data != "") {
                    arrayList = gson.fromJson(data, type)
                    for (i in arrayList.indices) {
                        if (arrayList[i].id == locations.id) {
                            arrayList.removeAt(i)
                            break
                        }
                    }
                    arrayList.add(locations)
                } else {
                    arrayList.add(locations)
                }
                val list = gson.toJson(arrayList)
                instance.getStore(this).saveString(Constant.FAV_LOCATION, list)
                showToast(this, addFavLocation.msg.toString())
                if (FAV_TYPE === Constant.FAV_SOURCE) {
                    favSrcId = addFavLocation.data?.ufavpointId
                    favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_filled)
                    isSrcFav = true
                } else if (FAV_TYPE === Constant.FAV_DESTINATION) {
                    favDestId = addFavLocation.data?.ufavpointId
                    favouriteDestBtn.setImageResource(R.drawable.ic_favorite_filled)
                    isDestFav = true
                }
            } else if (addFavLocation != null && addFavLocation.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (addFavLocation != null) {
                showToast(this, addFavLocation.msg.toString())
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.GET_FAV_LOCATION) {
            val favLocation = response.body() as FavLocation?
            if (favLocation != null) {
                when {
                    favLocation.status.contentEquals("1") -> {
                        if (favLocation.locations != null) locationData = favLocation.locations
                        loadToolBarSearch()
                    }
                    favLocation.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    else -> showToast(this, getString(R.string.error_something_wrong))
                }
            } else {
                showToast(this, getString(R.string.error_something_wrong))
            }
        } else if (tag == Constant.SHARE_RIDE_DETAIL) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        val share = Intent(Intent.ACTION_SEND)
                        share.type = "text/plain"
                        share.putExtra(Intent.EXTRA_SUBJECT, common.msg)
                        share.putExtra(Intent.EXTRA_TEXT, common.msg)
                        this.startActivity(Intent.createChooser(share, getString(R.string.share)))
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    else -> showToast(this, common.msg.toString())
                }
            } else {
                showToast(this, getString(R.string.error_something_wrong))
            }
        } else if (tag == Constant.TRIGGER_CONTACTS) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        utility.showSnackBar(common.msg)
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    else -> {
                        showToast(this, common.msg.toString())
                    }
                }
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.RECENT_RIDE_DETAIL) {
            val rideDetails = response.body() as RideDetails?
            if (rideDetails != null) {
                when {
                    rideDetails.status.contentEquals("1") -> {
                        endLocName = rideDetails.rideDetail?.ride_end_location
                        cancelCharge = rideDetails.rideDetail?.cancellation_charges
                        cancelText = rideDetails.rideDetail?.ride_cancel_msg_text
                        Constant.CURRENCY = rideDetails.currencySymbol.toString()
                        rideDetails.rideDetail?.let { loadRecentLocation(it) }
                    }
                    rideDetails.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    rideDetails.status.contentEquals("-1") -> {
                        Utility(this).showSnackBar(rideDetails.msg)
                    }
                }
            } else {
                showToast(this, getString(R.string.error_something_wrong))
            }
        }
    }

    private val rideStatuses: Unit
        get() {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(false)
            instance.getConfigRideStatuses(Constant.RIDE_STATUSES, instance.token, this)
        }

    private val activeRide: Unit
        get() {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
//            showProgress(false)
            instance.getActiveRide(
                Constant.GET_ACTIVE_RIDE,
                instance.token,
                object : ResponseHandler {
                    @SuppressLint("MissingPermission")
                    override fun onSuccess(tag: Int, response: Response<*>) {
                        if (tag == Constant.GET_ACTIVE_RIDE) {
                            val rideDetails = response.body() as RideDetails?
                            if (rideDetails?.amount != null && rideDetails.amount.toDouble() > 0) {
                                cancelChargeMsg.visibility = View.VISIBLE
                                cancelChargeMsg.text = rideDetails.prevChargeText
                            }
                            if (rideDetails?.status.contentEquals("1")) {
                                for (status in rideStatus) {
                                    if (status.id == rideDetails?.rideDetail?.ride_status)
                                        statusValue = status.name
                                }
                                driverimage = rideDetails?.rideDetail?.driver_image
                                isDriverMarkerMoving = false
                                selectedVehicleId = rideDetails?.rideDetail?.vtype_id
                                selectedFinalFare = rideDetails?.rideDetail?.ride_total_fare
                                instance.rideId = rideDetails?.rideDetail?.ride_id ?: ""
                                endLocName = rideDetails?.rideDetail?.ride_end_location
                                cancelCharge = rideDetails?.rideDetail?.cancellation_charges
                                cancelText = rideDetails?.rideDetail?.ride_cancel_msg_text
                                Constant.CURRENCY = rideDetails?.currencySymbol ?: ""

                                if (statusValue?.isNotEmpty() == true) {

                                    setUpDriverProfile(rideDetails?.rideDetail ?: RideDetail())

                                    when (statusValue) {
                                        "BOOKED" -> {
                                            viewSource.background =
                                                ContextCompat.getDrawable(
                                                    this@MapFrontActivity,
                                                    R.drawable.edit_bg
                                                )
                                            viewDest.background =
                                                ContextCompat.getDrawable(
                                                    this@MapFrontActivity,
                                                    R.drawable.edit_bg
                                                )
                                            SEARCH_TYPE = Constant.SEARCH_EMPTY
                                            ivLocationPin.visibility = View.GONE
                                            if (nearByFetchTimer != null) nearByFetchTimer?.cancel()
                                            if (mMap != null) mMap?.clear()
                                            startPoint = LatLng(
                                                rideDetails?.rideDetail?.src_lat?.toDouble() ?: 0.0,
                                                rideDetails?.rideDetail?.src_lng?.toDouble() ?: 0.0
                                            )
                                            /** swap driverPos with startPoint*/
                                            drawPathPolyline(
                                                driverPos,
                                                rideDetails?.rideDetail?.ride_start_location,
                                                startPoint
                                            )
                                        }
                                        "PICKUP_READY" -> {
                                            viewSource.background =
                                                ContextCompat.getDrawable(
                                                    this@MapFrontActivity,
                                                    R.drawable.edit_bg
                                                )
                                            viewDest.background =
                                                ContextCompat.getDrawable(
                                                    this@MapFrontActivity,
                                                    R.drawable.edit_bg
                                                )
                                            SEARCH_TYPE = Constant.SEARCH_EMPTY
                                            ivLocationPin.visibility = View.GONE
                                            if (nearByFetchTimer != null) nearByFetchTimer?.cancel()
                                            startPoint = LatLng(
                                                rideDetails?.rideDetail?.src_lat?.toDouble()
                                                    ?: 0.0,
                                                rideDetails?.rideDetail?.src_lng?.toDouble()
                                                    ?: 0.0
                                            )
                                            if (mMap != null) mMap?.clear()
                                            endPosition = LatLng(
                                                rideDetails?.rideDetail?.dest_lat?.toDouble()
                                                    ?: 0.0,
                                                rideDetails?.rideDetail?.dest_lng?.toDouble() ?: 0.0
                                            )
                                            drawPathPolyline(
                                                driverPos,
                                                rideDetails?.rideDetail?.ride_end_location,
                                                endPosition
                                            )
                                        }
                                        "STARTED" -> {
                                            viewSource.background =
                                                ContextCompat.getDrawable(
                                                    this@MapFrontActivity,
                                                    R.drawable.edit_bg
                                                )
                                            viewDest.background =
                                                ContextCompat.getDrawable(
                                                    this@MapFrontActivity,
                                                    R.drawable.edit_bg
                                                )
                                            SEARCH_TYPE = Constant.SEARCH_EMPTY
                                            ivLocationPin.visibility = View.GONE
                                            if (nearByFetchTimer != null) nearByFetchTimer?.cancel()
                                            sosBtn.visibility = View.VISIBLE
                                            endPosition = LatLng(
                                                rideDetails?.rideDetail?.dest_lat?.toDouble()
                                                    ?: 0.0,
                                                rideDetails?.rideDetail?.dest_lng?.toDouble() ?: 0.0
                                            )
                                            startPoint = LatLng(
                                                rideDetails?.rideDetail?.src_lat?.toDouble()
                                                    ?.toDouble() ?: 0.0,
                                                rideDetails?.rideDetail?.src_lng?.toDouble()
                                                    ?.toDouble() ?: 0.0
                                            )
                                            if (mMap != null) mMap?.clear()
                                            drawPathPolyline(
                                                driverPos,
                                                rideDetails?.rideDetail?.ride_end_location,
                                                endPosition
                                            )
                                            cancelRideLt.visibility = View.GONE
                                            otpLayout.visibility = View.INVISIBLE
                                            endRideLt.visibility = View.VISIBLE
                                            callDriverLt.visibility = View.GONE

                                        }
                                    }
                                    if (mMap != null) mMap?.isMyLocationEnabled = false
                                }
                            } else if (rideDetails != null && rideDetails.status.contentEquals("-1")) {
                                getNearByDrivers(
                                    latitude.toString() + "",
                                    longitude.toString() + ""
                                )
                            } else if (rideDetails != null && rideDetails.status.contentEquals(
                                    Constant.SESSION_EXPIRED
                                )
                            ) {
                                utility.sessionExpire(this@MapFrontActivity)
                            } else showToast(
                                this@MapFrontActivity,
                                getString(R.string.error_something_wrong)
                            )
                        }
                    }


                    override fun onError(throwable: Throwable) {
                        println("throwable Active ride = [${throwable.printStackTrace()}]")
                        hideProgress()
                    }

                })
        }

    private fun setBookBtn(enable: Boolean) {
        if (enable) {
            confirmBookBtn.isEnabled = true
//            confirmBookBtn.setBackgroundResource(R.drawable.button_bg)
            confirmBookBtn.alpha = 1f
            confirmBookBtn.text = getString(R.string.book) + " " + cabType
        } else {
            confirmBookBtn.isEnabled = false
            confirmBookBtn.alpha = 0.6f
            confirmBookBtn.text =
                getString(R.string.no) + " " + cabType + " " + getString(R.string.available)
        }
    }

    override fun onError(throwable: Throwable) {
        println("throwable = [${throwable.printStackTrace()}]")
        hideProgress()
        utility.showSnackBar(resources.getString(R.string.error_something_wrong))
    }

    private fun setUpDriverProfile(rideDetails: RideDetail) {
        try {
            rideAccepted = true
            if (MyFirebaseMessagingService.notificationManager != null) MyFirebaseMessagingService.notificationManager?.cancelAll()
            tempDetails = rideDetails
            carName.text = rideDetails.ride_vehicle_make + " " + rideDetails.ride_vehicle_model
            driverMobile = rideDetails.ride_driver_phone
            carNumber.text = rideDetails.ride_vehicle_number
            driverName.text = rideDetails.ride_driver_name
            tvPaymentRide.text = payment_mode
            when (payment_mode) {
                Constant.CARD -> {
                    Utility.setCustomDrawable(
                        setup_txt_payment,
                        R.drawable.bt_ic_card,
//                        R.drawable.ic_arrow_up
                        0
                    )
                    Utility.setCustomDrawable(
                        tvPaymentRide,
                        R.drawable.bt_ic_card,
//                        R.drawable.ic_right_arrow
                        0
                    )
                }
                Constant.WALLET -> {
                    Utility.setCustomDrawable(
                        setup_txt_payment,
                        R.drawable.ic_wallet,
//                        R.drawable.ic_arrow_up
                        0
                    )
                    Utility.setCustomDrawable(
                        tvPaymentRide,
                        R.drawable.ic_wallet,
//                        R.drawable.ic_right_arrow
                        0
                    )
                }
                else -> {
                    payment_mode = Constant.CASH
                    Utility.setCustomDrawable(
                        setup_txt_payment,
                        R.drawable.ic_cash_on_delivery,
//                        R.drawable.ic_arrow_up
                        0
                    )
                    Utility.setCustomDrawable(
                        tvPaymentRide,
                        R.drawable.ic_cash_on_delivery,
//                        R.drawable.ic_right_arrow
                        0
                    )
                }
            }
            setup_txt_payment.text = payment_mode
            val value = DecimalFormat("#.##")
            if (rideDetails.driverRating != null) {
                starImageView.setImageResource(R.drawable.star_yellow)
                driverRating.text =
                    value.format(rideDetails.driverRating.toDouble().toDouble()) + ""
            } else {
                starImageView.setImageResource(R.drawable.ic_empty_star)
                driverRating.text = getString(R.string.not_applicable)
            }
            rideOtp.text = rideDetails.ride_otp
            estimatedFare.text = Constant.CURRENCY + " " + rideDetails.rideEstimates?.base_fare
            estimatedTimeArrival.text =
                rideDetails.ride_time_minutes + " " + resources.getString(R.string.min)
            Utility.showImage(this, driverCarImage, rideDetails.driver_image)
            Utility.showImage(this, driverImage, rideDetails.icon)
            instance.rideRequestId = ""
            instance.rideId = rideDetails.ride_id.toString()
            driverDataLayout.visibility = View.VISIBLE
            letsGolayout.visibility = View.GONE
            mainToolbarLayout.visibility = View.GONE
            hsvLayout.visibility = View.GONE
            imgBackRide.visibility = View.VISIBLE
            findingRideLt.visibility = View.GONE
            searchLayout.visibility = View.GONE
            fbBtn.visibility = View.GONE
            driverPos = LatLng(
                rideDetails.driver_lat?.toDouble() ?: 0.0,
                rideDetails.driver_lng?.toDouble() ?: 0.0
            )

            mFirebaseDatabaseRef?.child(rideDetails.ride_driver_id ?: "0")
                ?.addValueEventListener(this)
            val behavior = BottomSheetBehavior.from<View>(second_bottom_sheet_holder)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            mMap?.uiSettings?.setAllGesturesEnabled(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun endRide() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        endLocName = if (mLastLocation == null) {
            val midLatLng = mMap?.cameraPosition?.target
            getAddress(midLatLng?.latitude!!, midLatLng.longitude)
        } else {
            getAddress(mLastLocation?.latitude!!, mLastLocation?.longitude!!)
        }
        showProgress(false)
        instance.endRide(
            Constant.END_RIDE,
            tempDetails?.ride_id,
            latitude.toString(),
            longitude.toString(),
            endLocName,
            instance.token,
            this
        )
    }

    private fun triggerEmergencyContacts() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.triggerEmergencyContacts(
            Constant.TRIGGER_CONTACTS,
            tempDetails?.ride_id,
            instance.token,
            this
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        isSelectedLocManually = false
        if (startPoint != null && startPoint !== LatLng(
                latitude,
                longitude
            ) && endPoint != null && instance.rideRequestId.isEmpty()
        ) {
            if (instance.rideId.isEmpty()) {
                getVehicleType(
                    latitude.toString(),
                    longitude.toString(),
                    endPoint?.latitude.toString(),
                    endPoint?.longitude.toString()
                )
            }
        }
        return false
    }

    private inner class ParserTask : AsyncTask<Array<Any>, Int, Any>() {
        override fun doInBackground(vararg p0: Array<Any>): Any {
            if (SEARCH_TYPE === Constant.SEARCH_SOURCE)
                getAddress(
                    startPoint?.latitude!!, startPoint?.longitude!!
                )
            else {
                getAddress(endPoint?.latitude!!, endPoint?.longitude!!)
            }
            return ""
        }

        override fun onPostExecute(o: Any) {
            println("startLocAddress async set= $startLocAddress")
            if (startLocAddress != null || endLocAddress != null) {
                if (SEARCH_TYPE === Constant.SEARCH_SOURCE) {
                    editSrcLocation.setText(startLocAddress)
                    tvSrcLocation.text = startLocAddress
                    edtLocate.setText(startLocAddress)
                    isSrcFilled = true
                    isFirstTime = false
                    Log.e("Under Set Location:: ", "" + isFirstTime)
                } else {
                    editDestLocation.setText(endLocAddress)
                    if (view != null) {
                        view.edtDestSearch?.setText(endLocAddress)
                    }
                    edtLocate.setText(endLocAddress)
                }
            } else {
                showToast(this@MapFrontActivity, getString(R.string.pls_select_start_loc))
            }
        }
    }

    private fun setLocation() {
        try {
            val parserTask = ParserTask()
            parserTask.execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class locationAsyncTask : AsyncTask<Any?, Any?, Any?>() {
        override fun doInBackground(objects: Array<Any?>): Any {
            if (isSelectedLocManually) getAddress(
                startPoint?.latitude!!,
                startPoint?.longitude!!
            ) else
                getAddress(latitude, longitude)
            return ""
        }

        override fun onPostExecute(o: Any?) {
            super.onPostExecute(o)
            println("startLocAddress async source= $startLocAddress")
            if (startLocAddress != null) {
                editSrcLocation.setText(startLocAddress)
                tvSrcLocation.text = startLocAddress
                isSrcFilled = true
                isFirstTime = false
                Log.e("Under SrcLoc Location", "" + isFirstTime)
            } else {
                showToast(this@MapFrontActivity, getString(R.string.pls_select_start_loc))
            }
        }
    }

    private fun setSourceLoc() {
        try {
            val task = locationAsyncTask()
            task.execute()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        when (view.id) {

            R.id.confirmBookBtn -> {
                if (selectedVehicleId.isNullOrEmpty()) {
                    showToast(this, getString(R.string.please_select_vehicle))
                    return
                }
                if (payment_mode.isNullOrEmpty()) {
                    showToast(this, getString(R.string.pick_a_payment_option))
                    return
                }
                addRideRequest()
            }
            R.id.favouriteSrcBtn -> {
                FAV_TYPE = Constant.FAV_SOURCE
                if (isSrcFav) {
                    deleteAddressData(favSrcId)
                } else {
                    if (editSrcLocation.text.toString().isNotEmpty()) showSelectFavDialog()
                }
            }
            R.id.favouriteDestBtn -> {
                FAV_TYPE = Constant.FAV_DESTINATION
                if (isDestFav) {
                    deleteAddressData(favDestId)
                } else {
                    if (editDestLocation.text.toString().isNotEmpty()) showSelectFavDialog()
                }
            }
            R.id.sosBtn -> if (tempDetails != null) showSosConfirmation()
            R.id.cancelRideLt -> {
                showCancellationConfirmation()
            }
            R.id.cancelRequestBtn -> cancelRideRequest("CancelBtn")
            R.id.endRideLt -> confirmationEndRide()
            R.id.paymentLt ->
                startActivity(
                    Intent(
                        this,
                        PaymentOptionActivity::class.java
                    )
                )

            R.id.couponLt -> {
                if (selectedVehicleId.isNullOrEmpty()) {
                    showToast(this, resources.getString(R.string.please_select_vehicle))
                    return
                }
                startActivityForResult(
                    Intent(this, OffersActivity::class.java)
                        .putExtra("SourceLat", startPoint?.latitude.toString())
                        .putExtra("SourceLng", startPoint?.longitude.toString())
                        .putExtra("DestLat", endPoint?.latitude.toString())
                        .putExtra("DestLng", endPoint?.longitude.toString())
                        .putExtra("CouponCode", coupon)
                        .putExtra("VehicleType", selectedVehicleId), Constant.COUPON_CODE
                )
            }
            R.id.callDriverLt -> callDriver()
            R.id.shareDetails -> shareRideDetails()
            R.id.totalFare -> {
            }


            R.id.tvLetsGo -> {
                /** check if ride is active then show the popup of already ride in process */
                if (instance.rideId!=""){
                    showToast(this,getString(R.string.already_have_ride))
                }
                else if (utility.isGpsEnabled) {
                    recentClicked = false
                    val midLatLng =
                        mMap?.cameraPosition?.target // Will return the map location of marker
                    getAddress(midLatLng?.latitude!!, midLatLng.longitude)
                    openLocationSelectOptions(Constant.SEARCH_DESTINATION, "ride")
                    manageVisibilityAndText(View.VISIBLE)
                }
            }
            /** set click on ride button*/
            R.id.tvRide -> {
                if (utility.isGpsEnabled) {
                    recentClicked = false
                    val midLatLng =
                        mMap?.cameraPosition?.target // Will return the map location of marker
                    getAddress(midLatLng?.latitude!!, midLatLng.longitude)
                    openLocationSelectOptions(Constant.SEARCH_DESTINATION, "ride")
                    manageVisibilityAndText(View.VISIBLE)
                }
            }
            /** set click on package delivery*/
            R.id.tvPackage -> {

//                if (utility.isGpsEnabled) {
//                    recentClicked = false
//                    val midLatLng =
//                        mMap?.cameraPosition?.target // Will return the map location of marker
//                    getAddress(midLatLng?.latitude!!, midLatLng.longitude)
//                    openLocationSelectOptions(Constant.SEARCH_DESTINATION,"package")
//                    manageVisibilityAndText(View.VISIBLE)
//                }
//                startActivity(Intent(this,PackageDeliveryActivity::class.java).putExtra("pickupName",startLocAddress).putExtra("pickupLatLng",startPoint)
//                    .putExtra("endLocName",endLocAddress).putExtra("endLatLng",endPoint))
                startActivity(
                    Intent(
                        this,
                        PackageDeliveryActivity::class.java
                    ).putExtra("pickupName", startLocAddress).putExtra("pickupLatLng", startPoint)
                )
//
            }
            R.id.lnPickLayout -> {
                /** check if ride is active then show the popup of already ride in process */
                if (instance.rideId!=""){
                    showToast(this,getString(R.string.already_have_ride))
                }
                else if (utility.isGpsEnabled) {
                    recentClicked = false
                    val midLatLng =
                        mMap?.cameraPosition?.target // Will return the map location of marker
                    getAddress(midLatLng?.latitude!!, midLatLng.longitude)
                    openLocationSelectOptions(Constant.SEARCH_SOURCE, "ride")
                    manageVisibilityAndText(View.VISIBLE)
                }
            }
            R.id.confirmLocation -> {
                toolbarCommon.visibility = View.GONE
                confirmLocation.visibility = View.GONE
                when (SEARCH_TYPE) {
                    Constant.SEARCH_SOURCE -> {
                        tvToolbarLabelMain.text = resources.getString(R.string.pick_up_label)
                        if (this.view != null) {
                            this.view.edtPickupSearch?.setText(startLocAddress)
                            manageVisibilityAndText(View.VISIBLE)
                            SEARCH_TYPE = Constant.SEARCH_DESTINATION
                        }
                        toolbarSearchDialog?.show()
                    }
                    Constant.SEARCH_DESTINATION -> {
                        tvToolbarLabelMain.text = resources.getString(R.string.destination_label)
                        if (this.view != null) {
                            this.view.edtDestSearch?.setText(endLocAddress)
                        }
                        toolbarSearchDialog?.dismiss()
                        if (!rideAccepted) {
                            setLocationOnMap(endPoint)
                        }
                    }
                }
            }
            R.id.imgBackMain -> {
                toolbarSearchDialog?.show()
                toolbarCommon.visibility = View.GONE
                confirmLocation.visibility = View.GONE
                when (SEARCH_TYPE) {
                    Constant.SEARCH_SOURCE -> {
                        startLocName = ""
                        startLocAddress = ""
                    }
                    Constant.SEARCH_DESTINATION -> {
                        endPoint = null
                        endLocName = ""
                        endLocAddress = ""
                        this.view.edtDestSearch?.setText(endLocAddress)
                    }
                }
            }
            R.id.cardDrawer -> {
                openDrawer()
            }
            R.id.lnSavedPlaces -> {
                /** check if ride is active then show the popup of already ride in process */
                if (instance.rideId!=""){
                    showToast(this,getString(R.string.already_have_ride))
                }
                else if (utility.isGpsEnabled) {
                    recentClicked = false
                    selectItem(R.id.nav_saved_places)
                }
//                openLocationSelectOptions(Constant.SEARCH_SOURCE)
            }
            R.id.lnRecentTravel -> {
                /** check if ride is active then show the popup of already ride in process */
                if (instance.rideId!=""){
                    showToast(this,getString(R.string.already_have_ride))
                }
                else if (utility.isGpsEnabled) {
                    if (recentRideId.isNotEmpty()) {
                        getRecentRideListing()
//                        getRecentRideDetail(recentRideId)
                    } else {
                        showToast(this, resources.getString(R.string.no_recent_travel))
                    }
                }
            }
            R.id.imgBackRide -> {

                if (driverDataLayout.visibility == View.GONE && hsvLayout.visibility == View.VISIBLE) {
                    SEARCH_TYPE = Constant.SEARCH_DESTINATION
//                    this.view?.edtDestSearch?.requestFocus()
                }
                onBackPressed()
            }

        }
    }

    private fun openLocationSelectOptions(i: String, s: String) {
        endPoint = null
        fromRideOrPackage = s
        letsGolayout.visibility = View.GONE
        mainToolbarLayout.visibility = View.GONE
        ivLocationPin.visibility = View.GONE
        if (mMap != null) mMap?.uiSettings?.isMyLocationButtonEnabled = false
        SEARCH_TYPE = i
        addressData
    }

    private fun addAddressData(lat: String, lng: String, locName: String?) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.addRiderFavoriteLocations(
            Constant.ADD_FAV_LOCATION, instance.token, locationTypeFav, lat + "", lng + "", locName
                .toString(), this
        )
    }

    private fun callDriver() {
        val callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_REQUEST
            )
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
//            callIntent.data = Uri.parse("tel:$driverMobile")
            callIntent.data = Uri.parse(
                "tel:${
                    instance.getStore(this).getString(Constant.PHONE_CODE)
                }$driverMobile"
            )
            startActivity(callIntent)
        }
    }

    fun checkPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion <= Build.VERSION_CODES.LOLLIPOP) {
            return false
        }
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        val readStoragePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val writeStoragePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (listPermissionsNeeded.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_LOCATION
            )
            return false
        }
        return true
    }

    private fun setMylocationBtn() {

        val view = mapFragment?.view?.findViewById<View?>("1".toInt())
        if (view != null) {
            val locationButton =
                (view.parent as View).findViewById<View>("2".toInt())
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 30, resources.getDimension(R.dimen.size_300).toInt())
            locationButton.visibility = View.VISIBLE
            locationButton.setOnClickListener {

//                /** call location method button here to show current location button while ride is active */
                getCurrentLoc()
            }
        }

    }

    fun getCurrentLoc() {
        val latLng =
            LatLng(mMap?.myLocation?.latitude ?: 0.0, mMap?.myLocation?.longitude ?: 0.0)
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f))
    }

    /**
     * Method requied for getting the updated location from Firebase
     * */
    private fun initalizeFireDb() {
        mFirebaseInstance = FirebaseDatabase.getInstance()
        mFirebaseDatabaseRef = mFirebaseInstance?.getReference(Constant.fireBaseTableName)
        mFirebaseInstance?.getReference("Tracking")?.setValue("Move Marker")
//        mFirebaseDatabaseRef?.child("")?.addValueEventListener(this)

    }

    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.CANCEL_RIDE_CODE) {
            if (resultCode == RESULT_OK) {
                if (mMap != null) mMap?.clear()
                if (mFirebaseDatabaseRef != null) mFirebaseDatabaseRef?.removeEventListener(this)
                if (editDestLocation.text.isEmpty()) {
                    hsvLayout.visibility = View.GONE
                    instance.getStore(this).saveBoolean(Constant.oneTimeCall, true)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }
                hsvLayout.visibility = View.VISIBLE
                imgBackRide.visibility = View.VISIBLE
                searchLayout.visibility = View.VISIBLE
                lnRideData.visibility = View.VISIBLE
                driverDataLayout.visibility = View.GONE
                findingRideLt.visibility = View.GONE
                nearByVehicles
                if (MyFirebaseMessagingService.notificationManager != null) MyFirebaseMessagingService.notificationManager!!.cancelAll()

            }
        } else if (requestCode == Constant.COUPON_CODE) {
            if (resultCode == RESULT_OK) {
                coupon = data?.getStringExtra(Constant.COUPON_CODE_VALUE)
                txt_applyCoupon.text = data?.getStringExtra(Constant.COUPON_MSG)
            }
        } else if (requestCode == Constant.SAVED_PLACE_REQUEST) {
            if (resultCode == RESULT_OK) {
                /** set search type to destination by pardeep sharma*/
                SEARCH_TYPE = Constant.SEARCH_DESTINATION
                val position = data?.getIntExtra(Constant.SAVED_CODE, 0)
                addressData
                letsGolayout.visibility = View.GONE
                mainToolbarLayout.visibility = View.GONE
                ivLocationPin.visibility = View.GONE
                position?.let { onClickPlace(null, true, it) }
//                view?.edtDestSearch?.requestFocus()
            }
        } else if (requestCode == Constant.RECENT_RIDE_CODE) {
            if (resultCode == RESULT_OK && data?.extras != null) {
                val rideId = data.getStringExtra(Constant.RIDE_ID)
                if (!rideId.isNullOrBlank()) getRecentRideDetail(rideId)
            }
        }
    }

    override fun onResume() {
        registerReceiver(receiver, filter)
        registerReceiver(mGpsSwitchStateReceiver, gpsFilter)

        nearByVehicles
        println("onResume***************************************************")
        wallet = instance.getStore(this).getString(Constant.WALLET_STATUS)
        if (wallet.isNotEmpty()) {
            if (wallet.contentEquals("1") && (instance.walletBalance.isNotEmpty() && instance.walletBalance.toDouble() > 1)) {
                wallet = "1"
                setup_txt_payment.text = getString(R.string.wallet)
            } else {
                wallet = "0"
                setup_txt_payment.text = getString(R.string.cash)
            }
        }
        payment_mode = instance.getStore(this).getString(Constant.PAYMENT_METHOD)
        println("onResume***************************************************")
        when (payment_mode) {
            Constant.CARD -> {
                payment_card = instance.getStore(this).getString(Constant.PAYMENT_CARD)
                Utility.setCustomDrawable(
                    setup_txt_payment,
                    R.drawable.bt_ic_card,
                    R.drawable.ic_right_arrow
                )
                Utility.setCustomDrawable(
                    tvPaymentRide,
                    R.drawable.bt_ic_card,
//                    R.drawable.ic_right_arrow
                    0
                )
            }
            Constant.WALLET -> {
                Utility.setCustomDrawable(
                    setup_txt_payment,
                    R.drawable.ic_wallet,
                    R.drawable.ic_right_arrow
                )
                Utility.setCustomDrawable(
                    tvPaymentRide,
                    R.drawable.ic_wallet,
//                    R.drawable.ic_right_arrow
                    0
                )
                wallet = "1"
            }
            else -> {
                payment_mode = Constant.CASH
                Utility.setCustomDrawable(
                    setup_txt_payment,
                    R.drawable.ic_cash_on_delivery,
                    R.drawable.ic_right_arrow
                )
                Utility.setCustomDrawable(
                    tvPaymentRide,
                    R.drawable.ic_cash_on_delivery,
//                    R.drawable.ic_right_arrow
                    0
                )
                wallet = "0"
                payment_card = ""
            }
        }
        setup_txt_payment.text = payment_mode
        tvPaymentRide.text = payment_mode

        super.onResume()
    }

    /* *
     * Network call for getting nearby vehicles after every 20 sec // Can be replaced with Firebase db calling to reduce api overhead
     * */
    private val nearByVehicles: Unit
        get() {
            nearByFetchTimer = Timer()
            nearByFetchTimer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (!isNetworkConnected(this@MapFrontActivity)) {
                        if (nearByFetchTimer != null) nearByFetchTimer?.cancel()
                        return
                    }
                    println("DataHolder.Companion.getInstance().getRideId() = " + instance.rideId)
                    if (instance.rideId == null || instance.rideId.isEmpty()) {
                        Log.e("lat long ", " " + latitude + " " + longitude)
                        if (lastSentLat == latitude.toString() && lastSentLng == longitude.toString()) {
                            return
                        } else {
                            lastSentLat = latitude.toString()
                            lastSentLng = longitude.toString()
                        }
                        getNearByDrivers(latitude.toString() + "", longitude.toString() + "")
                    }
                }
            }, 0, (Constant.CallTime * 1000).toLong())
        }

    override fun onStop() {
        super.onStop()
        if (nearByFetchTimer != null) {
            nearByFetchTimer?.cancel()
            nearByFetchTimer = null
        }
    }

    override fun onPause() {
        super.onPause()
        if (receiver != null) {
            unregisterReceiver(receiver)
        }
        if (mGpsSwitchStateReceiver != null) {
            unregisterReceiver(mGpsSwitchStateReceiver)
        }
        if (nearByFetchTimer != null) {
            nearByFetchTimer?.cancel()
            nearByFetchTimer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (nearByFetchTimer != null) {
            nearByFetchTimer?.cancel()
            nearByFetchTimer = null
        }
        if (mFirebaseDatabaseRef != null) mFirebaseDatabaseRef?.removeEventListener(this)
    }

    /*
    * Method to check the current device theme -> Called once for loading map dark theme
    * */
    private fun isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onMapClick(p0: LatLng) {
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap?.setOnMapClickListener(this)
        mMap?.setOnMyLocationButtonClickListener(this)
        mMap?.uiSettings?.isMapToolbarEnabled = false
        mMap?.uiSettings?.isCompassEnabled = false
        mMap?.uiSettings?.isRotateGesturesEnabled = false
        mMap?.isTrafficEnabled = false
        mMap?.isIndoorEnabled = false
        mMap?.isBuildingsEnabled = false
        if (checkPermission()) {
            mMap?.isMyLocationEnabled = !rideAccepted
        }
        try {
            if (isDarkThemeOn()) {
                mMap?.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json
                    )
                )
            }

        } catch (e: Resources.NotFoundException) {
            Log.e("Style", "Can't find style. Error: ", e)
        }


        if (mMap != null) mMap?.setOnMyLocationChangeListener { location ->
            if (isFirstTime) {
                if (startPoint == null) {
                    println("location 1 =]")
                    startPoint = LatLng(location.latitude, location.longitude)
                }
                longitude = location.longitude
                latitude = location.latitude
                if (!isSrcFilled && utility.isGpsEnabled) {
                    println("location 2 =]")
                    setSourceLoc()
                }
                if (instance.getStore(this).getBoolean(Constant.oneTimeCall, true)) {
                    println("location 3 =]")
                    mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint!!, 15.5f))
                    instance.getStore(this).saveBoolean(Constant.oneTimeCall, false)
                }
            }
        }

        mMap?.setOnCameraIdleListener {
                if (mIsCameraIdle) {
                    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        loadLocation()
                    } else if (!isFirstTime) {
                        loadLocation()
                    }

            }
        }
        if (number == Constant.DEMO_RIDER) {
            setLocationOnMap(ablysoft)
            startPoint = ablysoft
            latitude = ablysoft.latitude
            longitude = ablysoft.longitude
            if (mMap != null) mMap?.isMyLocationEnabled = false
            mMap?.uiSettings?.setAllGesturesEnabled(false)
            endPoint = sector17
            setSourceLoc()

        }
    }

    private var sector17: LatLng = LatLng("30.74134".toDouble(), "76.78217".toDouble())
    private var ablysoft: LatLng = LatLng("30.6809239".toDouble(), "76.7276827".toDouble())
    private fun loadLocation() {
        val midLatLng = mMap?.cameraPosition?.target
        if (SEARCH_TYPE != Constant.SEARCH_EMPTY) {
            if (SEARCH_TYPE == Constant.SEARCH_SOURCE) {
                latitude = midLatLng?.latitude!!
                longitude = midLatLng.longitude
                startPoint = LatLng(midLatLng.latitude, midLatLng.longitude)
                if (!isFavClick) {
                    favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_border)
                }
                isFavClick = false
            } else if (SEARCH_TYPE == Constant.SEARCH_DESTINATION) {
                endPoint = LatLng(midLatLng?.latitude!!, midLatLng.longitude)
                if (!isFavClick) {
                    favouriteDestBtn.setImageResource(R.drawable.ic_favorite_border)
                }
                if (confirmLocation.visibility == View.GONE && instance.rideId.isEmpty()) {
                    showProgress(false)
                    getVehicleType(
                        startPoint?.latitude.toString(),
                        startPoint?.longitude.toString(),
                        endPoint?.latitude.toString(),
                        endPoint?.longitude.toString()
                    )
                }
                isFavClick = false
            }
            setLocation()
            if (instance.getStore(this).getBoolean(Constant.oneTimeCall, true)) {
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint!!, 15.5f))
                instance.getStore(this).saveBoolean(Constant.oneTimeCall, false)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    instance.getStore(this).saveBoolean(Constant.askedPermission, false)
                    initialView()
                } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Genric(this).permissionAlert(Constant.GPS, this)
                }
            }
            CALL_REQUEST -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callDriver()
            }
        }
    }

    private fun getVehicleType(
        srcLat: String,
        srcLng: String,
        destLat: String,
        destLng: String
    ) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        if (isFavClick) showProgress(false)
        currentFocus?.let { utility.hideSoftKeyboard(it) }
        /** set button text to empty*/
        confirmBookBtn.text = getString(R.string.book) + " "
        instance.getVehicleType(
            Constant.GET_VEHICLES,
            srcLat,
            srcLng,
            destLat,
            destLng,
            selectedSeats.toString(),
            this
        )
    }

    override fun onLocationChanged(location: Location) {
        println("MapFrontActivity.onLocationChanged")
        mLastLocation = location
        if (startPoint == null) {
            println(
                " = onLocationChanged " +
                        "**********************************************************"
            )

            startPoint = LatLng(location.latitude, location.longitude)
            println("startPoint = " + startPoint.toString())

        }
    }

    var speedIs1KmMinute: Double = 0.0
    var sendEndLoc: LatLng? = null
    override fun onDataChange(dataSnapshot: DataSnapshot) {
        val user = dataSnapshot.getValue(
            User::class.java
        )
        if (user != null) {
            val currentLtLng = LatLng(user.lattitude, user.longitude)
            println("dataSnapshot lat= [${user.lattitude}]")
            println("dataSnapshot lng= [${user.longitude}]")

            if (currentLtLng.latitude != 0.0 && currentLtLng.longitude != 0.0) {
//                cabLatLngList.add(currentLtLng)
//                showMovingCab()
                if (lastReceivedLatLng == currentLtLng) return else lastReceivedLatLng =
                    currentLtLng
                if (!isDriverMarkerMoving) {
                    val driverMarkerHandler = Handler(Looper.getMainLooper())
                    driverMarkerHandler.post(object : Runnable {
                        //                        var index=0
                        override fun run() {
                            isDriverMarkerMoving = true
//                            if (index < cabLatLngList.size) {
                            sendEndLoc?.let {
                                getDistance(
                                    currentLocation = currentLtLng,
                                    destinationLocation = it
                                )
                            }
                            updateCarLocation(currentLtLng)
                            isDriverMarkerMoving = false

//                                driverMarkerHandler.postDelayed(this, 3000)
//                                ++index
                            println("Showmovingcab 1")

//                                cabLatLngList.clear()

//                            }
                        }
                    })
                    /*  if (lastReceivedLatLng == currentLtLng) return
                      else {
                          try {
                              for (line in polyLineList){
                                  polyLineList.remove(line)
                                  println("getDistance(lastReceivedLatLng,currentLtLng) ${getDistance(lastReceivedLatLng,currentLtLng)}")
                                  if(getDistance(lastReceivedLatLng,currentLtLng)>10) {
                                      lastReceivedLatLng = null
                                      greyPolyLine?.points = polyLineList
                                      blackPolyline?.points = polyLineList
                                      break
                                  }
                              }

                          }catch (e:Exception){
                              e.printStackTrace()
                          }
                          lastReceivedLatLng = currentLtLng
                      }*/
                    val exist = PolyUtil.locationIndexOnEdgeOrPath(
                        currentLtLng,
                        polyLineList,
                        true,
                        false,
                        300.0
                    )
                    println("exist $exist")
                    if (exist == -1) {
//                        drawPathPolyline(currentLtLng,endLocName,endPosition)
                    }

                }

            }

            /*if (rideAccepted) {
                if (previousDriverLoc == null) {
                    animate(currentLtLng, endPoint)
                } else {
                    previousDriverLoc?.let {
                        animate(it, currentLtLng)
                    }
                }
                previousDriverLoc = currentLtLng
            }*/
        }
    }

    fun round(unrounded: Double, precision: Int, roundingMode: Int): Double {
        val bd = BigDecimal(unrounded)
        val rounded: BigDecimal = bd.setScale(precision, roundingMode)
        return rounded.toDouble()
    }

    var speed = 0.0
    private fun expoConvert(value: Double): Double //Got here 6.743240136E7 or something..
    {
        val formatter: DecimalFormat =
            if (value - value.toInt() > 0.0) DecimalFormat("0.00") //Here you can also deal with rounding if you wish..
            else DecimalFormat("0")
        return formatter.format(value).toDouble()
    }

    var ETA = 0L
    private fun getDistance(currentLocation: LatLng?, destinationLocation: LatLng): Double {
        var distance = 0f

        val myLocation = Location("")
        val destLocation = Location("")
        myLocation.latitude = currentLocation?.latitude ?: 0.0
        myLocation.longitude = currentLocation?.longitude ?: 0.0
        destLocation.latitude = destinationLocation.latitude
        destLocation.longitude = destinationLocation.longitude
        distance = myLocation.distanceTo(destLocation) //in meters
        println("distance = [${distance}]")
//        showToast(this,distance.toString() + isDriverMarkerMoving)
//        if(distance<600) isDriverMarkerMoving=true else isDriverMarkerMoving=false
        distance = distance / 1000

        println("distance = [${distance}], speed = [${speed}]")
        if (speed > 0.0) {
            ETA = (Math.round((distance / speed) * 100) / 100)
            println("timeis ${(distance / speed)}")
            println("Time $ETA")
            var hours = ETA / 60
            if (hours < 1)
                estimatedTimeArrival.text = ETA.toString() + " " + getString(R.string.min)
            else
                estimatedTimeArrival.text =
                    hours.toString() + " " + getString(R.string.hour) + " " + (ETA % 60).toString() + " " + getString(
                        R.string.min
                    )

        }
        var timeTemp = (distance / speed)
        if (timeTemp < 0.25) {
            println("Timeis $timeTemp")
            estimatedTimeArrival.text = getString(R.string.arrived)

        }

        /* var ext = " m"
         if (distance > 1000) {
             distance /= 1000//in kilometers
             ext = " Km"
         }
         val value = DecimalFormat("#.00")*/
        return distance.toDouble()
//        return value.format(distance) + ext
    }

    override fun onCancelled(databaseError: DatabaseError) {
        println("databaseError = $databaseError")
    }

    private fun animate(latLng: LatLng, endLoc: LatLng?) {
        this.runOnUiThread {
            onLocationUpdate(latLng, endLoc)
        }
    }

    private fun onLocationUpdate(latLng: LatLng, endLoc: LatLng?) {
        carMarker?.position = latLng
        carMarker?.setAnchor(0.5f, 0.5f)
        if (latLng != endLoc) {

            mMap?.animateCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(latLng)
                        .zoom(13.5f).build()
                )
            )
            carMarker?.rotation = getBearing(latLng, endLoc)
        }
    }

    fun getAddress(latitude: Double, longitude: Double): String {
        println("lat = [$latitude], lng = [$longitude]")
        val geocoder = Geocoder(this, Locale.getDefault())
        var address = ""
        try {
            if (latitude != 0.0 || longitude != 0.0) {
                Log.e("idleee", "here $latitude,$longitude")
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.size > 0) {
                    val obj = addresses[0]
                    if (SEARCH_TYPE === Constant.SEARCH_SOURCE) {
                        startLocAddress = obj.getAddressLine(0)
                        startLocName = obj.getAddressLine(0)
                    } else {
                        endLocAddress = obj.getAddressLine(0)
                        endLocName = obj.getAddressLine(0)
                    }
                    address = obj.getAddressLine(0)
                    println("startLocAddress address = $startLocAddress")
                    println("startLocName address = $startLocName")

                    println("endLocAddress address = $endLocAddress")
                    println("endLocName address = $endLocName")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return address
    }

    /**
     * method used to get nearby drivers from API
     */
    var lastSentLat = String()
    var lastSentLng = String()
    fun getNearByDrivers(lat: String?, lng: String?) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }

        startPoint?.let {
            markerPoints?.add(it)
        }

        if (isSelectedLocManually) {
            instance.getNearByDrivers(
                Constant.GET_NEARBY_DRIVERS,
                startPoint?.latitude.toString(),
                startPoint?.longitude.toString(),
                selectedVehicleId,
                this
            )
        } else {
            instance.getNearByDrivers(
                Constant.GET_NEARBY_DRIVERS, lat.toString(), lng.toString(),
                selectedVehicleId, this
            )
        }
    }

    var placesClient: PlacesClient? = null

    private fun initView() {
        mapFragment = fragmentManager.findFragmentById(R.id.map) as MapFragment

        initialView()
        callDriverLt.setOnClickListener(this)
        paymentLt.setOnClickListener(this)
        couponLt.setOnClickListener(this)
        cancelRideLt.setOnClickListener(this)
        shareDetails.setOnClickListener(this)
        endRideLt.setOnClickListener(this)
        sosBtn.setOnClickListener(this)
        markerPoints = ArrayList()

        imgBackRide.setOnClickListener(this)
        lnRecentTravel.setOnClickListener(this)
        lnSavedPlaces.setOnClickListener(this)
        cardDrawer.setOnClickListener(this)
        confirmLocation.setOnClickListener(this)
        favouriteSrcBtn.setOnClickListener(this)
        favouriteDestBtn.setOnClickListener(this)
        totalFare.setOnClickListener(this)
        cancelRequestBtn.setOnClickListener(this)
        tvLetsGo.setOnClickListener(this)
        imgBackMain.setOnClickListener(this)
        lnPickLayout.setOnClickListener(this)
        tvSrcLocation.isSelected = true
        confirmBookBtn.setOnClickListener(this)
        tvPaymentRide.setOnClickListener(this)
        tvRide.setOnClickListener(this)
        tvPackage.setOnClickListener(this)


        genric = Genric(this, mMap)
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }

        initalizeFireDb()

        // Method to check the permissions
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        if (genric?.servicesAvailable == true) {
            genric?.createLocationRequest(mLocationRequest!!)
            if (!Places.isInitialized()) {
                Places.initialize(
                    applicationContext,
                    getString(R.string.google_maps_key)
                )
            }
            placesClient = Places.createClient(this)

        }
        checkPermission()
    }

    fun cancelRideRequest(requestFrom: String) {
        println("requestFrom = [$requestFrom]")
        requestCancelFrom = requestFrom
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        if (instance.rideRequestId.isNotEmpty())
            instance.cancelRideRequest(
                Constant.CANCEL_RIDE_REQUEST,
                instance.rideRequestId,
                "0",
                instance.token,
                this
            )
    }

    private fun getRideDetail(rideId: String?) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.rideDetail(Constant.RIDE_DETAIL, rideId.toString(), instance.token, this)
    }

    private fun getRecentRideDetail(rideId: String?) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.rideDetail(Constant.RECENT_RIDE_DETAIL, rideId.toString(), instance.token, this)
    }

    private fun getRecentRideListing() {
        val intent = Intent(
            this,
            RideListingActivity::class.java
        ).putExtra(Constant.IS_RECENT_RIDE, true)
        startActivityForResult(intent, Constant.RECENT_RIDE_CODE)
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
    }


    private fun addRideRequest() {
        try {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            if (number == Constant.DEMO_RIDER) {
                endPoint = sector17
                startPoint = ablysoft
                startLocAddress = "Unnamed Road, Sector 67, Punjab 160062, India"
                endLocAddress = "Sector 17, Chandigarh, India"
            }

            showProgress(false)
            if (isSelectedLocManually) {
                instance.addRideRequest(
                    Constant.ADD_RIDE_REQUEST,
                    startPoint?.latitude.toString(),
                    startPoint?.longitude.toString(),
                    endPoint?.latitude.toString(),
                    endPoint?.longitude.toString(),
                    selectedVehicleId.toString(),
                    startLocAddress.toString(),
                    endLocAddress.toString(),
                    payment_card.toString(),
                    payment_mode.toString(),
                    wallet,
                    coupon.toString(),
                    selectedSeats.toString(),
                    instance.token,
                    this
                )
            } else {
                instance.addRideRequest(
                    Constant.ADD_RIDE_REQUEST,
                    latitude.toString(),
                    longitude.toString(),
                    endPoint?.latitude.toString(),
                    endPoint?.longitude.toString(),
                    selectedVehicleId.toString(),
                    startLocAddress.toString(),
                    endLocAddress.toString(),
                    payment_card.toString(),
                    payment_mode.toString(),
                    wallet,
                    coupon.toString(),
                    selectedSeats.toString(),
                    instance.token,
                    this
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun initialView() {
        // if (utility.isGpsEnabled()) {
        mapFragment?.getMapAsync(this)
    }

    private fun setRideRespondTime(startTimer: Int) {
        rideCancelTimer = object : CountDownTimer((startTimer * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //val i = millisUntilFinished.toInt() / 1000
            }

            override fun onFinish() {
                cancelRideRequest("Timer")
            }
        }
        rideCancelTimer?.start()
    }

    private var handler = Handler()
    private lateinit var runnable: Runnable
    var cabLatLngList = ArrayList<LatLng>()
    var lastReceivedLatLng: LatLng? = null

    internal var isDriverMarkerMoving = false

    private var previousLatLng: LatLng? = null
    private var currentLatLng: LatLng? = null

    private fun updateCarLocation(latLng: LatLng) {
        println("updateCarLocation called")
        if (previousLatLng == null) {
            currentLatLng = latLng
            previousLatLng = currentLatLng
//            carMarker?.position = currentLatLng as LatLng
            carMarker?.setAnchor(0.5f, 0.5f)
//            animateCamera(currentLatLng!!)
        } else {
            previousLatLng = currentLatLng
            currentLatLng = latLng
            val valueAnimator = AnimationUtils.carAnimator()

            valueAnimator.addUpdateListener { va ->
                if (currentLatLng != null && previousLatLng != null) {
                    val multiplier = va.animatedFraction
                    println("multiplier = [${multiplier}]")
                    if (multiplier >= 0.2) {
                        val nextLocation = LatLng(
                            multiplier * currentLatLng!!.latitude + (1 - multiplier) * previousLatLng!!.latitude,
                            multiplier * currentLatLng!!.longitude + (1 - multiplier) * previousLatLng!!.longitude
                        )
                        carMarker?.position = nextLocation
                        val rotation = getRotation(previousLatLng!!, nextLocation)
                        if (!rotation.isNaN()) {
                            carMarker?.rotation = rotation
                        }
                        carMarker?.setAnchor(0.5f, 0.5f)
                        /**
                         * Commented below line to restrict map to zoom every time when new lat long received
                         */
//                        animateCamera(nextLocation)

                    }
                }
            }

            valueAnimator.start()
        }

    }

    var polyLineList: MutableList<LatLng> = ArrayList()
    var totalDistance = ""

    //this will be commented for resolving issues
    private fun drawPathPolyline(startLoc: LatLng?, endLocation: String?, endLclatlng: LatLng?) {
        sendEndLoc = endLclatlng
        if (mMap != null) mMap?.clear()
        if (nearByFetchTimer != null) nearByFetchTimer?.cancel()
        polyLineList.clear()
        var polylineOptions: PolylineOptions? = null
        var blackPolylineOptions: PolylineOptions? = null
        println("startLoc = [$startLoc], endLocation = [$endLocation], endLclatlng = [$endLclatlng]")
        try {
            val requestUrl = ("https://maps.googleapis.com/maps/api/directions/json?" +   //walking
                    "mode=driving&"
                    + "transit_routing_preference=less_driving&"
                    + "origin=" + startLoc?.latitude + "," + startLoc?.longitude + "&"
                    + "destination=" + endLclatlng?.latitude + "," + endLclatlng?.longitude +
                    "&key=" + getString(R.string.google_maps_key))
            Log.d("TAG", requestUrl)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                requestUrl, null,
                { response ->
                    Log.d("TAG", response.toString() + "")
                    try {
                        var jDuration: JSONObject
                        var jDistance: JSONObject
                        val jsonArray = response.getJSONArray("routes")
                        for (i in 0 until jsonArray.length()) {
                            val route = jsonArray.getJSONObject(i)
                            val jLegs = (jsonArray[i] as JSONObject).getJSONArray("legs")
                            for (j in 0 until jLegs.length()) {
                                jDuration = (jLegs[j] as JSONObject).getJSONObject("duration")
                                jDistance = (jLegs[j] as JSONObject).getJSONObject("distance")
                                totalDistance = jDistance.getString("value")
                                estimatedTimeArrival.text = jDuration.getString("text")
                                var timevalue = jDuration.getString("value").toInt() / 60
                                estimatedTime = timevalue.toString()

                            }

                            val poly = route.getJSONObject("overview_polyline")
                            val polyline = poly.getString("points")
                            println("polyline = $polyline")
                            polyLineList = decodePoly(polyline)
                        }
                        speed = 0.0
                        try {
                            speed = (totalDistance.toDouble() / 1000) / estimatedTime.toDouble()
                            speed = expoConvert(speed)
                            println("totalDistance = [${totalDistance}] estimatedTime= $estimatedTime Speed $speed")
                        } catch (e: Exception) {
                            println("exception ->${e.localizedMessage}")
                            e.printStackTrace()
                        }
//                        sendEndLoc?.let { getDistance(startLoc, it) }

                        val builder = LatLngBounds.Builder()
                        for (latLng in polyLineList) {
                            builder.include(latLng)
                        }
                        val bounds = builder.build()
                        val mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2)
                        mMap?.animateCamera(mCameraUpdate)
                        polylineOptions = PolylineOptions()
                        polylineOptions?.color(Color.GRAY)
                        polylineOptions?.width(8f)
                        polylineOptions?.startCap(SquareCap())
                        polylineOptions?.endCap(SquareCap())
                        polylineOptions?.jointType(JointType.ROUND)
                        polylineOptions?.addAll(polyLineList)
                        greyPolyLine = mMap?.addPolyline(polylineOptions!!)
                        blackPolylineOptions = PolylineOptions()
                        blackPolylineOptions?.width(8f)
                        blackPolylineOptions?.color(Color.BLACK)
                        blackPolylineOptions?.startCap(SquareCap())
                        blackPolylineOptions?.endCap(SquareCap())
                        blackPolylineOptions?.jointType(JointType.ROUND)
                        blackPolyline = mMap?.addPolyline(blackPolylineOptions!!)

                        val polylineAnimator = ValueAnimator.ofInt(0, 100)
                        polylineAnimator.duration = 4000
                        polylineAnimator.repeatCount = 3
                        polylineAnimator.interpolator = LinearInterpolator()
                        polylineAnimator.addUpdateListener { valueAnimator ->
                            try {
                                val points = greyPolyLine?.points
                                val percentValue = valueAnimator.animatedValue as Int
                                val size = points?.size
                                val newPoints = (size?.times((percentValue / 100.0f)))!!.toInt()
                                val p: List<LatLng> = points.subList(0, newPoints)
                                blackPolyline?.points = p
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                            }
                        }
                        polylineAnimator.start()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        /** set marker according to vehicle types*/
                        val drawableIcon: Int
                        when (vehicleType) {
                            Constant.AUTO -> {
                                drawableIcon = R.drawable.ic_auto
                            }
                            Constant.TAXI -> {
                                drawableIcon = R.drawable.ic_cab
                            }
                            else -> drawableIcon = R.drawable.ic_bike
                        }

                        val bitmap =
                            AppCompatResources.getDrawable(this, drawableIcon)?.toBitmap()
                        val smallMarker = Bitmap.createScaledBitmap(
                            bitmap!!,
                            38,
                            65,
                            false
                        )
                        carMarker = mMap?.addMarker(
                            MarkerOptions().position(startLoc!!)
                                .flat(true).draggable(true)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        )
                        var destinationMarker =
                            AppCompatResources.getDrawable(this, R.drawable.ic_location)?.toBitmap()

//                        mMap?.addMarker(
//                            MarkerOptions().icon(
//                                destinationMarker?.let { BitmapDescriptorFactory.fromBitmap(it) }
//                            ).position(endLclatlng!!)
//                        )
                        Glide.with(this).asBitmap().load(driverimage).circleCrop()
                            .into(object : SimpleTarget<Bitmap?>() {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap?>?
                                ) {
                                    if (resource != null) {
                                        val img_res: ImageView =
                                            viewmarker?.findViewById(R.id.markerImage)!!
                                        img_res.setImageBitmap(resource)

                                        carMarker = mMap?.addMarker(
                                            MarkerOptions().position(startLoc!!).icon(
                                                BitmapDescriptorFactory.fromBitmap(getViewBitmap()!!)
                                            )
                                        )
                                    }
                                }
                            })
                    }

                }) { error -> Log.d("TAG", error.toString() + "It is ") }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(jsonObjectRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            println("error if = " + e.message)
        }
    }
    fun getViewBitmap(): Bitmap? {
        val view = viewmarker
        view?.layout(0, 0, 100, 100)
        //Get the dimensions of the view so we can re-layout the view at its current size
        //and create a bitmap of the same size
        val width = view?.width
        val height = view?.height
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width!!, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height!!, View.MeasureSpec.EXACTLY)

        //Cause the view to re-layout
        view.measure(measuredWidth, measuredHeight)
        view.layout(0, 0, view?.measuredWidth, view.measuredHeight)

        //Create a bitmap backed Canvas to draw the view into
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
        view.draw(c)
        return b
    }

    private fun decodePoly(encoded: String): ArrayList<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

    @SuppressLint("InflateParams")
    private fun loadToolBarSearch() {
        mMap?.uiSettings?.setAllGesturesEnabled(false) // Disable map click , gestures -> set(true) to enable
        view = layoutInflater.inflate(R.layout.view_toolbar_search, null)
        view.list_favourites?.visibility = View.VISIBLE
        view.list_search?.visibility = View.VISIBLE
        if (SEARCH_TYPE == Constant.SEARCH_SOURCE) {
            view.tvToolbarLabel?.text = resources.getString(R.string.pick_up_label)
        } else if (SEARCH_TYPE == Constant.SEARCH_DESTINATION) {
            view.tvToolbarLabel?.text = resources.getString(R.string.destination_label)
        }
        try {
            if (locationData.isNotEmpty()) {
                view.lnSavedPlace?.visibility = View.VISIBLE
                searchAdapter = FavLocationSearchAdapter(this, locationData, this, this)
                view.list_favourites?.adapter = searchAdapter
            } else {
                view.lnSavedPlace?.visibility = View.INVISIBLE
            }
            dataAdapter = GooglePlacesAutocompleteAdapter(
                this,
                R.layout.adapter_google_places_autocomplete,
                this,
                placesClient!!
            )
            view.list_search?.adapter = dataAdapter
            view.list_search?.isTextFilterEnabled = true
            toolbarSearchDialog = Dialog(this, R.style.MaterialSearch)
            toolbarSearchDialog?.setContentView(view)
            toolbarSearchDialog?.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            toolbarSearchDialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            toolbarSearchDialog?.show()
            toolbarSearchDialog?.setCancelable(false)
            toolbarSearchDialog?.setCanceledOnTouchOutside(false)

            view.edtPickupSearch?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if (locationData != null) {
                        if (s.toString().isEmpty()) {
                            onFind(true, 1)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (SEARCH_TYPE == Constant.SEARCH_SOURCE && view.edtPickupSearch?.hasFocus() == true) {
                        if (s.toString().isNotEmpty()) {
                            view.srcDelBtn?.visibility = View.VISIBLE
                            view.list_search?.visibility = View.VISIBLE
                        } else {
                            view.srcDelBtn?.visibility = View.GONE
                            view.list_search?.visibility = View.GONE
                        }
                        if (locationData != null) {
                            searchAdapter?.filter?.filter(s.toString())
                        }
                        dataAdapter?.filter?.filter(s.toString())
                    } else {
                        dataAdapter?.filter?.filter("")
                    }
                }
            })

            view.edtDestSearch?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if (locationData != null) {
                        if (s.toString().isEmpty()) {
                            onFind(true, 1)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (SEARCH_TYPE == Constant.SEARCH_DESTINATION && view.edtDestSearch?.hasFocus() == true) {
                        if (s.toString().isNotEmpty()) {
                            view.destDelBtn?.visibility = View.VISIBLE
                            view.list_search?.visibility = View.VISIBLE
                        } else {
                            view.destDelBtn?.visibility = View.GONE
                            view.list_search?.visibility = View.GONE
                        }
                        if (locationData != null) {
                            searchAdapter?.filter?.filter(s.toString())
                        }
                        dataAdapter?.filter?.filter(s.toString())
                    } else {
                        dataAdapter?.filter?.filter("")
                    }
                }
            })

            // Delete button on pick-up edit-text
            view.srcDelBtn?.setOnClickListener {
                view.edtPickupSearch?.setText("")
            }

            // Delete button on drop edit-text
            view.destDelBtn?.setOnClickListener {
                view.edtDestSearch?.setText("")
            }


            view.edtPickupSearch?.onFocusChangeListener =
                View.OnFocusChangeListener { _, p1 ->
                    if (p1) {
                        view.list_search?.visibility = View.VISIBLE
                        if (view.edtPickupSearch?.text.toString().isNotEmpty()) {
                            view.srcDelBtn?.visibility = View.VISIBLE
                        } else {
                            view.srcDelBtn?.visibility = View.GONE
                        }
                        if (SEARCH_TYPE == Constant.SEARCH_DESTINATION) {
                            view.img_tool_back?.performClick()
                            view.edtPickupSearch?.text = view.edtPickupSearch?.text
                        }
                    } else {
                        view.srcDelBtn?.visibility = View.GONE
                        view.list_search?.visibility = View.GONE
                    }
                }
            view.edtDestSearch?.onFocusChangeListener =
                View.OnFocusChangeListener { _, p1 ->
                    if (p1) {
                        if (view.edtDestSearch?.text.toString().isEmpty()) {
                            dataAdapter?.filter?.filter("")
                            view.list_search?.visibility = View.GONE
                        } else {
                            view.list_search?.visibility = View.VISIBLE
                        }
                        if (view.edtDestSearch?.text.toString().isNotEmpty()) {
                            view.destDelBtn?.visibility = View.VISIBLE
                        } else {
                            view.destDelBtn?.visibility = View.GONE
                        }
                    } else {
                        view.destDelBtn?.visibility = View.GONE
                        view.list_search?.visibility = View.GONE
                    }
                }

            // Will be displayed only in case of Pick-Up location
            view.lnCurentLoc?.setOnClickListener {
                try {
                    when (SEARCH_TYPE) {
                        Constant.SEARCH_SOURCE -> {
                            val midLatLng = mMap?.myLocation
                            view.edtPickupSearch?.setText(
                                getAddress(
                                    midLatLng?.latitude!!,
                                    midLatLng.longitude
                                )
                            )
                            setLocation()
                            view.tvToolbarLabel?.text =
                                resources.getString(R.string.destination_label)
//                            view?.edtDestSearch?.requestFocus()
                            manageVisibilityAndText(View.VISIBLE)
                            SEARCH_TYPE = Constant.SEARCH_DESTINATION
                            tvToolbarLabelMain.text =
                                resources.getString(R.string.destination_label)
                            toolbarSearchDialog?.show()
                        }
                        Constant.SEARCH_DESTINATION -> {
                            view.tvToolbarLabel?.text =
                                resources.getString(R.string.destination_label)
                            if (endPoint == null) {
                                val midLatLng = mMap?.cameraPosition?.target
                                endPoint = LatLng(midLatLng?.latitude!!, midLatLng.longitude)
                                getAddress(endPoint?.latitude!!, endPoint?.longitude!!)
                            }
                            view.edtDestSearch?.setText(endLocAddress)
                            editDestLocation.setText(endLocAddress)
                            editDestLocation.clearFocus()
                            view.edtDestSearch?.clearFocus()
                            view.edtPickupSearch?.clearFocus()
                            loadLocation()
                            toolbarSearchDialog?.dismiss()
                        }
                    }
                } catch (ex: Exception) {
                }
            }

            view.tvChooseMap?.setOnClickListener {

                try {
                    mMap?.uiSettings?.setAllGesturesEnabled(true)
                    view.edtPickupSearch?.let { it1 -> utility.hideSoftKeyboard(it1) }
                    toolbarSearchDialog?.dismiss()
                    toolbarCommon.visibility = View.VISIBLE
                    confirmLocation.visibility = View.VISIBLE
                    when (SEARCH_TYPE) {
                        Constant.SEARCH_SOURCE -> {
                            tvToolbarLabelMain.text = resources.getString(R.string.pick_up_label)
                            val midLatLng = mMap?.cameraPosition?.target
                            startPoint = midLatLng
                            setLocation()
                        }
                        Constant.SEARCH_DESTINATION -> {
                            val midLatLng = mMap?.cameraPosition?.target
                            endPoint = midLatLng
                            setLocation()
                            tvToolbarLabelMain.text =
                                resources.getString(R.string.destination_label)
                        }
                    }
//                    edtLocate.requestFocus()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            view.parent_toolbar_search?.setOnClickListener {
                view.edtPickupSearch?.let { it1 -> utility.hideSoftKeyboard(it1) }
                toolbarSearchDialog?.dismiss()
            }
            view.img_tool_back?.setOnClickListener {
                try {
                    mMap?.uiSettings?.setAllGesturesEnabled(true)
                    view.list_search?.visibility = View.GONE
                    //view.list_favourites.visibility = View.GONE
                    selectedVehicleId = ""
                    when (SEARCH_TYPE) {
                        Constant.SEARCH_SOURCE -> {
                            letsGolayout.visibility = View.VISIBLE
                            mainToolbarLayout.visibility = View.VISIBLE
                            toolbarCommon.visibility = View.GONE
                            confirmLocation.visibility = View.GONE
                            utility.hideSoftKeyboard(view.img_tool_back)
                            view.list_search?.visibility = View.GONE
                            toolbarSearchDialog?.dismiss()
                        }
                        Constant.SEARCH_DESTINATION -> {
                            letsGolayout.visibility = View.GONE
                            mainToolbarLayout.visibility = View.GONE
                            toolbarCommon.visibility = View.GONE
                            confirmLocation.visibility = View.GONE
                            utility.hideSoftKeyboard(view.img_tool_back)
                            manageVisibilityAndText(View.GONE)
                            endPoint = null
                            endLocAddress = ""
                            endLocName = ""
                            view.edtDestSearch?.setText(endLocAddress)
                            SEARCH_TYPE = Constant.SEARCH_SOURCE
                        }
                    }
                } catch (ex: Exception) {
                }
            }
            toolbarSearchDialog?.setOnDismissListener {
                /** hide mylocation button in case of active ride */
                if (instance.rideId == "") {
                    setMylocationBtn()
                    ivLocationPin.visibility = View.VISIBLE
                    this.currentFocus?.let { it1 -> utility.hideSoftKeyboard(it1) }
                }
            }
            toolbarSearchDialog?.setOnKeyListener { _: DialogInterface?, keyCode: Int, action: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK && action?.action == KeyEvent.ACTION_UP) {
                    view.img_tool_back?.performClick()
                    return@setOnKeyListener true
                }
                false
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onFind(find: Boolean, results: Int) {
        try {
            if (find && results > 0) {
                view.list_favourites?.visibility = View.VISIBLE
            } else if (!find && results < 0) {
                view.list_favourites?.visibility = View.GONE
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onClickPlace(placeId: String?, isFavourite: Boolean, position: Int) {
        println("SEARCH_TYPE = $SEARCH_TYPE isFavourite $isFavourite")
        this.view.edtPickupSearch?.clearFocus()
        this.view.edtDestSearch?.clearFocus()
        utility.hideKeyboard()
        txt_applyCoupon.text = getString(R.string.apply_coupon)
        mMap?.uiSettings?.setAllGesturesEnabled(false)
        isFavClick = true
        if (!isFavourite) {
            val placeFields =
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            var request: FetchPlaceRequest? = null
            if (placeId != null) {
                request = FetchPlaceRequest.builder(placeId, placeFields)
                    .build()
            }
            if (request != null) {
                placesClient?.fetchPlace(request)?.addOnSuccessListener { task ->
                    if (SEARCH_TYPE == Constant.SEARCH_SOURCE) {
                        try {
                            if (view != null) {
                                view.edtPickupSearch?.setText(task.place.name)
                                view.list_search?.visibility = View.GONE
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                        isSrcFav = false
                        favSrcId = ""
                        favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_border)
                        isSelectedLocManually = true
                        editSrcLocation.setText(task.place.name)
                        tvSrcLocation.text = task.place.name
                        isSrcFilled = true
                        startLocAddress = task.place.address
                        startLocName = task.place.name
                        startPoint = task.place.latLng
                        if (endPoint == null) getNearByDrivers(
                            startPoint?.latitude.toString(),
                            startPoint?.latitude.toString()
                        ) else {
                            getVehicleType(

                                startPoint?.latitude.toString(),
                                startPoint?.longitude.toString(),
                                endPoint?.latitude.toString(),
                                endPoint?.longitude.toString()
                            )
                        }
                        manageVisibilityAndText(View.VISIBLE)
                    } else if (SEARCH_TYPE == Constant.SEARCH_DESTINATION) {
                        isDestFav = false
                        favDestId = ""
                        favouriteDestBtn.setImageResource(R.drawable.ic_favorite_border)
                        editDestLocation.setText(task.place.name)
                        view.edtDestSearch?.setText(task.place.name)
                        endLocName = task.place.name.toString()
                        endLocAddress = task.place.address.toString()
                        println("end loc address $endLocAddress")
                        endPoint = task.place.latLng
                        if (toolbarSearchDialog != null && toolbarSearchDialog?.isShowing == true) toolbarSearchDialog?.dismiss()

                        /** check if we are from package , then we have to go on else condition*/

                        if (startPoint != null && endPoint != null) {
//                            if (fromRideOrPackage == "package"){
//                                startActivity(Intent(this,PackageDeliveryActivity::class.java).putExtra("pickupName",startLocAddress).putExtra("pickupLatLng",startPoint)
//                                    .putExtra("endLocName",endLocAddress).putExtra("endLatLng",endPoint))
//                            }else{
                            getVehicleType(
                                startPoint?.latitude.toString(),
                                startPoint?.longitude.toString(),
                                endPoint?.latitude.toString(),
                                endPoint?.longitude.toString()
                            )
                        }
//                            setLocationOnMap(endPoint)
                        //  }

                    }
                }?.addOnFailureListener { e -> e.printStackTrace() }
            }

        } else if (SEARCH_TYPE == Constant.SEARCH_SOURCE) {
            isFavClick = true
            isSrcFav = true
            favSrcId = locationData[position].id
            favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_filled)
            isSelectedLocManually = true
            isSrcFilled = true
            startLocAddress = locationData[position].google_loc_name
            startLocName = locationData[position].google_loc_name
            startPoint = LatLng(
                locationData[position].latitude?.toDouble() ?: 0.0,
                locationData[position].longitude?.toDouble() ?: 0.0
            )
            editSrcLocation.setText(startLocAddress)
            tvSrcLocation.text = startLocAddress
            manageVisibilityAndText(View.VISIBLE)
            if (endPoint == null) getNearByDrivers(
                startPoint?.latitude.toString(),
                startPoint?.latitude.toString()
            ) else {
                if (startPoint != null && endPoint != null) getVehicleType(
                    startPoint?.latitude.toString(),
                    startPoint?.longitude.toString(),
                    endPoint?.latitude.toString(),
                    endPoint?.longitude.toString()
                )
            }
        } else if (SEARCH_TYPE == Constant.SEARCH_DESTINATION) {
            isFavClick = true
            isDestFav = true
            favDestId = locationData[position].id
            favouriteDestBtn.setImageResource(R.drawable.ic_favorite_filled)
            endLocName = locationData[position].google_loc_name
            endLocAddress = locationData[position].google_loc_name
            endPoint = LatLng(
                locationData[position].latitude?.toDouble() ?: 0.0,
                locationData[position].longitude?.toDouble() ?: 0.0
            )
            editDestLocation.setText(endLocAddress)
            view.edtDestSearch?.setText(endLocAddress)
            view.edtDestSearch?.clearFocus()
            view.edtPickupSearch?.clearFocus()
            if (toolbarSearchDialog != null && toolbarSearchDialog?.isShowing == true) toolbarSearchDialog?.dismiss()
            if (startPoint != null && endPoint != null)
                getVehicleType(
                    startPoint?.latitude.toString(),
                    startPoint?.longitude.toString(),
                    endPoint?.latitude.toString(),
                    endPoint?.longitude.toString()
                )

        }
    }

    private fun setLocationOnMap(latLng: LatLng?) {
        println("latLng setLocationOnMap = $latLng")
        if (mMap != null) {
            if (SEARCH_TYPE === Constant.SEARCH_SOURCE) {
                startPoint = latLng
                mMap?.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(startPoint!!).zoom(15.5f).build()
                    )
                )
            } else {
                endPoint = latLng
                mMap?.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder().target(endPoint!!).zoom(15.5f).build()
                    )
                )
            }
        }
    }

    private fun deleteAddressData(id: String?) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        instance.deleteFavoriteLocation(
            Constant.DELETE_FAV_LOCATION, instance.token, id,
            object : ResponseHandler {
                override fun onSuccess(tag: Int, response: Response<*>) {
                    if (tag == Constant.DELETE_FAV_LOCATION) {
                        checkFavSrcDestAddress(id)
                        var arrayList = ArrayList<Locations>()
                        val common = response.body() as Common?
                        if (common != null) {
                            if (common.status.contentEquals("1")) {
                                val type = object : TypeToken<List<Locations?>?>() {}.type
                                val gson = Gson()
                                val data = instance.getStore(this@MapFrontActivity)
                                    .getString(Constant.FAV_LOCATION)
                                if (data != "") {
                                    arrayList = gson.fromJson(data, type)
                                    for (i in arrayList.indices) {
                                        if (arrayList[i].id == id) {
                                            arrayList.removeAt(i)
                                            break
                                        }
                                    }
                                }
                                showToast(this@MapFrontActivity, common.msg.toString())
                                val list = gson.toJson(arrayList)
                                instance.getStore(this@MapFrontActivity)
                                    .saveString(Constant.FAV_LOCATION, list)
                            } else if (common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                                Utility(this@MapFrontActivity).sessionExpire(this@MapFrontActivity)
                            } else {
                                showToast(this@MapFrontActivity, common.msg.toString())
                            }
                        } else showToast(
                            this@MapFrontActivity,
                            this@MapFrontActivity.getString(R.string.error_something_wrong)
                        )
                    }
                }

                override fun onError(throwable: Throwable) {}
            })
    }

    fun checkFavSrcDestAddress(id: String?) {
        if (id == favSrcId) {
            isSrcFav = false
            favouriteSrcBtn.setImageResource(R.drawable.ic_favorite_border)
        }
        if (id == favDestId) {
            isDestFav = false
            favouriteDestBtn.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    companion object {
        const val REQUEST_LOCATION = 0
        private const val CALL_REQUEST = 1
        private var nearByFetchTimer: Timer? = null

    }

    /**
     * Method used for hiding and showing the view of the toolbar-search dialog
     * */
    private fun manageVisibilityAndText(check: Int) {

        this.view.edtDestSearch?.visibility = check
        this.view.srcDelBtn?.visibility = View.GONE
        this.view.destDelBtn?.visibility = View.GONE
        this.view.verticalLine?.visibility = check

        //Return the search type based on the visibility of views
        SEARCH_TYPE = if (check == View.VISIBLE) {
            this.view.edtPickupSearch?.setText(startLocAddress)
            this.view.tvToolbarLabel?.text = resources.getString(R.string.destination_label)
            //this.view?.edt_tool_search?.isEnabled = false
            this.view.edtPickupSearch?.clearFocus()
//            this.view.edtDestSearch?.requestFocus()
            this.view.lnCurentLoc?.visibility = View.GONE

            dataAdapter?.filter?.filter("")
            searchAdapter?.filter?.filter("")
            endPoint = null
            endLocAddress = ""
            endLocName = ""
            val padding = this.resources.getDimension(R.dimen.margin_15)
            setIcon(
                this.resources.getDimension(R.dimen.margin_10).toInt(),
                padding.toInt(),
                R.drawable.round_10
            )
            Constant.SEARCH_DESTINATION
        } else {
            this.view.edtDestSearch?.setText(endLocAddress)
            this.view.tvToolbarLabel?.text = resources.getString(R.string.pick_up_label)
            this.view.edtDestSearch?.clearFocus()
            this.view.edtPickupSearch?.requestFocus()
            this.view.lnCurentLoc?.visibility = View.VISIBLE
            dataAdapter?.filter?.filter("")
            searchAdapter?.filter?.filter("")
            val padding = this.resources.getDimension(R.dimen.margin_15)
            setIcon(padding.toInt(), padding.toInt(), R.drawable.round_5)
            Constant.SEARCH_SOURCE
        }
    }

    private fun setVehiclesAdapter(vehicles: Vehicles?) {

        mIsCameraIdle = false
        mMap?.uiSettings?.setAllGesturesEnabled(false)
        mMap?.uiSettings?.isZoomGesturesEnabled = true
        mMap?.uiSettings?.isScrollGesturesEnabled = true
        val finishParams = vehicles?.vehiclesTypes
        finishParams?.get(0)?.isChecked = true
        selectedVehicleId = finishParams?.get(0)?.id
        selectedFinalFare =
            vehicles?.currencySymbol + finishParams?.get(0)?.vehicleTypeEstimateArr?.finalFare
        cabType = finishParams?.get(0)?.name

        vehicleAdapter = DisplayVehiclesAdapter(finishParams, this, vehicles?.currencySymbol, this)
        recyclerVehicles.adapter = vehicleAdapter

        /**
         * @author Pardeep Sharma
         * set api for first time for vehicle details to get the availabiltity info on th every first time
         */
        getNearByDrivers(latitude.toString() + "", longitude.toString() + "")
        if (vehicles?.vehiclesTypes?.isNotEmpty() == true) {
            hsvLayout.visibility = View.VISIBLE
            imgBackRide.visibility = View.VISIBLE
            letsGolayout.visibility = View.GONE
            mainToolbarLayout.visibility = View.GONE
        } else {
            hsvLayout.visibility = View.GONE
            imgBackRide.visibility = View.GONE
        }
//        utility.hideKeyboard()
        currentFocus?.let { utility.hideSoftKeyboard(it) }

        /**
         * set click of info image
         */
        vehicleAdapter?.onInfoClick = { currency, data ->
            showDetailDialog(currency!!, data)
        }
    }

    /**
     * @author Pardeep Sharma
     * show bottomsheet when click on eye button
     */
    private fun showDetailDialog(currency: String, data: VehiclesTypes) {
        val view: View = layoutInflater.inflate(R.layout.dialog_split_fare_detail, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        view.tvSplitBaseFare.text = currency + data.vehicleTypeEstimateArr?.finalFare
        view.valueBasePrice.text = currency + data.vehicleTypeEstimateArr?.baseFare
        view.valueRatePerKm.text = currency + data.vehicleTypeEstimateArr?.farePerKm
        view.valueRidePerMin.text = currency + data.vehicleTypeEstimateArr?.farePerMin
        view.carSplitType.text = data.name
        view.carTypeSplitDesc.text = data.details
        Utility.showImage(this, view.carSplitImage, data.icon)
        bottomSheetDialog?.show()
    }

    /**
     * Dialogs used in the process
     * */

    @SuppressLint("InflateParams")
    private fun confirmationEndRide() {
        val view: View = layoutInflater.inflate(R.layout.dialog_end_ride, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        view.yesBtn.setOnClickListener {
            bottomSheetDialog?.dismiss()
            endRide()
        }
        view.noBtn.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
        bottomSheetDialog?.show()
    }

    @SuppressLint("InflateParams")
    private fun showSosConfirmation() {
        val view: View = layoutInflater.inflate(R.layout.dialog_sos, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        view.tvSos.onSlideCompleteListener = this
        bottomSheetDialog?.show()
    }

    @SuppressLint("InflateParams")
    private fun showCancellationConfirmation() {
        startActivityForResult(
            Intent(this, CancellationRideActivity::class.java).putExtra(
                Constant.CANCEL_CHARGE, cancelCharge
            ).putExtra(Constant.CANCEL_TEXT, cancelText), Constant.CANCEL_RIDE_CODE
        )
    }

    @SuppressLint("InflateParams")
    private fun showSelectFavDialog() {
        val view: View = layoutInflater.inflate(R.layout.selected_address_view, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        locationTypeFav = ""
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                this.resources.getColor(R.color.black),
                this.resources.getColor(R.color.green_color)
            )
        )
        view.radioHome.buttonTintList = colorStateList
        view.radioWork.buttonTintList = colorStateList
        view.radioOther.buttonTintList = colorStateList
        view.radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            @SuppressLint("ResourceType")
            override fun onCheckedChanged(radioGroup: RadioGroup, checkedId: Int) {
                val rb = radioGroup.findViewById<RadioButton>(checkedId)
                if (null != rb && checkedId > -1) {
                    locationTypeFav = rb.text.toString()
                    if (locationTypeFav.equals(getString(R.string.other), ignoreCase = true)) {
                        run {
                            view.edtOtherType.visibility = View.VISIBLE
                            view.edtOtherType.requestFocus()
                            utility.showSoftKeyboard(view.edtOtherType)
                        }
                    } else {
                        view.edtOtherType.visibility = View.GONE
                        view.edtOtherType.setText("")
                        utility.hideSoftKeyboard(view.edtOtherType)
                    }
                }
            }
        })
        if (FAV_TYPE === Constant.FAV_SOURCE) view.edtLocation.setText(startLocAddress) else if (FAV_TYPE === Constant.FAV_DESTINATION) view.edtLocation.setText(
            endLocAddress
        )
        view.tvOk.setOnClickListener(
            View.OnClickListener {
                if (locationTypeFav?.isEmpty() == true) {
                    showToast(
                        this@MapFrontActivity,
                        getString(R.string.please_select_location_type)
                    )
                    return@OnClickListener
                } else if (locationTypeFav.equals(
                        getString(R.string.other),
                        ignoreCase = true
                    ) && view.edtOtherType.text.toString().isEmpty()
                ) {
                    showToast(this@MapFrontActivity, getString(R.string.please_enter_location_type))
                    return@OnClickListener
                } else {
                    utility.hideSoftKeyboard(view.edtOtherType)
                    if (view.edtOtherType.text.toString().isNotEmpty()) {
                        locationTypeFav = view.edtOtherType.text.toString()
                    }
                    bottomSheetDialog?.dismiss()
                    if (FAV_TYPE === Constant.FAV_SOURCE) addAddressData(
                        startPoint?.latitude.toString(),
                        startPoint?.longitude.toString(),
                        startLocAddress
                    ) else if (FAV_TYPE === Constant.FAV_DESTINATION) addAddressData(
                        endPoint?.latitude.toString(),
                        endPoint?.longitude.toString(),
                        endLocAddress
                    )
                }
            })
        view.tvCancel.setOnClickListener {
            bottomSheetDialog?.dismiss()
        }
        bottomSheetDialog?.show()
    }

    @SuppressLint("InflateParams")
    private fun showReTryDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_ride_cancel, null, false)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        view.reTryBtn.setOnClickListener {
            bottomSheetDialog?.dismiss()
            addRideRequest()
        }
        view.cancelBtn.setOnClickListener {
            if (rideCancelTimer != null) rideCancelTimer?.cancel()
            findingRideLt.visibility = View.GONE
            lnRideData.visibility = View.VISIBLE
            this.view.edtDestSearch?.clearFocus()
            this.view.edtPickupSearch?.clearFocus()
            bottomSheetDialog?.dismiss()
        }
        bottomSheetDialog?.show()
    }

    override fun onSlideComplete(view: SlideToActView) {
        if (view.isCompleted()) {
            triggerEmergencyContacts()
            bottomSheetDialog?.dismiss()
        }
    }

    /*
    * The Recent location click process
    * */
    private fun loadRecentLocation(rideDetail: RideDetail) {
        recentClicked = true
        startPoint = LatLng(
            rideDetail.src_lat?.toDouble()!!,
            rideDetail.src_lng?.toDouble()!!
        )
        endPoint = LatLng(
            rideDetail.dest_lat?.toDouble()!!,
            rideDetail.dest_lng?.toDouble()!!
        )
        setLocationOnMap(endPoint)
        letsGolayout.visibility = View.GONE
        mainToolbarLayout.visibility = View.GONE
        ivLocationPin.visibility = View.GONE
        startLocAddress = rideDetail.ride_start_location
        endLocAddress = rideDetail.ride_end_location
        editSrcLocation.setText(startLocAddress)
        editDestLocation.setText(endLocAddress)
        try {
            view = layoutInflater.inflate(R.layout.view_toolbar_search, null)
            this.view.edtPickupSearch?.setText(startLocAddress)
            this.view.edtDestSearch?.setText(endLocAddress)
        } catch (ex: Exception) {

        }
        if (mMap != null) mMap?.uiSettings?.isMyLocationButtonEnabled = false
        SEARCH_TYPE = Constant.SEARCH_DESTINATION
        loadLocation()
    }

    private fun getRotation(start: LatLng, end: LatLng): Float {
        val latDifference: Double = abs(start.latitude - end.latitude)
        val lngDifference: Double = abs(start.longitude - end.longitude)
        var rotation = -1F
        when {
            start.latitude < end.latitude && start.longitude < end.longitude -> {
                rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
            }
            start.latitude >= end.latitude && start.longitude < end.longitude -> {
                rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
            }
            start.latitude >= end.latitude && start.longitude >= end.longitude -> {
                rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
            }
            start.latitude < end.latitude && start.longitude >= end.longitude -> {
                rotation =
                    (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
        return rotation
    }

    private fun getBearing(begin: LatLng?, end: LatLng?): Float {
        return SphericalUtil.computeHeading(begin, end).toFloat()

        /*val lat = abs(begin!!.latitude - end!!.latitude)
        val lng = abs(begin.longitude - end.longitude)
        if (begin.latitude < end.latitude && begin.longitude < end.longitude) return Math.toDegrees(
            atan(lng / lat)
        )
            .toDouble() else if (begin.latitude >= end.latitude && begin.longitude < end.longitude) return (90 - Math.toDegrees(
            atan(lng / lat)
        ) + 90).toDouble() else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude) return (Math.toDegrees(
            atan(lng / lat)
        ) + 180).toDouble() else if (begin.latitude < end.latitude && begin.longitude >= end.longitude) return (90 - Math.toDegrees(
            atan(lng / lat)
        ) + 270).toDouble()
        return (-1).toDouble()*/
    }

    fun getCarBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.icon_car)
        return bitmap
//        return Bitmap.createScaledBitmap(bitmap, 50, 100, false)
    }

    /*
    * Setting the Start drawable icon on edit text
    * */
    private fun setIcon(padding1: Int, padding: Int, resource: Int) {
        this.view.edtPickupSearch?.setPadding(
            padding1,
            padding,
            padding,
            padding
        )
        this.view.edtPickupSearch?.setCompoundDrawablesWithIntrinsicBounds(
            resource,
            0,
            0,
            0
        )
    }

    var selectedSeats = 0
    override fun onSelectCardRide(vehiclesTypes: VehiclesTypes) {
        /** set type of transport*/
        vehicleType = vehiclesTypes.vtype_app_icon.toString()
        if (lastCarSelectedCarId == selectedVehicleId?.toInt()) {
            return
        }
        if (vehicleAdapter != null) {
            vehicleAdapter?.notifyDataSetChanged()
        }
        lastCarSelectedCarId = selectedVehicleId?.toInt()!!
        coupon = ""

        if (vehiclesTypes.isShare == "1") {
            seatSelectionLt.visibility = View.VISIBLE
            couponLt.visibility = View.GONE
            val seats = arrayOf("1 Seat", "2 Seats")
            val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, seats)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //Setting the ArrayAdapter data on the Spinner
            seatSpinner.adapter = aa
            selectedSeats = 1
            seatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0)
                        selectedSeats = 1
                    else selectedSeats = 2
                    getNearByDrivers(latitude.toString() + "", longitude.toString() + "")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }
        } else {
            selectedSeats = 0
            seatSelectionLt.visibility = View.GONE
            couponLt.visibility = View.VISIBLE
            txt_applyCoupon.text = resources.getString(R.string.apply_coupon)
            getNearByDrivers(latitude.toString() + "", longitude.toString() + "")

        }

//        bookBtn.performClick()
    }
}