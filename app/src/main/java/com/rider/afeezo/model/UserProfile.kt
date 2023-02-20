package com.rider.afeezo.model

import java.io.Serializable

class UserProfile : Serializable {
    var currencySymbol: String? = null
    var status: String? = null
    var data: UserProfileData? = null

}
