package com.rider.afeezo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.interfaces.OnSelectedPlaceListener
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class GooglePlacesAutocompleteAdapter(
    context: Context, textViewResourceId: Int,
    itemListener: OnSelectedPlaceListener, placesClient: PlacesClient
) : ArrayAdapter<AutocompletePrediction>(context, textViewResourceId) {

    var inflater: LayoutInflater? = null
    var itemListener: OnSelectedPlaceListener
    var placeIdList: ArrayList<String>? = null
    private var resultList: List<AutocompletePrediction>? = null

    //private val context: Context? = null
    private val placesClient: PlacesClient
    override fun getCount(): Int {
        return if (resultList != null) resultList!!.size else 0
    }

    override fun getItem(index: Int): AutocompletePrediction {
        return resultList!![index]
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = inflater?.inflate(R.layout.adapter_google_places_autocomplete, null)
        }
        val holder = Holder()
        holder.place_txt = convertView?.findViewById(R.id.place_type)
        holder.place_type = convertView?.findViewById(R.id.place_txt)
        holder.ivType = convertView?.findViewById(R.id.ivType)
        holder.linearTop = convertView?.findViewById(R.id.linear_top)
        holder.ivType?.visibility = View.GONE
        val item = getItem(position)
        if (item != null) {
            holder.place_txt?.text = item.getPrimaryText(null)
            holder.place_type?.text = item.getSecondaryText(null)
        }
        holder.linearTop?.setOnClickListener {
            itemListener.onClickPlace(
                item.placeId,
                false,
                position
            )
        }
        return convertView!!
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val results = FilterResults()
                var filterData: List<AutocompletePrediction>? = ArrayList()
                filterData = getAutocomplete(charSequence)
                results.values = filterData
                if (filterData != null) {
                    results.count = filterData.size
                } else {
                    results.count = 0
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (results.count > 0) {
                    resultList = results.values as List<AutocompletePrediction>
                    //notifyDataSetChanged()
                }
                notifyDataSetChanged()
                /*else {
                        notifyDataSetInvalidated()
                    }*/
            }

            override fun convertResultToString(resultValue: Any): CharSequence {
                return if (resultValue is AutocompletePrediction) {
                    resultValue.getFullText(null)
                } else {
                    super.convertResultToString(resultValue)
                }
            }
        }
    }

    private fun getAutocomplete(constraint: CharSequence): MutableList<AutocompletePrediction>? {
        val requestBuilder = FindAutocompletePredictionsRequest.builder()
            .setQuery(constraint.toString())
            .setCountry(instance.getStore(context).getString(Constant.COUNRTY_CODE))
            .setSessionToken(AutocompleteSessionToken.newInstance())
        val results = placesClient.findAutocompletePredictions(requestBuilder.build())
//            .setCountry(instance.getStore(context).getString(Constant.COUNRTY_CODE))

        try {
            Tasks.await(results, 60, TimeUnit.SECONDS)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }
        return if (results.isSuccessful) {
            if (results.result != null) {
                results.result!!.autocompletePredictions
            } else null
        } else {
            null
        }
    }

    inner class Holder {
        var place_txt: TextView? = null
        var place_type: TextView? = null
        var ivType: ImageView? = null
        var linearTop: LinearLayout? = null
    }

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.itemListener = itemListener
        this.placesClient = placesClient
    }
}