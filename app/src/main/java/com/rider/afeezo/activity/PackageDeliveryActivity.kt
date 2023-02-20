package com.rider.afeezo.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.databinding.DataBindingUtil
import com.rider.afeezo.R
import com.rider.afeezo.databinding.ActivityPackageDeliveryBinding
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.model.Contacts
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_add_recipient.*
import kotlinx.android.synthetic.main.dialog_add_recipient.view.*
import kotlinx.android.synthetic.main.dialog_add_recipient.view.submitBtn
import kotlinx.android.synthetic.main.dialog_note_driver.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class PackageDeliveryActivity : BaseActivity() ,View.OnClickListener{
    lateinit var binding: ActivityPackageDeliveryBinding
    private var pickupLocation = ""
    private var pickupLatLng :LatLng?=null
    private var endLatLng :LatLng?=null
    private var endLocation = ""
    private var latitude = ""
    private var longitude = ""
    private var bottomSheetDialog: BottomSheetDialog? = null
    var view: View?=null

    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == Constant.CONTACT_CODE) {
                        val contactData = data?.data
                        val c = contentResolver.query(contactData!!, null, null, null, null)
                        if (c!!.moveToFirst()) {
                            val contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
                            val hasNumber =
                                c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                            if (Integer.valueOf(hasNumber) == 1) {
                                val numbers = contentResolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                    null,
                                    null
                                )
                                if (numbers!!.moveToFirst()) {
                                    do {
                                       val num =
                                            numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                        val name  =
                                            numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                                        /** set name and number on fields */
                                        view?.etName?.setText(name)
                                        view?.etPhone?.setText(num)
                                    } while (numbers.moveToNext())
                                }

                            } else Utility.showToast(this, getString(R.string.no_contact_found))
                        }

                } else {
                data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    latitude = place.latLng?.latitude.toString()
                    longitude = place.latLng?.longitude.toString()

                    if (requestCode == 101)
                        binding.tvPickAddress.text = place.name
                    else if (requestCode == 102)
                        binding.tvDropOffAddress.text = place.name
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_package_delivery)

        custom_toolbar_title.text = resources.getString(R.string.review_delivery)
        backBtn.setOnClickListener(this)
        binding.tvPickAddress.setOnClickListener(this)
        binding.tvDropOffAddress.setOnClickListener(this)
        binding.pickupAddRecipientLayout.setOnClickListener(this)
        binding.dropoffAddRecipientLayout.setOnClickListener(this)
        binding.pickupNoteLayout.setOnClickListener(this)
        binding.dropoffNoteLayout.setOnClickListener(this)



        pickupLocation = intent.getStringExtra("pickupName")!!
//        endLocation = intent.getStringExtra("endLocName")!!
        pickupLatLng = (intent.extras?.get("pickupLatLng") as LatLng)

        binding.tvPickAddress.text = pickupLocation
        binding.radio0.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.radio0.setTextColor(resources.getColor(R.color.white))
                binding.radio1.setTextColor(resources.getColor(R.color.black))
                binding.tvPickAddress.text = pickupLocation
                binding.tvDropOffAddress.text = ""
            }
        }

        binding.radio1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.radio0.setTextColor(resources.getColor(R.color.black))
                binding.radio1.setTextColor(resources.getColor(R.color.white))
                binding.tvPickAddress.text = ""
                binding.tvDropOffAddress.text = pickupLocation
            }
        }
    }

    private fun setLocation(requestCode: Int){
        // Set the fields to specify which types of place data to
// return after the user has made a selection.
        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.ADDRESS
        )

// Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setCountry(DataHolder.instance.getStore(this).getString(Constant.COUNRTY_CODE))
            .build(this)
        startActivityForResult(intent,requestCode)
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.backBtn ->{
                finish()
            }
            R.id.tvDropOffAddress ->{
                setLocation(102)
            }
            R.id.tvPickAddress ->{
                setLocation(101)
            }
            R.id.pickupAddRecipientLayout ->{
                addRecipient("pickup")
            }
            R.id.dropoffAddRecipientLayout ->{
                addRecipient("dropoff")
            }
            R.id.pickupNoteLayout ->{
                addNote("pickup")
            }
            R.id.dropoffNoteLayout ->{
                addNote("dropoff")
            }
        }
    }

    private fun addRecipient(from :String) {
         view = layoutInflater.inflate(R.layout.dialog_add_recipient, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view!!)

        view?.submitBtn?.setOnClickListener {
            if (view?.etName?.text.toString().trim().isEmpty())
                view?.etName?.error = getString(R.string.please_enter_name)
            else if (view?.etPhone?.text.toString().trim().isEmpty())
                view?.etPhone?.error = getString(R.string.please_enter_phone_number)
            else if (from == "pickup") {
                binding.tvAddReceipnt.text = view?.etName?.text.toString()
                binding.tvConfirmDetails.text = view?.etPhone?.text.toString()
                bottomSheetDialog?.dismiss()
            }else{
                binding.tvAddReceipntDropOff.text = view?.etName?.text.toString()
                binding.tvConfirmDetailsDropOff.text = view?.etPhone?.text.toString()
                bottomSheetDialog?.dismiss()
            }
        }

        /** set click on contacts button */
         view?.contacts?.setOnClickListener {
             contactsData
         }
        bottomSheetDialog?.show()
    }

    /** bottom sheet method for add note layout*/

    private fun addNote(from :String) {
        val view: View = layoutInflater.inflate(R.layout.dialog_note_driver, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)

        view.submitBtn.setOnClickListener {
            if (view.etNotes.text.toString().trim().isEmpty())
                view.etNotes.error = getString(R.string.enter_message)

            else if (from == "pickup") {
                binding.tvAddNoteToDriver.text = view.etNotes.text.toString()
                bottomSheetDialog?.dismiss()
            }else{
                binding.tvAddNoteToDriverDropOff.text = view.etNotes.text.toString()
                bottomSheetDialog?.dismiss()
            }
        }

        bottomSheetDialog?.show()
    }

    /**
     * method used to get contact data from device
     */
    private val contactsData: Unit
        get() {
            val coarseLoc =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            if (coarseLoc != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    1
                )
            } else {
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(intent, Constant.CONTACT_CODE)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(intent, Constant.CONTACT_CODE)
        }
    }
}