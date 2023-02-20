package com.rider.afeezo.model

import com.google.gson.annotations.SerializedName

class CardsItem {

    @field:SerializedName("last4")
    val last4: String? = null

    @field:SerializedName("bank")
    val bank: String? = null

    @field:SerializedName("channel")
    val channel: String? = null

    @field:SerializedName("exp_month")
    val expMonth: String? = null

    @field:SerializedName("id")
    val id: String? = null

    @field:SerializedName("exp_year")
    val expYear: String? = null

    @field:SerializedName("type")
    val type: String? = null

    @field:SerializedName("isSelected")
    var isSelected: Boolean? = null
}