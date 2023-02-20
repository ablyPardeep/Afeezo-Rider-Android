package com.rider.afeezo.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.activity.SelectedRideDetail
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.model.Records.Rides
import kotlinx.android.synthetic.main.item_ride_list_2.view.*

class RideListAdapter(
    var activity: Activity,
    var rideData: ArrayList<Rides>,
    var currencySymbol: String,
    val listener: OnClickEventListener
) : RecyclerView.Adapter<RideListAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_ride_list_2, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val myRides = rideData[position]
        holder.itemView.driverName.text = myRides.driver_name
        holder.itemView.tripRating.text = myRides.driver_rating
        Utility.showImage(activity, holder.itemView.driverImg, myRides.driver_image)
        val status = currencySymbol + myRides.total_fare
        holder.itemView.tv_tripFare.text = status
        holder.itemView.tvTripStatus.text = myRides.status
        if (myRides.status == "Cancelled") {
            holder.itemView.tvTripStatus.setTextColor(activity.resources.getColor(R.color.wallet_red_color))
        } else {
            holder.itemView.tvTripStatus.setTextColor(activity.resources.getColor(R.color.black))
        }
        holder.itemView.tv_tripDateTime.text = Utility.changeTimeFormat(myRides.booked_time!!)
        holder.itemView.tv_startLocation.text = myRides.start_location
        holder.itemView.tv_endLocation.text = myRides.end_location
    }

    override fun getItemCount(): Int {
        return rideData.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {
            listener.onRideClick(rideId = rideData[bindingAdapterPosition].id, bookingId = rideData[bindingAdapterPosition].booking_id)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}

interface OnClickEventListener {

    fun onRideClick(rideId: String? = "", bookingId: String? = "") {}

}