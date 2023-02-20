package com.rider.afeezo.model

class Records {
    var rides: ArrayList<Rides>? = null
    var paging: Paging? = null
    var coupons: ArrayList<Coupons>? = null

    override fun toString(): String {
        return "ClassPojo [rides = $rides, paging = $paging]"
    }

    inner class Rides {
        var driver_rating: String? = null
        var booking_id: String? = null
        var driver_image: String? = null
        var vehicle_type: String? = null
        var driver_name: String? = null
        var travel_time_fare: String? = null
        var fare: String? = null
        var vehicle_id: String? = null
        var vehicle_make: String? = null
        var id: String? = null
        var distance_kms: String? = null
        var rounded_off: String? = null
        var time_minutes: String? = null
        var vehicle_number: String? = null
        var driver_id: String? = null
        var rider_name: String? = null
        var end_location: String? = null
        var status: String? = null
        var start_location: String? = null
        var cancelled_time: String? = null
        var rider_phone: String? = null
        var surcharge_multiplier: String? = null
        var booked_time: String? = null
        var surcharge: String? = null
        var total_fare: String? = null
        var base_fare: String? = null
        var tax: String? = null
        var end_time: String? = null
        var vehicle_type_name: String? = null
        var vehicle_model: String? = null
        var start_time: String? = null

        override fun toString(): String {
            return "ClassPojo [booking_id = $booking_id, vehicle_type = $vehicle_type, driver_name = $driver_name, travel_time_fare = $travel_time_fare, fare = $fare, vehicle_id = $vehicle_id, vehicle_make = $vehicle_make, id = $id, distance_kms = $distance_kms, rounded_off = $rounded_off, time_minutes = $time_minutes, vehicle_number = $vehicle_number, driver_id = $driver_id, rider_name = $rider_name, end_location = $end_location, status = $status, start_location = $start_location, cancelled_time = $cancelled_time, rider_phone = $rider_phone, surcharge_multiplier = $surcharge_multiplier, booked_time = $booked_time, surcharge = $surcharge, total_fare = $total_fare, base_fare = $base_fare, tax = $tax, end_time = $end_time, vehicle_type_name = $vehicle_type_name, vehicle_model = $vehicle_model, start_time = $start_time]"
        }
    }
}