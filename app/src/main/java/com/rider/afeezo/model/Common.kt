package com.rider.afeezo.model

import com.google.gson.annotations.SerializedName

class Common {
    @field:SerializedName("status")
    var status: String? = null

    @field:SerializedName("payementModes")
    val paymentModes: List<PaymentModes>? = null

    @field:SerializedName("currencySymbol")
    var currencySymbol: String? = null

    @field:SerializedName("order_id")
    var wallet_order_id: String? = null

    @field:SerializedName("minLoadMoney")
    var minLoadMoney: String? = null

    @field:SerializedName("sender")
    var smsSender: String? = null

    @field:SerializedName("tkn")
    var tempToken: String? = null

    @field:SerializedName("content")
    var content: String? = null
    @field:SerializedName("text")
    var referText: String? = null

    @field:SerializedName("rideRequestAutoCancelledInterval")
    var rideRequestAutoCancelledInterval: String? = null

    var url: String? = null
    var image: String? = null

    @field:SerializedName("tabs")
    var tabs: ArrayList<String>? = null

    @field:SerializedName("web_url")
    var web_url: String? = null

    @field:SerializedName("title")
    var title: String? = null

    @field:SerializedName("ask_profile_info")
    var ask_profile_info: String? = null

    @field:SerializedName("msg")
    var msg: String? = null

    @field:SerializedName("user_token")
    var rider_token: String? = null

    @field:SerializedName("user_id")
    var rider_id: String? = null

    @field:SerializedName("auto_login")
    var auto_login: String? = null

    @field:SerializedName("rideRequestId")
    var rideRequestId: String? = null

    @field:SerializedName("requestId")
    var requestId: String? = null

    @field:SerializedName("rideId")
    var rideId: String? = null
}