package com.rider.afeezo.interfaces


interface OnSelectedPlaceListener {
    fun onClickPlace(placeId: String?, isFavourite: Boolean, position: Int)
}