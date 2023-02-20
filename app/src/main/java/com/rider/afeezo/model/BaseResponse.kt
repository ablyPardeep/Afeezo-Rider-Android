package com.rider.afeezo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BaseResponse<T> {

    @field:SerializedName("status")
    val status: String? = null

    @field:SerializedName("data")
    val data: T? = null

    @SerializedName("msg")
    val msg: String? = null
}
