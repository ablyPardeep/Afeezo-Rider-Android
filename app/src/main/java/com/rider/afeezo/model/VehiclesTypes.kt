package com.rider.afeezo.model

import com.google.gson.annotations.SerializedName

class VehiclesTypes {
    @SerializedName("min_fare")
    var min_fare: String? = null

    @SerializedName("night_start_time")
    var night_start_time: String? = null

    @SerializedName("location_name")
    var location_name: String? = null

    @SerializedName("cancellation_charge")
    var cancellation_charge: String? = null

    @SerializedName("night_surcharge_enabled")
    var night_surcharge_enabled: String? = null

    @SerializedName("cancellation_minutes")
    var cancellation_minutes: String? = null

    @SerializedName("night_end_time")
    var night_end_time: String? = null

    @SerializedName("surcharge_multiply_factor")
    var surcharge_multiply_factor: String? = null

    @SerializedName("id")
    var id: String? = null

    @SerializedName("base_fare")
    var base_fare: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("peak_surcharge")
    var peak_surcharge: String? = null

    @SerializedName("seats")
    var seats: String? = null

    @SerializedName("price_per_km")
    var price_per_km: String? = null

    @SerializedName("price_per_min")
    var price_per_min: String? = null

    @SerializedName("icon")
    var icon: String? = null

    @SerializedName("details")
    var details: String? = null
    
    @SerializedName("isShare")
    var isShare: String? = null

    @SerializedName("vtype_app_icon")
    var vtype_app_icon: String? = null


    var isChecked: Boolean = false

    @SerializedName("vehicleTypeEstimateArr")
    var vehicleTypeEstimateArr: VehicleTypeEstimateArr? = null

}