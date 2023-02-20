package com.rider.afeezo.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.activity.BaseActivity
import com.rider.afeezo.activity.MapFrontActivity
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.interfaces.OnSelectedPlaceListener
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.interfaces.onFindLocation
import com.rider.afeezo.model.Common
import com.rider.afeezo.model.Locations
import com.google.gson.Gson
import kotlinx.android.synthetic.main.adapter_google_places_autocomplete.view.*
import retrofit2.Response

class FavLocationSearchAdapter(
    val activity: Activity,
    val locations: ArrayList<Locations>,
    val itemListener: OnSelectedPlaceListener?,
    val findLocation: onFindLocation?
) : RecyclerView.Adapter<FavLocationSearchAdapter.Holder>(), Filterable {
    var filterdlocations: ArrayList<Locations>?
    var locationFilter: LocationFilter? = null

    override fun getItemCount(): Int {
        return filterdlocations!!.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getFilter(): Filter {
        if (locationFilter == null) {
            locationFilter = LocationFilter()
        }
        return locationFilter!!
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class LocationFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            if (constraint != null && constraint.isNotEmpty()) {
                val tempList = ArrayList<Locations>()
                for (location in locations) {
                    if (location.name!!.lowercase().contains(constraint.toString().lowercase())
                        || location.google_loc_name!!.contains(constraint.toString().lowercase())
                    ) {
                        tempList.add(location)
                    }
                }
                filterResults.count = tempList.size
                filterResults.values = tempList
            } else {
                filterResults.count = locations.size
                filterResults.values = locations
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            filterdlocations = results.values as ArrayList<Locations>
            findLocation?.onFind(
                filterdlocations != null && filterdlocations!!.size > 0,
                results.count
            )
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(activity)
                .inflate(R.layout.adapter_google_places_autocomplete, parent, false)
        return Holder(view)
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.itemView.place_type.visibility = View.VISIBLE
        holder.itemView.removeFavouriteBtn.setOnClickListener {
            deleteAddressData(
                locations[position].id, position
            )
        }
        if (findLocation == null) {
            holder.itemView.ivDelete.visibility = View.VISIBLE
        } else {
            holder.itemView.removeFavouriteBtn.visibility = View.VISIBLE
        }
        when (locations[position].name!!.lowercase()) {
            Constant.HOME_TEXT -> holder.itemView.ivType.setImageResource(R.drawable.ic_home)
            Constant.WORK_TEXT -> holder.itemView.ivType.setImageResource(R.drawable.ic_work)
            else -> holder.itemView.ivType.setImageResource(R.drawable.ic_saved)
        }
        holder.itemView.place_type.text = locations[position].name
        holder.itemView.place_txt.text = locations[position].google_loc_name
        holder.itemView.setOnClickListener {
            itemListener?.onClickPlace(null, true, position)
        }
        holder.itemView.ivDelete.setOnClickListener {
            showDialog(
                activity,
                activity.resources.getString(R.string.are_you_sure_to_remove_address),
                locations[holder.adapterPosition].id,
                holder.adapterPosition
            )
        }
    }

    private fun showDialog(context: Context, msg: String?, id: String?, position: Int) {
        val b = AlertDialog.Builder(context, R.style.MaterialThemeDialog)
        b.setTitle(context.getString(R.string.message))
        b.setMessage(msg)
        b.setCancelable(false)
        b.setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
            deleteAddressData(id, position)
            dialog.cancel()
        }
        b.setNegativeButton(context.getString(R.string.no)) { dialog, _ ->
            dialog.cancel()
        }
        val alert11 = b.create()
        alert11.show()
    }

    private fun deleteAddressData(id: String?, pos: Int) {
        if (!isNetworkConnected(activity)) {
            showErrorDialog(activity)
            return
        }
        (activity as BaseActivity).showProgress(false)
        instance.deleteFavoriteLocation(
            Constant.DELETE_FAV_LOCATION, instance.token, id,
            object : ResponseHandler {
                override fun onSuccess(tag: Int, response: Response<*>) {
                    if (tag == Constant.DELETE_FAV_LOCATION) {
                        val common = response.body() as Common?
                        if (common != null) {
                            when {
                                common.status.contentEquals("1") -> {
                                    val gson = Gson()
                                    Utility.showToast(activity, common.msg!!)
                                    try {
                                        locations.removeAt(pos)
                                        val list = gson.toJson(locations)
                                        instance.getStore(
                                            activity
                                        ).saveString(Constant.FAV_LOCATION, list)
                                        if (activity is MapFrontActivity) {
                                            activity.checkFavSrcDestAddress(id)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    notifyItemRemoved(pos)
                                }
                                common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                                    Utility(activity).sessionExpire(activity)
                                }
                                else -> {
                                    Utility.showToast(activity, common.msg!!)
                                }
                            }
                            activity.hideProgress()
                        } else Utility.showToast(
                            activity,
                            activity.getString(R.string.error_something_wrong)
                        )
                    }
                }

                override fun onError(throwable: Throwable) {
                    activity.hideProgress()
                }
            })
    }

    init {
        filterdlocations = locations
    }
}