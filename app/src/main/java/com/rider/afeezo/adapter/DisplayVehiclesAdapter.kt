package com.rider.afeezo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rider.afeezo.R
import com.rider.afeezo.activity.MapFrontActivity
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.interfaces.OnCardRideSelected
import com.rider.afeezo.model.VehicleTypeEstimateArr
import com.rider.afeezo.model.VehiclesTypes
import kotlinx.android.synthetic.main.car_layout.view.*
import java.text.DecimalFormat

class DisplayVehiclesAdapter(
    private val finishParams: ArrayList<VehiclesTypes>?,
    private val activity: Context,
    private val currency: String?,
    private val listener: OnCardRideSelected
) : RecyclerView.Adapter<DisplayVehiclesAdapter.MyHolder>() {

    private var value = DecimalFormat("###.##")
    var onInfoClick : ((currency: String?,data:VehiclesTypes)->Unit)?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.car_layout, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return finishParams?.size ?:0
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = finishParams?.get(position)
        Utility.showImage(activity, holder.itemView.carImage, item?.icon)
        holder.itemView.carType.text = item?.name
        holder.itemView.carTypeDesc.text = item?.details

        holder.itemView.baseFare.text = currency +
            item?.vehicleTypeEstimateArr?.finalFare

        if (item?.isChecked == true) {
            holder.itemView.imgInfo.visibility = View.VISIBLE
            holder.itemView.cardRide.setCardBackgroundColor(activity.resources.getColor(R.color.colorBlackSelection))
        } else {
            holder.itemView.imgInfo.visibility = View.GONE
            holder.itemView.cardRide.setCardBackgroundColor(activity.resources.getColor(R.color.white))
        }
        holder.itemView.cardRide.setOnClickListener {
            clearAll()
            item?.isChecked = true
            (activity as MapFrontActivity).selectedVehicleId = item?.id
            activity.selectedFinalFare = currency +item?.vehicleTypeEstimateArr?.finalFare
            activity.cabType = item?.name
            item?.let { it1 -> listener.onSelectCardRide(it1) }
        }
        holder.itemView.imgInfo.setOnClickListener {
            onInfoClick?.invoke(currency,item!!)
        }
    }

    private fun clearAll() {
        if (finishParams != null) {
            for (item in finishParams) {
                item.isChecked = false
            }
        }
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

