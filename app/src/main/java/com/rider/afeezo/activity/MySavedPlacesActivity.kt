package com.rider.afeezo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.rider.afeezo.R
import com.rider.afeezo.adapter.FavLocationSearchAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.interfaces.OnSelectedPlaceListener
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.FavLocation
import com.rider.afeezo.model.Locations
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_my_saved_places.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class MySavedPlacesActivity : BaseActivity(), ResponseHandler, OnSelectedPlaceListener {

    private var locationData: ArrayList<Locations>? = null
    private var searchAdapter: FavLocationSearchAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_saved_places)
        utility = Utility(this)
        setToolbar(base_activity_toolbar, resources.getString(R.string.saved_places))
        favAddress
    }

    private val addressData: Unit
        get() {
            locationData = ArrayList()
            val type = object : TypeToken<List<Locations?>?>() {}.type
            val gson = Gson()
            val data = instance.getStore(this).getString(Constant.FAV_LOCATION)
            if (data != "") {
                val arrayList = gson.fromJson<java.util.ArrayList<Locations>>(data, type)
                locationData?.addAll(arrayList)
            }
            if (locationData != null && locationData?.size!! > 0) {
                tvNoDataFound.visibility = View.GONE
                recyclerSavedPlaces.visibility = View.VISIBLE
                searchAdapter = FavLocationSearchAdapter(this, locationData!!, this, null)
                recyclerSavedPlaces.adapter = searchAdapter
            } else {
                showNoData()
            }
        }


    private fun showNoData() {
        tvNoDataFound.visibility = View.VISIBLE
        recyclerSavedPlaces.visibility = View.GONE
    }

    private val favAddress: Unit
        get() {
            showProgress(false)
            instance.getUserFavoriteLocations(
                Constant.GET_FAV_LOCATION, instance.token,
                this
            )
        }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.GET_FAV_LOCATION) {
            hideProgress()
            val favLocation = response.body() as FavLocation?
            if (favLocation != null) {
                if (favLocation.status.contentEquals("1") && favLocation.locations != null) {
                    val gson = Gson()
                    val arrayList = ArrayList<Locations>()
                    arrayList.addAll(favLocation.locations!!)
                    val list = gson.toJson(arrayList)
                    instance.getStore(this).saveString(Constant.FAV_LOCATION, list)
                    addressData
                } else {
                    showNoData()
                }
            } else {
                showNoData()
            }
        }
    }


    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    override fun onClickPlace(placeId: String?, isFavourite: Boolean, position: Int) {
        val resultIntent = Intent()
        resultIntent.putExtra(Constant.SAVED_CODE, position)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}