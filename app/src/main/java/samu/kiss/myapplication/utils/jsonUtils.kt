package samu.kiss.myapplication.utils

import android.content.Context
import org.json.JSONObject
import samu.kiss.myapplication.models.Location

fun loadLocations(context : Context) : MutableList<Location> {
    val locations = mutableListOf<Location>()
    val json_string =
        context.assets.open("locations.json").bufferedReader().use {
            it.readText()
        }
    var json = JSONObject(json_string)
    var locationsJsonArray = json.getJSONArray("locationsArray")
    for (i in 0..locationsJsonArray.length()-1) {
        val jsonObject = locationsJsonArray.getJSONObject(i)
        val latitude = jsonObject.getDouble("latitude")
        val longitude = jsonObject.getDouble("longitude")
        val name = jsonObject.getString("name")
        val location = Location(latitude, longitude, name)
        locations.add(location)
    }
    return locations
}
