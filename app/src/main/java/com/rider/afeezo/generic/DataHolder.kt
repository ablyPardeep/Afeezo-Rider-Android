package com.rider.afeezo.generic

import android.content.Context
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.*
import com.rider.afeezo.network.APIClient.Companion.getClient
import com.rider.afeezo.network.APIInterface
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DataHolder {
    var apiInterface: APIInterface? = null
    private var lisner: ResponseHandler? = null

    var token = ""
    var fcmToken = ""
    var smsSender = ""
    var rideRequestId = ""
    var rideId = ""
    var walletBalance = ""
    private var store: PrefStore? = null

    fun getStore(ctx: Context?): PrefStore {
        if (store == null) store = PrefStore(ctx)
        return store!!
    }

    init {
        apiInterface = getClient()?.create(APIInterface::class.java)
    }


    fun loginWithOtp(
        i: Int,
        phone: String,
        otp: String,
        deviceId: String,
        country: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], phone = [$phone], otp = [$otp], deviceId = [$deviceId], country = [$country], listener = [$listener]")
        lisner = listener
        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            if (result != null) {
                val firebaseToken: String = result//instance.fcmToken
                println("firebaseToken = $firebaseToken")
                val loginCall = apiInterface?.loginWithOtp(
                    phone, otp, "RIDER", deviceId, "ANDROID",
                    country,
                    firebaseToken
                )
                loginCall?.enqueue(object : Callback<Common?> {
                    override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                        //Log.e("Response=>", new Gson().toJson(response));
                        lisner?.onSuccess(i, response)
                    }

                    override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                        loginCall.cancel()
                        lisner?.onError(t)
                    }
                })
                // DO your thing with your firebase token
            }
        }
    }

    fun loginWithPassword(i: Int, phone: String, password: String, listener: ResponseHandler) {
        println("i = [$i], phone = [$phone], password = [$password], listener = [$listener]")
        lisner = listener
        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            if (result != null) {
                val firebaseToken: String = result//instance.fcmToken
                val loginCall = apiInterface?.loginWithPassword(phone, password, firebaseToken)
                loginCall?.enqueue(object : Callback<Common?> {
                    override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                        //Log.e("Response=>", new Gson().toJson(response));
                        lisner?.onSuccess(i, response)
                    }

                    override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                        loginCall.cancel()
                        lisner?.onError(t)
                    }
                })
            }
        }
    }

    fun paymentMethods(i: Int, token: String?, listener: ResponseHandler?) {
        lisner = listener
        val loginCall = apiInterface?.paymentMethods(
            token!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun addRideRequest(
        i: Int,
        srclat: String,
        srclng: String,
        destlat: String,
        destlng: String,
        vehicleType: String,
        startLocName: String,
        endLocName: String,
        paymentCard: String,
        paymentMode: String,
        wallet: String,
        coupon: String,
        seats: String,
        riderToken: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], srclat = [$srclat], srclng = [$srclng], destlat = [$destlat], destlng = [$destlng], vehicleType = [$vehicleType], startLocName = [$startLocName], endLocName = [$endLocName], coupon = [$coupon], riderToken = [$riderToken], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.addRideRequest(
            srclat, srclng, destlat, destlng, vehicleType,
            startLocName, endLocName,
            paymentMode,
            paymentCard,
            wallet, coupon,seats, riderToken
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }
///** In case of schedule ride */
//    fun addRideRequest(
//        i: Int,
//        srclat: String,
//        srclng: String,
//        destlat: String,
//        destlng: String,
//        vehicleType: String,
//        startLocName: String,
//        endLocName: String,
//        paymentCard: String,
//        paymentMode: String,
//        wallet: String,
//        coupon: String,
//        seats: String,
//        riderToken: String,
//        is_scheduled: String,
//        scheduled_timestamp: String,
//        listener: ResponseHandler
//    ) {
//        println("i = [$i], srclat = [$srclat], srclng = [$srclng], destlat = [$destlat], destlng = [$destlng], vehicleType = [$vehicleType], startLocName = [$startLocName], endLocName = [$endLocName], coupon = [$coupon], riderToken = [$riderToken], listener = [$listener]")
//        lisner = listener
//        val loginCall = apiInterface?.addRideRequest(
//            srclat, srclng, destlat, destlng, vehicleType,
//            startLocName, endLocName,
//            paymentMode,
//            paymentCard,
//            wallet, coupon,seats, riderToken,is_scheduled,scheduled_timestamp
//        )
//        loginCall?.enqueue(object : Callback<Common?> {
//            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
//                //Log.e("Response=>", new Gson().toJson(response));
//                lisner?.onSuccess(i, response)
//            }
//
//            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
//                loginCall.cancel()
//                lisner?.onError(t)
//            }
//        })
//    }

    fun sendOtp(i: Int, phone: String, country: String?, listener: ResponseHandler) {
        println("i = [$i], phone = [$phone], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.sendOtp(
            phone,
            country!!, "RIDER"
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getUserCoupons(
        i: Int,
        token: String,
        srcLat: String?,
        srcLong: String?,
        destLat: String?,
        destLong: String?,
        vehiclType: String?,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.getUserCoupons(
            token,
            srcLat!!, srcLong!!, destLat!!,
            destLong!!, vehiclType!!
        )
        loginCall?.enqueue(object : Callback<Offers?> {
            override fun onResponse(loginCall: Call<Offers?>, response: Response<Offers?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Offers?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun supportCatgeories(
        i: Int,
        page: String?,
        pageSize: String?,
        token: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.supportCatgeories(
            page!!,
            pageSize!!, token
        )
        loginCall?.enqueue(object : Callback<FaqCategories?> {
            override fun onResponse(
                loginCall: Call<FaqCategories?>,
                response: Response<FaqCategories?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<FaqCategories?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun validateCoupon(
        i: Int,
        coupon: String?,
        src_latitude: String?,
        src_longitude: String?,
        dest_latitude: String?,
        dest_longitude: String?,
        vehicle_type: String?,
        token: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.validateCoupon(
            coupon!!,
            src_latitude!!,
            src_longitude!!, dest_latitude!!, dest_longitude!!, vehicle_type!!, token
        )
        loginCall?.enqueue(object : Callback<CouponValidate?> {
            override fun onResponse(
                loginCall: Call<CouponValidate?>,
                response: Response<CouponValidate?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<CouponValidate?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun supportCategoryDetail(i: Int, faqCatId: String?, token: String, listener: ResponseHandler) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.supportCategoryDetail(faqCatId!!, token)
        loginCall?.enqueue(object : Callback<FaqDetail?> {
            override fun onResponse(loginCall: Call<FaqDetail?>, response: Response<FaqDetail?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<FaqDetail?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getOperatingLocationsByCountryCode(
        i: Int,
        countryCode: String?,
        token: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.getOperatingLocationsByCountryCode(
            countryCode!!, token
        )
        loginCall?.enqueue(object : Callback<OperatingLocs?> {
            override fun onResponse(
                loginCall: Call<OperatingLocs?>,
                response: Response<OperatingLocs?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<OperatingLocs?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getVehicleTypesByLocationId(
        i: Int,
        locationId: String?,
        token: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.getVehicleTypesByLocationId(
            locationId!!, token
        )
        loginCall?.enqueue(object : Callback<Vehicles?> {
            override fun onResponse(loginCall: Call<Vehicles?>, response: Response<Vehicles?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Vehicles?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getVehicleTypesById(i: Int, id: String?, token: String, listener: ResponseHandler) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.getVehicleTypesById(id!!, token)
        loginCall?.enqueue(object : Callback<VehicleTypeById?> {
            override fun onResponse(
                loginCall: Call<VehicleTypeById?>,
                response: Response<VehicleTypeById?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<VehicleTypeById?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun updateUserProfileinfo(
        i: Int,
        name: String,
        email: String,
        password: String,
        referral: String,
        device: String?,
        type: String?,
        riderToken: String?,
        listener: ResponseHandler?
    ) {
        println("i = [$i], name = [$name], email = [$email], password = [$password], referral = [$referral]")
        lisner = listener
        val loginCall = apiInterface?.updateUserProfileinfo(
            name, email, password, referral,
            device!!,
            type!!,
            riderToken!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun setUpWalletRecharge(
        i: Int,
        amount: String,
        riderToken: String?,
        listener: ResponseHandler
    ) {
        println("i = [$i], amount = [$amount], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.setUpWalletRecharge(
            amount,
            riderToken!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun addNewCard(i: Int, riderToken: String?, listener: ResponseHandler?) {
        println("i = [$i]")
        lisner = listener
        val loginCall = apiInterface?.addNewCard(
            riderToken!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getSavedCards(i: Int, riderToken: String?, listener: ResponseHandler?) {
        println("i = [$i]")
        lisner = listener
        val loginCall = apiInterface?.getUserSavedCards(riderToken!!)
        loginCall?.enqueue(object : Callback<UserCards?> {
            override fun onResponse(loginCall: Call<UserCards?>, response: Response<UserCards?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<UserCards?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun deleteSavedCard(i: Int, token: String?, id: String?, listener: ResponseHandler?) {
        println("i = [$i]")
        lisner = listener
        val loginCall = apiInterface?.deleteSavedCard(
            token!!, id!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getTempToken(i: Int, riderToken: String?, listener: ResponseHandler?) {
        lisner = listener
        val loginCall = apiInterface?.get_temp_token(riderToken)
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun userProfileInfo(i: Int, riderToken: String, listener: ResponseHandler) {
        println("i = [$i], riderToken = [$riderToken], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.userProfileInfo(riderToken)
        loginCall?.enqueue(object : Callback<UserProfile?> {
            override fun onResponse(
                loginCall: Call<UserProfile?>,
                response: Response<UserProfile?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<UserProfile?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun removeProfileImage(i: Int, riderToken: String, listener: ResponseHandler) {
        lisner = listener
        val loginCall = apiInterface?.remove_profile_image(riderToken)
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun uploadProfilePic(i: Int, token: String?, file: File, listener: ResponseHandler?) {
        lisner = listener
        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("user_profile_image", file.name, reqFile)
        val requestToken = token?.toRequestBody("text/plain".toMediaTypeOrNull())
        val action = "avatar".toRequestBody("text/plain".toMediaTypeOrNull())
        val imageUploadCall = apiInterface?.update_profile_image(body, action, requestToken)
        imageUploadCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(imageUploadCall: Call<Common?>, response: Response<Common?>) {
                listener?.onSuccess(i, response)
            }

            override fun onFailure(imageUploadCall: Call<Common?>, t: Throwable) {
                imageUploadCall.cancel()
                listener?.onError(t)
            }
        })
    }

    fun logout(i: Int, token: String?, listener: ResponseHandler) {
        println("i = [$i], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.logout(
            token!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun myRides(
        i: Int,
        page: String,
        pagesize: String,
        riderToken: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], page = [$page], pagesize = [$pagesize], riderToken = [$riderToken], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.myrides(page, pagesize, riderToken)
        loginCall?.enqueue(object : Callback<MyRides?> {
            override fun onResponse(loginCall: Call<MyRides?>, response: Response<MyRides?>) {
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<MyRides?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getNearByDrivers(
        i: Int, lat: String, lng: String, vehicleId: String?,
        listener: ResponseHandler
    ) {
        println("i = [$i], lat = [$lat], lng = [$lng], listener = [$listener]")
        lisner = listener
        if (instance.token.isEmpty()) {
            return
        }
        val loginCall = apiInterface?.getNearByVehicles(
            lat, lng,
            vehicleId!!, instance.token
        )
        loginCall?.enqueue(object : Callback<Drivers?> {
            override fun onResponse(loginCall: Call<Drivers?>, response: Response<Drivers?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Drivers?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getVehicleType(
        i: Int,
        srclat: String,
        srclng: String,
        destlat: String,
        destlng: String,
        seats: String,
        listener: ResponseHandler?
    ) {
        lisner = listener
        val loginCall = apiInterface?.getVehicleTypes(
            srclat, srclng, destlat, destlng,seats
        )
        loginCall?.enqueue(object : Callback<Vehicles?> {
            override fun onResponse(loginCall: Call<Vehicles?>, response: Response<Vehicles?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Vehicles?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun cancelRideRequest(
        i: Int,
        rideId: String,
        autoCancel: String?,
        token: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], rideId = [$rideId], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.cancelRideRequest(
            rideId,
            autoCancel!!, token
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun verifyReferral(
        i: Int,
        referralPhone: String?,
        device: String?,
        token: String?,
        listener: ResponseHandler?
    ) {
        lisner = listener
        val loginCall = apiInterface?.verifyReferral(
            referralPhone!!, device!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun cancelRide(
        i: Int,
        rideId: String,
        reason: String?,
        comments: String?,
        token: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], rideId = [$rideId], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.cancelRide(
            rideId,
            reason!!, comments!!, token
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun rideDetail(i: Int, rideId: String, token: String?, listener: ResponseHandler) {
        println("i = [$i], rideId = [$rideId], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.rideDetail(rideId, token!!)
        loginCall?.enqueue(object : Callback<RideDetails?> {
            override fun onResponse(
                loginCall: Call<RideDetails?>,
                response: Response<RideDetails?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<RideDetails?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun rateRide(i: Int, rideId: String, stars: String, token: String, listener: ResponseHandler) {
        println("i = [$i], rideId = [$rideId], stars = [$stars], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.rateRide(rideId, stars, token)
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }
    fun shareReferText(i: Int, token: String,listener: ResponseHandler) {
        lisner = listener
        val loginCall = apiInterface?.shareReferText(token)
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun rideCancelReasons(i: Int, token: String?, listener: ResponseHandler) {
        println("i = [$i],listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.rideCancelReasons(token!!)
        loginCall?.enqueue(object : Callback<RideCancel?> {
            override fun onResponse(loginCall: Call<RideCancel?>, response: Response<RideCancel?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<RideCancel?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun saveFirebaseToken(
        i: Int,
        firebaseTokenId: String,
        token: String,
        listener: ResponseHandler
    ) {
        println("i = [$i], firebaseTokenId = [$firebaseTokenId], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.saveFirebaseToken(firebaseTokenId, token)
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getActiveRide(i: Int, token: String, listener: ResponseHandler) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.getActiveRide(token)
        loginCall?.enqueue(object : Callback<RideDetails?> {
            override fun onResponse(
                loginCall: Call<RideDetails?>,
                response: Response<RideDetails?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<RideDetails?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun transactions(
        i: Int,
        token: String,
        page: String?,
        pageSize: String?,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.transactions(
            token,
            page!!, pageSize!!
        )
        loginCall?.enqueue(object : Callback<Transaction?> {
            override fun onResponse(
                loginCall: Call<Transaction?>,
                response: Response<Transaction?>
            ) {
//                Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Transaction?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun rewardPoints(
        i: Int, token: String, page: String?, pageSize: String?,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.rewardPoints(
            token,
            page!!, pageSize!!
        )
        loginCall?.enqueue(object : Callback<Rewards?> {
            override fun onResponse(loginCall: Call<Rewards?>, response: Response<Rewards?>) {
//                Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Rewards?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun requestWithdrawal(
        i: Int,
        token: String,
        amount: String?,
        bankName: String?,
        accName: String?,
        accNo: String?,
        ifscCode: String?,
        bankAddress: String?,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.requestWithdrawal(
            token,
            amount!!, bankName!!, accName!!, accNo!!, ifscCode!!, bankAddress!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun commonApiCall(i: Int, token: String, listener: ResponseHandler) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        var loginCall: Call<Common>? = null
        when (i) {
            Constant.ABOUT_US_API -> loginCall =
                apiInterface?.about_us(token)
            Constant.CONTACT_US_API -> loginCall =
                apiInterface?.contact_us(token)
            Constant.PRIVACY_POLICY_API -> loginCall =
                apiInterface?.privacy_policy(token)
            Constant.TERMS_COND_API -> loginCall =
                apiInterface?.terms_conditions(token)
        }
        loginCall!!.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun addUserEmergencyContact(
        i: Int,
        token: String,
        name: String?,
        phone: String?,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.addUserEmergencyContact(
            token,
            name!!, phone!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getUserEmergencyContacts(i: Int, token: String, listener: ResponseHandler) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.getUserEmergencyContacts(token)
        loginCall?.enqueue(object : Callback<EmergencyContacts?> {
            override fun onResponse(
                loginCall: Call<EmergencyContacts?>,
                response: Response<EmergencyContacts?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<EmergencyContacts?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun deleteEmergencyContact(i: Int, token: String, id: String?, listener: ResponseHandler) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.deleteEmergencyContact(
            token,
            id!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun addRiderFavoriteLocations(
        i: Int,
        token: String,
        name: String?,
        lattitude: String?,
        longitude: String?,
        locationName: String?,
        listener: ResponseHandler
    ) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.addRiderFavoriteLocations(
            token,
            name!!, lattitude!!, longitude!!, locationName!!
        )
        loginCall?.enqueue(object : Callback<AddFavLocation?> {
            override fun onResponse(
                loginCall: Call<AddFavLocation?>,
                response: Response<AddFavLocation?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<AddFavLocation?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getUserFavoriteLocations(i: Int, token: String, listener: ResponseHandler) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.getUserFavoriteLocations(token)
        loginCall?.enqueue(object : Callback<FavLocation?> {
            override fun onResponse(
                loginCall: Call<FavLocation?>,
                response: Response<FavLocation?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<FavLocation?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getRideShareUrl(i: Int, token: String, ride_id: String, listener: ResponseHandler?) {
        println("i = [$i], token = [$token], ride_id = [$ride_id]")
        lisner = listener
        val loginCall = apiInterface?.getRideShareUrl(token, ride_id)
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun deleteFavoriteLocation(i: Int, token: String, id: String?, listener: ResponseHandler) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.deleteFavoriteLocation(
            token,
            id!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun quickLoadMoneyTabs(i: Int, token: String, listener: ResponseHandler) {
        println("i = [$i], token = [$token], listener = [$listener]")
        lisner = listener
        val loginCall = apiInterface?.quick_load_money_tabs(token)
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun endRide(
        i: Int,
        ride_id: String?,
        latitude: String?,
        longitude: String?,
        endLoc: String?,
        token: String?,
        listener: ResponseHandler?
    ) {
        lisner = listener
        val loginCall = apiInterface?.endRide(
            ride_id!!, latitude!!, longitude!!, endLoc!!, token!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun triggerEmergencyContacts(
        i: Int,
        ride_id: String?,
        token: String?,
        listener: ResponseHandler?
    ) {
        lisner = listener
        val loginCall = apiInterface?.triggerEmergencyContacts(
            ride_id!!, token!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun getConfigRideStatuses(i: Int, token: String?, listener: ResponseHandler?) {
        lisner = listener
        val loginCall = apiInterface?.getConfigRideStatuses(token!!)
        loginCall?.enqueue(object : Callback<RideStatuses?> {
            override fun onResponse(
                loginCall: Call<RideStatuses?>,
                response: Response<RideStatuses?>
            ) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<RideStatuses?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun rideIssues(i: Int, token: String?, listener: ResponseHandler?) {
        lisner = listener
        val loginCall = apiInterface?.rideIssues(token!!)
        loginCall?.enqueue(object : Callback<RideIssue?> {
            override fun onResponse(loginCall: Call<RideIssue?>, response: Response<RideIssue?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<RideIssue?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    fun reportIssueRide(
        i: Int,
        token: String?,
        ride_id: String?,
        issueId: String?,
        comments: String?,
        listener: ResponseHandler?
    ) {
        lisner = listener
        val loginCall = apiInterface?.reportIssueRide(
            token!!, ride_id!!, issueId!!, comments!!
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }
    fun requestData(
        i: Int,
        token: String?,
        name: String?,
        email: String?,
        purpose: String?,
        listener: ResponseHandler?
    ) {
        lisner = listener
        val loginCall = apiInterface?.requestData(
            token.toString(), name.toString(), email.toString(), purpose.toString()
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }
    fun sendTruncateRequest(
        i: Int,
        token: String?,
        listener: ResponseHandler?
    ) {
        lisner = listener
        val loginCall = apiInterface?.sendTruncateRequest(
            token.toString(), "1"
        )
        loginCall?.enqueue(object : Callback<Common?> {
            override fun onResponse(loginCall: Call<Common?>, response: Response<Common?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<Common?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    /** function for get configurations */
    fun getConfigurations(
        i: Int,
        token: String?,
        listener: ResponseHandler?
    ) {
        lisner = listener
        val loginCall = apiInterface?.getConfigurations(
            token.toString()
        )
        loginCall?.enqueue(object : Callback<GetConfiguration?> {
            override fun onResponse(loginCall: Call<GetConfiguration?>, response: Response<GetConfiguration?>) {
                //Log.e("Response=>", new Gson().toJson(response));
                lisner?.onSuccess(i, response)
            }

            override fun onFailure(loginCall: Call<GetConfiguration?>, t: Throwable) {
                loginCall.cancel()
                lisner?.onError(t)
            }
        })
    }

    companion object {
        private var holder: DataHolder? = null
        val instance: DataHolder
            get() {
                if (holder == null) holder = DataHolder()
                return holder!!
            }
    }

    init {
        apiInterface = getClient()!!.create(APIInterface::class.java)
    }
}