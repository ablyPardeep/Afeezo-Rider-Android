package com.rider.afeezo.model

class VehiclesType {
    var vtype_price_per_min: String? = null
    var vtype_night_surcharge_enabled: String? = null
    var vtype_cancellation_minutes: String? = null
    var vtype_base_fare_kms: String? = null
    var vtype_night_start_time: String? = null
    var vtype_id: String? = null
    var vtype_peak_surcharge: String? = null
    var vtype_cancellation_charge: String? = null
    var vtype_seats: String? = null
    var vtype_surcharge_multiply_factor: String? = null
    var vtype_base_fare: String? = null
    var peak_surcharge_details: ArrayList<Peak_surcharge_details>? = null
    var vtype_name: String? = null
    var vtype_price_per_km: String? = null
    var vtype_min_fare: String? = null
    var vtype_night_end_time: String? = null

    override fun toString(): String {
        return "ClassPojo [vtype_price_per_min = $vtype_price_per_min, vtype_night_surcharge_enabled = $vtype_night_surcharge_enabled, vtype_cancellation_minutes = $vtype_cancellation_minutes, vtype_base_fare_kms = $vtype_base_fare_kms, vtype_night_start_time = $vtype_night_start_time, vtype_id = $vtype_id, vtype_peak_surcharge = $vtype_peak_surcharge, vtype_cancellation_charge = $vtype_cancellation_charge, vtype_seats = $vtype_seats, vtype_surcharge_multiply_factor = $vtype_surcharge_multiply_factor, vtype_base_fare = $vtype_base_fare, peak_surcharge_details = $peak_surcharge_details, vtype_name = $vtype_name, vtype_price_per_km = $vtype_price_per_km, vtype_min_fare = $vtype_min_fare, vtype_night_end_time = $vtype_night_end_time]"
    }

    inner class Peak_surcharge_details {
        var surcharge_multiply_factor: String? = null
        var surcharge_end_time: String? = null
        var surcharge_vehicle_type: String? = null
        var surcharge_day: String? = null
        var surcharge_start_time: String? = null
        var surcharge_id: String? = null

        override fun toString(): String {
            return "ClassPojo [surcharge_multiply_factor = $surcharge_multiply_factor, surcharge_end_time = $surcharge_end_time, surcharge_vehicle_type = $surcharge_vehicle_type, surcharge_day = $surcharge_day, surcharge_start_time = $surcharge_start_time, surcharge_id = $surcharge_id]"
        }
    }
}