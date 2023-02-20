package com.rider.afeezo.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.ContactAdapter
import com.rider.afeezo.adapter.EmergencyContactAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.interfaces.onClickListItemListener
import com.rider.afeezo.interfaces.onDeleteItemListener
import com.rider.afeezo.model.Common
import com.rider.afeezo.model.Contacts
import com.rider.afeezo.model.EmergencyContacts
import kotlinx.android.synthetic.main.activity_emergency_contact.*
import kotlinx.android.synthetic.main.layout_contacts.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

/**
 * Created by android on 21/3/18.
 */
class EmergencyContactActivity : BaseActivity(), ResponseHandler, View.OnClickListener,
    onDeleteItemListener, onClickListItemListener {
    private var position = ""
    private var lastNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contact)
        utility = Utility(this)
        addContactLt.setOnClickListener(this)
        setToolbar(base_activity_toolbar, resources.getString(R.string.emergency_contacts))
        contactData
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.CONTACT_CODE) {
            if (resultCode == RESULT_OK) {
                val contactData = data!!.data
                val c = contentResolver.query(contactData!!, null, null, null, null)
                var num = ""
                var name = ""
                if (c!!.moveToFirst()) {
                    val contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
                    val hasNumber =
                        c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    val phoneContacts = ArrayList<Contacts>()
                    var isFilledContactList = false
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
                                val contacts = Contacts()
                                num =
                                    numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                name =
                                    numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                                /* val photo =
                                      numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.))
                                */  println("num = [$num], name = [$name")
                                if (!isFilledContactList) {
                                    lastNumber = num
                                    isFilledContactList = true
                                    contacts.number = num
                                    contacts.name = name
                                    phoneContacts.add(contacts)
                                } else {
                                    if (lastNumber != num) {
                                        contacts.number = num
                                        contacts.name = name
                                        phoneContacts.add(contacts)
                                    }
                                }
                            } while (numbers.moveToNext())
                        }
                        setContactChoiceDialog(phoneContacts)
                    } else showToast(this, getString(R.string.no_contact_found))
                }
            }
        }
    }

    /**
     * method used to set contact choice dialog
     * @param phoneContacts
     */
    private fun setContactChoiceDialog(phoneContacts: ArrayList<Contacts>?) {
        try {
            if (phoneContacts != null) {
                if (phoneContacts.size > 1) {
                    val builder = AlertDialog.Builder(this, R.style.MaterialThemeDialog)
                    val li = LayoutInflater.from(this)
                    val promptsView = li.inflate(R.layout.layout_contacts, null)
                    builder.setTitle(getString(R.string.choose) + " " + getString(R.string.contact))
                    builder.setView(promptsView)
                    val adapter = ContactAdapter(this, phoneContacts, this)
                    promptsView.contactList.adapter = adapter
                    promptsView.contactList.layoutManager = LinearLayoutManager(this)
                    val dialog = builder.create()
                    dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
                    dialog.show()
                    promptsView.dialogButtonOK.setOnClickListener(View.OnClickListener {
                        if (position.isEmpty()) {
                            showToast(
                                this@EmergencyContactActivity,
                                getString(R.string.please_choose_contact)
                            )
                            return@OnClickListener
                        }
                        dialog.dismiss()
                        addContact(
                            phoneContacts[position.toInt()].number,
                            phoneContacts[position.toInt()].name
                        )
                    })
                } else {
                    addContact(phoneContacts[0].number, phoneContacts[0].name)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * method used to add contact
     * @param num
     * @param name
     */
    private fun addContact(number: String?, name: String?) {
        var num = number
        when {
            num!!.isEmpty() -> {
                showToast(this, getString(R.string.num_doesnt_exist))
                return
            }
            num.length < 10 -> {
                showToast(this, getString(R.string.invalid_number))
                return
            }
            num.contains("-") -> num = num.replace("-", "")
            num.contains(" ") -> num =
                num.replace(" ", "")
        }
        addContactData(name, num)
    }

    /**
     * method used to get emergency contact from API
     */
    private val contactData: Unit
        get() {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            instance.getUserEmergencyContacts(
                Constant.GET_CONTACT, instance.token,
                this
            )
        }

    /**
     * method used to delete emergency contact
     * @param id
     */
    private fun deleteContactData(id: String?) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(true)
        instance.deleteEmergencyContact(
            Constant.DELETE_CONTACT, instance.token, id,
            this
        )
    }

    /**
     * method used to add contact data from API
     */
    private fun addContactData(name: String?, num: String?) {
        println("name = [$name], num = [$num]")
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.addUserEmergencyContact(
            Constant.ADD_CONTACT, instance.token, name, num, this
        )
    }

    /**
     * method used to set contact adapter
     * @param contactsList
     */
    private fun setContactAdapter(contactsList: ArrayList<Contacts>?) {
        if (contactsList != null) {
            val adapter = EmergencyContactAdapter(this, contactsList, this)
            recycleView.adapter = adapter
            recycleView.layoutManager = LinearLayoutManager(this)
            recycleView.visibility = View.VISIBLE
            txtDataLt.visibility = View.GONE
        } else {
            recycleView.visibility = View.GONE
            txtDataLt.visibility = View.VISIBLE
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

    /**
     * method used to get contact data
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

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.ADD_CONTACT) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                contactData
                showToast(this, common.msg!!)
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (common != null) showToast(this, common.msg!!) else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        } else if (tag == Constant.GET_CONTACT) {
            val emergencyContacts = response.body() as EmergencyContacts?
            if (emergencyContacts != null && emergencyContacts.status.contentEquals("1")) {
                if (emergencyContacts.contacts != null) setContactAdapter(emergencyContacts.contacts) else {
                    recycleView.visibility = View.GONE
                    txtDataLt!!.visibility = View.VISIBLE
                }
            } else if (emergencyContacts != null && emergencyContacts.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.DELETE_CONTACT) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                showToast(this, common.msg!!)
                contactData
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    override fun onClick(view: View) {
        contactsData
    }

    override fun onDeleteItem(pos: String?) {
        showDialog(this, this.resources.getString(R.string.are_you_sure_to_remove_contact), pos)
    }

    private fun showDialog(context: Context, msg: String?, pos: String?) {
        val b = AlertDialog.Builder(context, R.style.MaterialThemeDialog)
        b.setTitle(context.getString(R.string.message))
        b.setMessage(msg)
        b.setCancelable(false)
        b.setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
            deleteContactData(pos)
            dialog.cancel()
        }
        b.setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        val alert11 = b.create()
        alert11.show()
    }

    override fun onClickItem(pos: Int) {
        position = pos.toString() + ""
    }
}