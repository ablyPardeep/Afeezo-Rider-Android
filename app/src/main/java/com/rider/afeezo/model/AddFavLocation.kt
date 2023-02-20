package com.rider.afeezo.model

import com.google.gson.annotations.SerializedName

class AddFavLocation {
    @field:SerializedName("status")
    var status: String? = null

    @field:SerializedName("data")
    var data: location? = null

    @field:SerializedName("msg")
    var msg: String? = null

    inner class location {
        @field:SerializedName("ufavpoint_id")
        var ufavpointId: String? = null

        @field:SerializedName("ufavpoint_user_id")
        var ufavpointUserId: String? = null

        @field:SerializedName("ufavpoint_name")
        var ufavpointName: String? = null

        @field:SerializedName("ufavpoint_latitude")
        var ufavpointLatitude: String? = null

        @field:SerializedName("ufavpoint_longitude")
        var ufavpointLongitude: String? = null

        @field:SerializedName("ufavpoint_google_name")
        var ufavpointGoogleName: String? = null

        @field:SerializedName("ufavpoint_added_date")
        var ufavpointAddedDate: String? = null
    }
}