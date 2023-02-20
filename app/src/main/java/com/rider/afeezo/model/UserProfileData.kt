package com.rider.afeezo.model

import java.io.Serializable

class UserProfileData :Serializable{

    var phone: String? = null
    var balance: String? = null
    var points: String? = null
    var country_id: String? = null
    var state: String? = null
    var reg_date: String? = null
    var country: String? = null
    var city: String? = null
    var state_id: String? = null
    var address_line_1: String? = null
    var email: String? = null
    var postal_code: String? = null
    var name: String? = null
    var address_line_2: String? = null
    var gender: String? = null
    var user_id: String? = null
    var user_image: String? = null
    var last_completed_ride: RecentRide? = null

    override fun toString(): String {
        return "ClassPojo [phone = $phone, country_id = $country_id, state = $state, reg_date = $reg_date, country = $country, city = $city, state_id = $state_id, address_line_1 = $address_line_1, email = $email, postal_code = $postal_code, name = $name, address_line_2 = $address_line_2, gender = $gender, user_id = $user_id, user_image = $user_image]"
    }
}
