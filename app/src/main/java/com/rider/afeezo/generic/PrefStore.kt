package com.rider.afeezo.generic

import android.content.Context
import android.content.SharedPreferences

class PrefStore(act: Context?) {
    var MyPREFERENCES = javaClass.simpleName
    var sharedpreferences: SharedPreferences? = null

    init {
        sharedpreferences = act?.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
    }

    fun saveString(key: String, value: String) {
        val editor = sharedpreferences?.edit()
        editor?.putString(key, value)
        editor?.apply()
    }

    fun <T> saveSet(key: String, list: List<String>) {
        val editor = sharedpreferences?.edit()
        val set: MutableSet<String> = HashSet()
        set.addAll(list)
        editor?.putStringSet(key, set)
        editor?.apply()
    }

    fun getString(key: String): String {
        return sharedpreferences?.getString(key, "") ?: ""
    }

    fun saveBoolean(key: String, value: Boolean) {
        val editor = sharedpreferences?.edit()
        editor?.putBoolean(key, value)
        editor?.apply()
    }

    fun getBoolean(key: String, value: Boolean): Boolean {
        return sharedpreferences?.getBoolean(key, value) ?: false
    }

    fun saveLong(key: String, value: Long) {
        val editor = sharedpreferences?.edit()
        editor?.putLong(key, value)
        editor?.apply()
    }

    fun getLong(key: String): Long {
        return sharedpreferences?.getLong(key, 0L) ?: 0L
    }

    fun clear(key: String) {
        sharedpreferences?.edit()?.remove(key)?.apply()
    }

}
