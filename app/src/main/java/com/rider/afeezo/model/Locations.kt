package com.rider.afeezo.model

class Locations {
    var id: String? = null
    var date_added: String? = null
    var name: String? = null
    var longitude: String? = null
    var latitude: String? = null
    var google_loc_name: String? = null
    var country: String? = null
    override fun toString(): String {
        return "ClassPojo [id = $id, date_added = $date_added, name = $name, longitude = $longitude, latitude = $latitude, google_loc_name = $google_loc_name]"
    }
}
