package com.rider.afeezo.model

import java.io.Serializable

class RideDetail : Serializable {
    var vtype_night_surcharge_enabled: String? = null
    val driver_image: String? = null
    var ride_previous_outstanding: String? = null
    var ride_rider_name: String? = null
    var ride_cancellation_charges: String? = null
    var ride_base_fare_after_reward_points: String? = null
    val driverRating: String? = null
    val map: String? = null
    val icon: String? = null
    val payments: ArrayList<Payments>? = null
    val financials: ArrayList<Financials>? = null
    var ride_discount: String? = null
    var driver_phone: String? = null
    var vtype_commission_perc: String? = null
    var ride_reached_pickup_point_time: String? = null
    var ride_vehicle_id: String? = null
    val vtype_id: String? = null
    val ride_otp: String? = null
    var vtype_active: String? = null
    var ride_booking_id: String? = null
    var driver_email: String? = null
    var ride_distance_kms: String? = null
    var vtype_surcharge_multiply_factor: String? = null
    val ride_time_minutes: String? = null
    var ride_company_name: String? = null
    var driver_company_id: String? = null
    val ride_end_location: String? = null
    var ride_travel_time_fare: String? = null
    var driver_address_line_2: String? = null
    val driver_id: String? = null // ride detail api
    val ride_start_location: String? = null
    var driver_active: String? = null
    var driver_address_line_1: String? = null
    var vtype_min_fare: String? = null
    val ride_id: String? = null
    var ride_surcharge: String? = null
    var vtype_price_per_min: String? = null
    var ride_fare: String? = null
    var vtype_cancellation_minutes: String? = null
    var driver_country: String? = null
    val ride_total_fare: String? = null
    val ride_booked_time: String? = null
    var driver_state: String? = null
    var driver_postal_code: String? = null
    var driver_is_approved: String? = null
    val ride_driver_id: String? = null // active ride api
    var vtype_name: String? = null
    var ride_rounded_off: String? = null
    var vtype_price_per_km: String? = null
    var ride_cancelled_by: String? = null
    val ride_vehicle_type_name: String? = null
    val ride_vehicle_model: String? = null
    var ride_end_time: String? = null
    var vtype_base_fare_kms: String? = null
    var driver_name: String? = null
    var vtype_night_start_time: String? = null
    var vtype_location: String? = null
    var driver_gender: String? = null
    var vtype_cancellation_charge: String? = null
    var vtype_seats: String? = null
    var ride_vehicle_type: String? = null
    val cancellation_charges: String? = null
    val ride_cancel_msg_text: String? = null
    val ride_driver_name: String? = null
    val ride_driver_phone: String? = null
    val ride_vehicle_make: String? = null
    val ride_surcharge_multiplier: String? = null
    var ride_base_fare: String? = null
    var ride_cancelled_by_id: String? = null
    var vtype_base_fare: String? = null
    var ride_surcharge_type: String? = null
    val ride_status: String? = null
    var driver_online: String? = null
    var vtype_peak_surcharge: String? = null
    var ride_rider_phone: String? = null
    var ride_rider_id: String? = null
    var ride_tax: String? = null
    val ride_status_name: String? = null
    var ride_cancelled_time: String? = null
    var ride_cancelled_minutes: String? = null
    var ride_start_time: String? = null
    val ride_vehicle_number: String? = null
    var driver_app_access_token: String? = null
    var ride_initial_waiting_minutes: String? = null
    var driver_is_deleted: String? = null
    var driver_city: String? = null
    var driver_reg_date: String? = null
    var vtype_night_end_time: String? = null
    val src_lat: String? = null
    val src_lng: String? = null
    val dest_lat: String? = null
    val dest_lng: String? = null
    val wallet: String? = null
    val driver_lat: String? = null
    val driver_lng: String? = null
    val cash: String? = null
    val ride_payment_mode: String? = null
    val ride_payment_status: String? = null
    val rideEstimates: RideEstimates? = null



    inner class RideEstimates {
        var distance: String? = null
        var time: String? = null
        var ridefare: String? = null
        var travel_time_fare: String? = null
        var surcharge: String? = null
        var base_fare: String? = null
        var multiplier: String? = null
        var surchargeType: String? = null
    }
    inner class Payments {
        var rp_payment_mode: String? = null
        var rp_date: String? = null
        var rp_ride_id: String? = null
        var rp_payment_id: String? = null
        var rp_amount: String? = null
    }

    inner class Financials {
        var key: String? = null
        var sign: String? = null
        var value: String? = null
    }
}