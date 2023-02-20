package com.rider.afeezo.model

import com.google.gson.annotations.SerializedName

class Vehicles {
    @SerializedName("status")
    var status: String? = null

    @SerializedName("currencySymbol")
    var currencySymbol: String? = null

    @SerializedName("msg")
    var msg: String? = null

    @SerializedName("vehiclesTypes")
    var vehiclesTypes: ArrayList<VehiclesTypes>?=null

}
