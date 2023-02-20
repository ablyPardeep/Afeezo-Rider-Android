package com.rider.afeezo.interfaces

import retrofit2.Response

open interface ResponseHandler {
    fun onSuccess(tag: Int, response: Response<*>)
    fun onError(throwable: Throwable)
}
