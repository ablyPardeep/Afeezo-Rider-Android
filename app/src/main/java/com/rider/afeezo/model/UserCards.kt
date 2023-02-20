package com.rider.afeezo.model

import java.util.List
import com.google.gson.annotations.SerializedName

class UserCards {

    @SerializedName("cards")
    var cards: ArrayList<CardsItem>? = null

    @SerializedName("status")
    var status: String? = null

}