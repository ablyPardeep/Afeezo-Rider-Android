package com.rider.afeezo.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.OperatingLocs
import com.rider.afeezo.model.VehicleTypeById
import com.rider.afeezo.model.Vehicles
import kotlinx.android.synthetic.main.activity_fares.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class FaresActivity : BaseActivity(), ResponseHandler, View.OnClickListener {
    private var countryNameCode = "IN"
    private var cityId: String? = null
    private var vehilceId: String? = null
    private var cityMap: MutableMap<String?, String?>? = null
    private var vehicleMap: MutableMap<String?, String?>? = null
    private var cities: ArrayList<String?>? = ArrayList()
    private var vehiclesList: ArrayList<String?>? = null
    private var vehiclesType: VehicleTypeById? = null
    private var checkedItem = 0
    private var defaultCheckedItem = -1
    private var checkedVehicelItem = 0
    private var defaultCheckedVehicleItem = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fares)
        cityMap = LinkedHashMap()
        vehicleMap = LinkedHashMap()
        vehicleTypeLt.setOnClickListener(this)
        cityLt.setOnClickListener(this)
        categoryLt.setOnClickListener(this)
        countryNameCode = instance.getStore(this).getString(Constant.COUNRTY_CODE)
        setToolbar(base_activity_toolbar, resources.getString(R.string.fares))
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        instance.getOperatingLocationsByCountryCode(
            Constant.GET_OPERATING_LOC,
            countryNameCode,
            instance.token,
            this
        )
    }


    override fun onSuccess(tag: Int, response: Response<*>) {
        if (tag == Constant.GET_OPERATING_LOC) {
            val operatingLocs = response.body() as OperatingLocs?
            if (operatingLocs != null && operatingLocs.status.contentEquals("1")) {
                if (operatingLocs.locations != null) {
                    for (locs in operatingLocs.locations!!) {
                        cities!!.add(locs.name)
                        cityMap!![locs.id] = locs.name
                    }
                }
            } else if (operatingLocs != null && operatingLocs.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this).sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.GET_VEHICLE_TYPES_BY_LOC_ID) {
            vehiclesList = ArrayList()
            val vehicles = response.body() as Vehicles?
            if (vehicles != null && vehicles.status.contentEquals("1")) {
                if (vehicles.vehiclesTypes != null) {
                    for (vehiclesTypes in vehicles.vehiclesTypes!!) {
                        vehiclesList!!.add(vehiclesTypes.name)
                        vehicleMap!![vehiclesTypes.id] = vehiclesTypes.name
                    }
                }
            } else if (vehicles != null && vehicles.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this).sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.GET_VEHICLE_TYPES_BY_VEHICLE_ID) {
            vehiclesType = response.body() as VehicleTypeById?
            if (vehiclesType != null && vehiclesType!!.status.contentEquals("1")) {
                baseFare.text =
                    vehiclesType!!.currencySymbol + vehiclesType!!.vehiclesType!!.vtype_base_fare
                addtimeFare.text = vehiclesType!!.currencySymbol + vehiclesType!!
                    .vehiclesType!!.vtype_price_per_min + " " + getString(R.string.per_min)
                minFare.text =
                    vehiclesType!!.currencySymbol + vehiclesType!!.vehiclesType!!.vtype_min_fare
                addKmFare.text =
                    vehiclesType!!.currencySymbol + vehiclesType!!.vehiclesType!!.vtype_price_per_km + " " + getString(
                        R.string.per_km
                    )
                cancelTime.text =
                    vehiclesType!!.vehiclesType!!.vtype_cancellation_minutes + " " + getString(R.string.min)
                cancelCharge.text =
                    vehiclesType!!.currencySymbol + vehiclesType!!.vehiclesType!!.vtype_cancellation_charge
                if (!expandable_layout.isExpanded) {
                    vehicleType.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.up_arrow,
                        0
                    )
                    expandable_layout.expand()
                }
            } else if (vehiclesType != null && vehiclesType!!.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this).sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cityLt -> if (cities != null) setSingleChoiceDialog(cities!!, selectedCity)
            R.id.categoryLt -> if (vehiclesList != null) setSingleChoiceDialogForVehicles(
                vehiclesList!!, selectedCategory
            ) else showToast(this, getString(R.string.please_select_city_first))
            R.id.vehicleTypeLt -> if (vehiclesType != null) {
                if (expandable_layout.isExpanded) {
                    vehicleType!!.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.down_arrow,
                        0
                    )
                    expandable_layout.collapse()
                } else {
                    vehicleType!!.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.up_arrow,
                        0
                    )
                    expandable_layout.expand()
                }
            }
        }
    }


    /**
     * method used to set single choice dialog
     * @param choiceType
     * @param view
     */
    fun setSingleChoiceDialog(choiceType: ArrayList<String?>, view: TextView?) {
        val mBuilder = AlertDialog.Builder(this, R.style.MaterialThemeDialog)
        mBuilder.setTitle(getString(R.string.choose) + view!!.text.toString())
        checkedItem = if (defaultCheckedItem == -1) -1 else defaultCheckedItem
        mBuilder.setSingleChoiceItems(
            choiceType.toTypedArray(),
            checkedItem,
            DialogInterface.OnClickListener { dialogInterface, i ->
                defaultCheckedItem = i
                defaultCheckedVehicleItem = -1
                selectedCategory.text = ""
                vehicleType.text = ""
                vehicleType.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.down_arrow,
                    0
                )
                expandable_layout.collapse()
                vehiclesType = null
                view.text = choiceType[i]
                txtCityLabel.setTextColor(resources.getColor(android.R.color.tab_indicator_text))
                txtCategoryLabel.setTextColor(ContextCompat.getColor(this, R.color.g_plus_dark))
                if (view.id == selectedCity.id) {
                    for ((key, value) in cityMap!!) {
                        if (view.text.toString() == value) {
                            cityId = key
                        }
                    }
                    if (!isNetworkConnected(this@FaresActivity)) {
                        showErrorDialog(this@FaresActivity)
                        return@OnClickListener
                    }
                    instance.getVehicleTypesByLocationId(
                        Constant.GET_VEHICLE_TYPES_BY_LOC_ID,
                        cityId,
                        instance.token,
                        this
                    )
                }
                dialogInterface.dismiss()
            })
        val mDialog = mBuilder.create()
        mDialog.show()
    }

    /**
     * method used to set single choice dialog for vehicles
     */
    private fun setSingleChoiceDialogForVehicles(choiceType: ArrayList<String?>, view: TextView?) {
        val mBuilder = AlertDialog.Builder(this, R.style.MaterialThemeDialog)
        mBuilder.setTitle("Choose " + view!!.text.toString())
        checkedVehicelItem = if (defaultCheckedVehicleItem == -1) -1 else defaultCheckedVehicleItem
        mBuilder.setSingleChoiceItems(
            choiceType.toTypedArray(),
            checkedVehicelItem,
            DialogInterface.OnClickListener { dialogInterface, i ->
                view.text = choiceType[i]
                defaultCheckedVehicleItem = i
                if (view.id == selectedCategory.id) {
                    view.text = choiceType[i]
                    vehicleType!!.text = choiceType[i]
                    txtCategoryLabel!!.setTextColor(resources.getColor(android.R.color.tab_indicator_text))
                    for ((key, value) in vehicleMap!!) {
                        if (view.text.toString() == value) {
                            vehilceId = key
                        }
                    }
                    if (!isNetworkConnected(this@FaresActivity)) {
                        showErrorDialog(this@FaresActivity)
                        return@OnClickListener
                    }
                    instance.getVehicleTypesById(
                        Constant.GET_VEHICLE_TYPES_BY_VEHICLE_ID,
                        vehilceId,
                        instance.token,
                        this
                    )
                }
                dialogInterface.dismiss()
            })
        val mDialog = mBuilder.create()
        mDialog.show()
    }
}