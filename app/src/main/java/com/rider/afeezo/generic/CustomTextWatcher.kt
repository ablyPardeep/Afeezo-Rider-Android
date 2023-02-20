package com.rider.afeezo.generic

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.android.material.textfield.TextInputLayout


class CustomTextWatcher(view: View, textInputLayout: TextInputLayout) : TextWatcher {

    var view: View? = null
    var textInputLayout: TextInputLayout? = null

    init {
        this.view = view
        this.textInputLayout = textInputLayout
    }

    override fun beforeTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence?, i: Int, i1: Int, i2: Int) {}

    override fun afterTextChanged(editable: Editable?) {
        textInputLayout!!.error = null
    }
}
