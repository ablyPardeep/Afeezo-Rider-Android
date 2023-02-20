package com.rider.afeezo.network

import com.rider.afeezo.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface APIInterface {

    @FormUrlEncoded
    @POST("mobile-app-api/sendOtp")
    fun sendOtp(
        @Field("phone") phone: String,
        @Field("country") country: String,
        @Field("type") type: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/loginWithOtp")
    fun loginWithOtp(
        @Field("phone") phone: String, @Field("otp") otp: String, @Field
            ("type") type: String, @Field("device") device: String, @Field("osType") osType: String,
        @Field("country") country: String, @Field("firebaseTokenId") firebaseTokenId: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/getNearByVehicles/{latitude}/{longitude}/{id}")
    fun getNearByVehicles(
        @Path("latitude") latitude: String,
        @Path("longitude") longitude: String,
        @Path("id") id: String,
        @Field("_user_token") _user_token: String
    ): Call<Drivers>

    @FormUrlEncoded
    @POST("user-app-api/addRideRequest")
    fun addRideRequest(
        @Field("src_latitude") src_latitude: String,
        @Field("src_longitude") src_longitude: String,
        @Field("dest_latitude") dest_latitude: String,
        @Field("dest_longitude") dest_longitude: String,
        @Field("vehicle_type") vehicle_type: String,
        @Field("startLocName") startLocName: String,
        @Field("endLocName") endLocName: String,
        @Field("payment_mode") payment_mode: String,
        @Field("payment_card") payment_card: String,
        @Field("wallet") wallet: String,
        @Field("coupon") coupon: String,
        @Field("seats") seats: String,
        @Field("_user_token") _user_token: String
    ): Call<Common>

//    /** In case of schedule ride */
//    @FormUrlEncoded
//    @POST("user-app-api/addRideRequest")
//    fun addRideRequest(
//        @Field("src_latitude") src_latitude: String,
//        @Field("src_longitude") src_longitude: String,
//        @Field("dest_latitude") dest_latitude: String,
//        @Field("dest_longitude") dest_longitude: String,
//        @Field("vehicle_type") vehicle_type: String,
//        @Field("startLocName") startLocName: String,
//        @Field("endLocName") endLocName: String,
//        @Field("payment_mode") payment_mode: String,
//        @Field("payment_card") payment_card: String,
//        @Field("wallet") wallet: String,
//        @Field("coupon") coupon: String,
//        @Field("seats") seats: String,
//        @Field("_user_token") _user_token: String,
//        @Field("is_scheduled") is_scheduled: String,
//        @Field("scheduled_timestamp") scheduled_timestamp: String
//    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/cancelRideRequest")
    fun cancelRideRequest(
        @Field("request_id") ride_id: String,
        @Field("isAutoCancel") autoCancel: String,
        @Field("_user_token") _user_token: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/verifyReferral")
    fun verifyReferral(
        @Field("phone") phone: String,
        @Field("device") device: String
    ): Call<Common>


    @FormUrlEncoded
    @POST("mobile-app-api/getVehicleTypesByCordinates")
    fun getVehicleTypes(
        @Field("src_latitude") src_latitude: String,
        @Field("src_longitude") src_longitude: String,
        @Field("dest_latitude") dest_latitude: String,
        @Field("dest_longitude") dest_longitude: String,
        @Field("seats") seats: String
    ): Call<Vehicles>

    @FormUrlEncoded
    @POST("user-app-api/rideDetail")
    fun rideDetail(
        @Field("ride_id") ride_id: String,
        @Field("_user_token") _user_token: String
    ): Call<RideDetails>

    @FormUrlEncoded
    @POST("user-app-api/rateRide")
    fun rateRide(
        @Field("ride_id") ride_id: String,
        @Field("stars") stars: String,
        @Field("_user_token") _user_token: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/shareReferText")
    fun shareReferText(
        @Field("_user_token") _user_token: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/rideCancelReasons")
    fun rideCancelReasons(@Field("_user_token") _user_token: String): Call<RideCancel>

    @FormUrlEncoded
    @POST("user-app-api/saveFirebaseToken")
    fun saveFirebaseToken(
        @Field("firebaseTokenId") firebaseTokenId: String,
        @Field("_user_token") _user_token: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/updateUserProfileinfo")
    fun updateUserProfileinfo(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("referral") referral: String,
        @Field("device") device: String,
        @Field("type") type: String,
        @Field("_user_token") _user_token: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/loginWithPassword")
    fun loginWithPassword(
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("firebaseTokenId") firebaseTokenId: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/available-payment-options")
    fun paymentMethods(@Field("_user_token") _user_token: String): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/checkRegistered")
    fun checkRegistered(@Field("phone") phone: String): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/setUpWalletRecharge")
    fun setUpWalletRecharge(
        @Field("amount") amount: String,
        @Field("_user_token") _user_token: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/get_temp_token")
    fun get_temp_token(@Field("_user_token") _user_token: String?): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/userProfileInfo")
    fun userProfileInfo(@Field("_user_token") _user_token: String): Call<UserProfile>

    @Multipart
    @POST("user-app-api/update_profile_image")
    fun update_profile_image(
        @Part file: MultipartBody.Part,
        @Part("action") avatar: RequestBody,
        @Part("_user_token") _user_token: RequestBody?
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/remove_profile_image")
    fun remove_profile_image(@Field("_user_token") _user_token: String): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/logout")
    fun logout(@Field("_user_token") _user_token: String): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/myrides")
    fun myrides(
        @Field("page") page: String,
        @Field("pagesize") pagesize: String,
        @Field("_user_token") _user_token: String
    ): Call<MyRides>

    @FormUrlEncoded
    @POST("user-app-api/getActiveRide")
    fun getActiveRide(@Field("_user_token") _user_token: String): Call<RideDetails>

    @FormUrlEncoded
    @POST("user-app-api/cancelRide")
    fun cancelRide(
        @Field("ride_id") ride_id: String,
        @Field("reason") reason: String,
        @Field("comments") comments: String,
        @Field("_user_token") _user_token: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/quick_load_money_tabs")
    fun quick_load_money_tabs(@Field("_user_token") _user_token: String): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/getUserCoupons")
    fun getUserCoupons(
        @Field("_user_token") _user_token: String,
        @Field("src_latitude") src_latitude: String,
        @Field("src_longitude") src_longitude: String,
        @Field("dest_latitude") dest_latitude: String,
        @Field("dest_longitude") dest_longitude: String,
        @Field("vehicle_type") vehicle_type: String
    ): Call<Offers>

    @FormUrlEncoded
    @POST("mobile-app-api/getOperatingLocationsByCountryCode")
    fun getOperatingLocationsByCountryCode(
        @Field("country_code") country_code: String,
        @Field("_user_token") _user_token: String
    ): Call<OperatingLocs>

    @FormUrlEncoded
    @POST("mobile-app-api/getVehicleTypesByLocationId/{location_id}")
    fun getVehicleTypesByLocationId(
        @Path("location_id")
        location_id: String,
        @Field("_user_token")
        _user_token: String
    ): Call<Vehicles>


    @FormUrlEncoded
    @POST("mobile-app-api/getVehicleTypesById")
    fun getVehicleTypesById(
        @Field("id")
        id: String,
        @Field("_user_token")
        _user_token: String
    ): Call<VehicleTypeById>

    @FormUrlEncoded
    @POST("user-app-api/supportCatgeories")
    fun supportCatgeories(
        @Field("page") page: String, @Field("pagesize") pagesize: String,
        @Field("_user_token") _user_token: String
    ): Call<FaqCategories>

    @FormUrlEncoded
    @POST("user-app-api/addNewCard")
    fun addNewCard(@Field("_user_token") _user_token: String): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/getUserSavedCards")
    fun getUserSavedCards(@Field("_user_token") _user_token: String): Call<UserCards>

    @FormUrlEncoded
    @POST("user-app-api/deleteSavedCard")
    fun deleteSavedCard(
        @Field("_user_token") _user_token: String, @Field
            ("id") id: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/validateCoupon")
    fun validateCoupon(
        @Field("coupon") coupon: String,
        @Field("src_latitude") src_latitude: String,
        @Field("src_longitude") src_longitude: String,
        @Field("dest_latitude") dest_latitude: String,
        @Field("dest_longitude") dest_longitude: String,
        @Field("vehicle_type") vehicle_type: String,
        @Field("_user_token") _user_token: String
    ): Call<CouponValidate>

    @FormUrlEncoded
    @POST("user-app-api/supportCategoryDetail/{faqcat_id}")
    fun supportCategoryDetail(
        @Path("faqcat_id") faqcat_id: String,
        @Field("_user_token") _user_token: String
    ): Call<FaqDetail>

    @FormUrlEncoded
    @POST("user-app-api/transactions")
    fun transactions(
        @Field("_user_token") _user_token: String,
        @Field("page") page: String,
        @Field("pagesize") pagesize: String
    ): Call<Transaction>

    @FormUrlEncoded
    @POST("user-app-api/rewardPoints")
    fun rewardPoints(
        @Field("_user_token") _user_token: String,
        @Field("page") page: String,
        @Field("pagesize") pagesize: String
    ): Call<Rewards>

    @FormUrlEncoded
    @POST("user-app-api/requestWithdrawal")
    fun requestWithdrawal(
        @Field("_user_token") _user_token: String,
        @Field("amount") amount: String,
        @Field("bank_name") bank_name: String,
        @Field("account_name") account_name: String,
        @Field("account_number") account_number: String,
        @Field("ifsc_swift_code") ifsc_swift_code: String,
        @Field("bank_address") bank_address: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/about_us")
    fun about_us(@Field("_user_token") _user_token: String): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/contact_us")
    fun contact_us(@Field("_user_token") _user_token: String): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/privacy_policy")
    fun privacy_policy(@Field("_user_token") _user_token: String): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/terms_conditions")
    fun terms_conditions(@Field("_user_token") _user_token: String): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/addUserEmergencyContact")
    fun addUserEmergencyContact(
        @Field("_user_token") _user_token: String,
        @Field("name") name: String,
        @Field("phone") phone: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/getUserEmergencyContacts")
    fun getUserEmergencyContacts(
        @Field("_user_token") _user_token: String
    ): Call<EmergencyContacts>

    @FormUrlEncoded
    @POST("user-app-api/deleteEmergencyContact")
    fun deleteEmergencyContact(
        @Field("_user_token") _user_token: String,
        @Field("id") id: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/deleteFavoriteLocation")
    fun deleteFavoriteLocation(
        @Field("_user_token") _user_token: String,
        @Field("id") id: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/getUserFavoriteLocations")
    fun getUserFavoriteLocations(
        @Field("_user_token") _user_token: String
    ): Call<FavLocation>

    @FormUrlEncoded
    @POST("user-app-api/getRideShareUrl")
    fun getRideShareUrl(
        @Field("_user_token") _user_token: String,
        @Field("ride_id") ride_id: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/addRiderFavoriteLocations")
    fun addRiderFavoriteLocations(
        @Field("_user_token") _user_token: String,
        @Field("name") name: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("google_location_name") google_location_name: String
    ): Call<AddFavLocation>

    @FormUrlEncoded
    @POST("user-app-api/endRide")
    fun endRide(
        @Field("ride_id") ride_id: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("endLocName") endLocName: String,
        @Field("_user_token") _user_token: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/triggerEmergencyContacts")
    fun triggerEmergencyContacts(
        @Field("ride_id") ride_id: String,
        @Field("_user_token") _user_token: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("mobile-app-api/getConfigRideStatuses")
    fun getConfigRideStatuses(@Field("_user_token") _user_token: String): Call<RideStatuses>

    @FormUrlEncoded
    @POST("mobile-app-api/rideIssues")
    fun rideIssues(@Field("_user_token") _user_token: String): Call<RideIssue>

    @FormUrlEncoded
    @POST("user-app-api/reportIssueRide")
    fun reportIssueRide(
        @Field("_user_token") _user_token: String,
        @Field("ride_id") ride_id: String,
        @Field("issue") issue: String,
        @Field("comments") comments: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/requestData")
    fun requestData(
        @Field("_user_token") _user_token: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("purpose") purpose: String
    ): Call<Common>

    @FormUrlEncoded
    @POST("user-app-api/sendTruncateRequest")
    fun sendTruncateRequest(
        @Field("_user_token") _user_token: String,
        @Field("confirm") name: String,
    ): Call<Common>

    @GET("/maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("sensor") sensor: String?,
        @Query("mode") mode: String?,
        @Query("key") key: String?
    ): Call<ResponseBody?>?

    @GET("mobile-app-api/getconfigurations")
    fun getConfigurations(
        @Query("_user_token") _user_token: String
    ): Call<GetConfiguration>
}

