package com.rider.afeezo.generic

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DataParser {

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude  */
    fun parse(jObject: JSONObject): List<List<HashMap<String, String>>> {
        val routes: MutableList<List<HashMap<String, String>>> = ArrayList()
        val jRoutes: JSONArray
        var jLegs: JSONArray
        var jSteps: JSONArray
        var jDistance: JSONObject? = null
        var jDuration: JSONObject? = null
        try {
            jRoutes = jObject.getJSONArray("routes")
            /** Traversing all routes  */
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
                val path: MutableList<Any> = ArrayList()
                /** Traversing all legs  */
                for (j in 0 until jLegs.length()) {
                    jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                    jDistance = (jLegs[j] as JSONObject).getJSONObject("distance")
                    val hmDistance = HashMap<String, String>()
                    hmDistance["distance"] = jDistance.getString("text")
                    /** Getting duration from the json data  */
                    jDuration = (jLegs[j] as JSONObject).getJSONObject("duration")
                    val hmDuration = HashMap<String, String>()
                    hmDuration["duration"] = jDuration.getString("text")
                    /** Adding distance object to the path  */
                    path.add(hmDistance)
                    /** Adding duration object to the path  */
                    path.add(hmDuration)
                    /** Traversing all steps  */
                    for (k in 0 until jSteps.length()) {
                        var polyline = ""
                        polyline =
                            ((jSteps[k] as JSONObject)["polyline"] as JSONObject)["points"] as String
                        val list = decodePoly(polyline)
                        /** Traversing all points  */
                        for (l in list.indices) {
                            val hm = HashMap<String, String>()
                            hm["lat"] = list[l].latitude.toString()
                            hm["lng"] = list[l].longitude.toString()
                            path.add(hm)
                        }
                    }
                    routes.add(path as List<HashMap<String, String>>)
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
        }
        return routes
    }


    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

    fun parse(jsonData: String): List<HashMap<String, String>> {
        var jsonArray: JSONArray? = null
        val jsonObject: JSONObject
        try {
            Log.d("Places", "parse")
            jsonObject = JSONObject(jsonData)
            jsonArray = jsonObject.getJSONArray("results")
        } catch (e: JSONException) {
            Log.d("Places", "parse error")
            e.printStackTrace()
        }
        return getPlaces(jsonArray)
    }

    private fun getPlaces(jsonArray: JSONArray?): List<HashMap<String, String>> {
        val placesCount = jsonArray?.length()
        val placesList: MutableList<HashMap<String, String>> = ArrayList()
        var placeMap: HashMap<String, String>? = null
        Log.d("Places", "getPlaces")
        for (i in 0 until (placesCount ?: 0)) {
            try {
                placeMap = getPlace(jsonArray!![i] as JSONObject)
                placesList.add(placeMap)
                Log.d("Places", "Adding places")
            } catch (e: JSONException) {
                Log.d("Places", "Error in Adding places")
                e.printStackTrace()
            }
        }
        return placesList
    }

    private fun getPlace(googlePlaceJson: JSONObject): HashMap<String, String> {
        val googlePlaceMap = HashMap<String, String>()
        var placeName: String? = "-NA-"
        var vicinity: String? = "-NA-"
        var latitude: String? = ""
        var longitude: String? = ""
        var reference: String? = ""
        var icon: String? = "-NA-"
        Log.d("getPlace", "Entered")
        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name")
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity")
            }
            if (!googlePlaceJson.isNull("icon")) {
                icon = googlePlaceJson.getString("icon")
            }
            latitude =
                googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat")
            longitude =
                googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng")
            reference = googlePlaceJson.getString("reference")
            googlePlaceMap["place_name"] = placeName ?: ""
            googlePlaceMap["vicinity"] = vicinity ?: ""
            googlePlaceMap["lat"] = latitude
            googlePlaceMap["lng"] = longitude
            googlePlaceMap["reference"] = reference
            googlePlaceMap["icon"] = icon ?: ""
            Log.d("getPlace", "Putting Places")
        } catch (e: JSONException) {
            Log.d("getPlace", "Error")
            e.printStackTrace()
        }
        return googlePlaceMap
    }
}

