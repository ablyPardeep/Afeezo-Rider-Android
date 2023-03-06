package com.rider.afeezo.activity


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.BuildConfig
import com.rider.afeezo.R
import com.rider.afeezo.adapter.LanguageAdapter
import com.rider.afeezo.adapter.MenuAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showDialog
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.OnMenuItemClickCallBack
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.interfaces.onClickListItemListener
import com.rider.afeezo.model.Common
import com.rider.afeezo.model.LanguagePojo
import com.rider.afeezo.model.UserProfile
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_nav_drawer.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import retrofit2.Response
import java.util.ArrayList

open class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMenuItemClickCallBack, ResponseHandler, onClickListItemListener {
    var recentRideId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_nav_drawer)
        utility = Utility(this)
        navigation_view.itemIconTintList = null
        navigation_view.setNavigationItemSelectedListener(this)
        instance.token = instance.getStore(this).getString(Constant.RIDER_TOKEN)
        // Loading the menu items from utility
        val headerView = navigation_view.getHeaderView(0)

        headerView.recMenuItems.adapter = MenuAdapter(Utility.getMenuList(this), this, this)
        headerView.recMenuItems.layoutManager = LinearLayoutManager(this)
        headerView.ivNavBack.setOnClickListener {
            drawer_layout.closeDrawer(GravityCompat.START)
        }

        /**check if wallet is off then hide he wallet section from here */
        if(instance.getStore(this@MainActivity).getString(Constant.WALLET_ENABLE)=="1") {
            headerView.lnSecond.visibility = View.VISIBLE
            headerView.tvTitle.visibility = View.VISIBLE
            headerView.navTvAddmoney.visibility = View.VISIBLE
        } else{
            headerView.lnSecond.visibility = View.GONE
            headerView.tvTitle.visibility = View.GONE
            headerView.navTvAddmoney.visibility = View.GONE
        }
        /**check if reward is off then hide the reward section from here */
        if (instance.getStore(this).getString(Constant.REFERRAL_ENABLE) == "1")
              headerView.lnTop.visibility = View.VISIBLE else  headerView.lnTop.visibility = View.GONE

        drawer_layout.addDrawerListener(object :DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                userProfileInfo()
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })



    }

    override fun onResume() {
        super.onResume()
        userProfileInfo()
    }
    fun openDrawer() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START, true)
        } else {
            drawer_layout.openDrawer(GravityCompat.START, true)
        }
    }

    /**
     * method used to get user profile information
     */
    fun userProfileInfo() {
        if(instance.getStore(this@MainActivity).getBoolean(Constant.PROFILE_DOWNLOAD,true)) {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(true)
            instance.userProfileInfo(
                Constant.USER_PROFILE_INFO,
                instance.token, this
            )
        }

    }
    /**
     * method used to set header
     *
     * @param profile
     */
    private fun setHeader(profile: UserProfile) {
        val headerView = navigation_view.getHeaderView(0)
        headerView.ivEdit.setOnClickListener {
            startActivity(Intent(this@MainActivity, MyProfileEditActivity::class.java))
        }
        println("profile")
        headerView.userName.text = profile.data?.name
        headerView.phoneNo.text = profile.data?.phone
        headerView.lnTop.setOnClickListener { selectItem(R.id.nav_rewards) }
        headerView.lnSecond.setOnClickListener { selectItem(R.id.nav_wallet) }
        headerView.navTvAddmoney.setOnClickListener {
            startActivityForResult(
                Intent(this, AddWalletMoney::class.java),
                Constant.TRANSACTION_REQUEST_CODE
            )
        }
        headerView.tvLogout.setOnClickListener { showLogoutDialog() }
        headerView.tvVersion.setOnClickListener { showToast(this, BuildConfig.BASE_URL) }
        headerView.tvVersion.text = BuildConfig.VERSION_NAME
        headerView.tvGreeting.text = Utility.getTimeline()
        headerView.tvPointsVal.text =
            profile.data?.points + " " + resources.getString(R.string.points)
        headerView.tvWalletVal.text = profile.currencySymbol + " " + profile.data?.balance
        Utility.showImage(this, headerView.userImage, profile.data?.user_image)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.TRANSACTION_REQUEST_CODE) {
            userProfileInfo()
        }
    }
    private fun showDeleteAccountDialog() {
        val b = AlertDialog.Builder(this, R.style.MaterialThemeDialog)
        b.setTitle(this.resources.getString(R.string.message))
        b.setMessage(this.resources.getString(R.string.delete_account_confirmation))
        b.setCancelable(false)
        b.setPositiveButton(this.resources.getString(R.string.yes)) { dialog, _ ->
           sendDeleteRequest()
            dialog.cancel()
        }
        b.setNegativeButton(this.resources.getString(R.string.no)) { dialog, _ -> dialog.cancel() }
        val alert11 = b.create()
        alert11.show()
    }

    private fun showLogoutDialog() {
        val b = AlertDialog.Builder(this, R.style.MaterialThemeDialog)
        b.setTitle(this.resources.getString(R.string.message))
        b.setMessage(this.resources.getString(R.string.are_you_sure))
        b.setCancelable(false)
        b.setPositiveButton(this.resources.getString(R.string.yes)) { dialog, _ ->
            logoutFromServer()
            dialog.cancel()
        }
        b.setNegativeButton(this.resources.getString(R.string.no)) { dialog, _ -> dialog.cancel() }
        val alert11 = b.create()
        alert11.show()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        selectItem(item.itemId)
        return false
    }


    /**
     * Method used for opening drawer items
     * */
    fun selectItem(itemId: Int) {
        when (itemId) {
            R.id.nav_booking -> {
                startActivity(Intent(this, RideListingActivity::class.java))
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
            }
            R.id.nav_offer -> {
                startActivity(Intent(this, OffersActivity::class.java))
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
            }
            R.id.nav_wallet -> {
                startActivity(Intent(this, WalletActivity::class.java))
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
            }
            R.id.nav_fare -> {
                startActivity(Intent(this, FaresActivity::class.java))
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
            }
            R.id.nav_payments -> {
                startActivity(Intent(this, PaymentOptionActivity::class.java))
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
            }
            R.id.nav_share -> {
                startActivity(Intent(this, ShareAndEarn::class.java))
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
            }
            R.id.nav_support -> {
                startActivity(Intent(this, SupportActivity::class.java))
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
            }
            R.id.nav_changeLanguage -> {
                language_dialog()
            }
            R.id.nav_contactus -> {
                Constant.currentOpen = Constant.CONTACT_US
                startActivity(Intent(this, WebViewActivity::class.java))
            }
            R.id.nav_aboutus -> {
                Constant.currentOpen = Constant.ABOUT_US
                startActivity(Intent(this, WebViewActivity::class.java))
            }
            R.id.nav_terms -> {
                Constant.currentOpen = Constant.TERMS_COND
                startActivity(Intent(this, WebViewActivity::class.java))
            }
            R.id.nav_privacy -> {
                Constant.currentOpen = Constant.PRIVACY_POLICY
                startActivity(Intent(this, WebViewActivity::class.java))
            }
            R.id.nav_contacts -> {
                startActivity(Intent(this, EmergencyContactActivity::class.java))
            }
            R.id.nav_location -> {
            }
            R.id.nav_logout -> logoutFromServer()
            R.id.nav_delete_account -> showDeleteAccountDialog()
            R.id.nav_request_data -> startActivity(Intent(this, RequestDataActivity::class.java))
            R.id.nav_rewards -> {
                startActivity(Intent(this, RewardPointsActivity::class.java))
            }

            R.id.nav_saved_places -> {
                startActivityForResult(
                    Intent(this, MySavedPlacesActivity::class.java),
                    Constant.SAVED_PLACE_REQUEST
                )
            }
        }
        //drawer_layout.closeDrawers()
    }

    var bottomSheetLangDialog: BottomSheetDialog? = null
    fun language_dialog() {

        bottomSheetLangDialog = BottomSheetDialog(this)
        bottomSheetLangDialog?.setContentView(R.layout.popup_language)
        setlanguageAdapter()
        bottomSheetLangDialog?.show()

    }
    var recyclerview_language: RecyclerView? = null
    var languageList = ArrayList<LanguagePojo>()
    private fun setlanguageAdapter() {
        languageList = ArrayList<LanguagePojo>()
        var language = LanguagePojo()
        language.languageName = getString(R.string.english)
        language.languageCode = "en"
        languageList.add(language)
        language = LanguagePojo()
        language.languageName = getString(R.string.arabic)
        language.languageCode = "ar"
        languageList.add(language)
        recyclerview_language =
            bottomSheetLangDialog?.findViewById<View>(R.id.rvLanguages) as RecyclerView?
        recyclerview_language?.adapter = LanguageAdapter(this, languageList, this)
        recyclerview_language?.layoutManager = LinearLayoutManager(this)

    }
    private fun logoutFromServer() {
        showProgress(false)
        instance.logout(Constant.LOGOUT, instance.token, this)
    }
    private fun sendDeleteRequest() {
        showProgress(false)
        instance.sendTruncateRequest(Constant.DELETE_ACCOUNT, instance.token, this)
    }

    /**
     * method used to remove shared data key
     */
    private fun removeSharedDataKey() {
        val pref = getSharedPreferences(
            "demopref", MODE_PRIVATE
        )
        pref.edit().remove(Constant.COMMON_LOGIN).apply()
    }


    override fun onBackPressed() {
        if (drawer_layout != null && drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawers()
        else
            super.onBackPressed()
    }

    override fun onClickItem(viewId: String) {
        selectItem(viewId.toInt())
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
//        if (tag != Constant.GET_NEARBY_DRIVERS) {
        hideProgress()
//        }
        if (tag == Constant.LOGOUT) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                showToast(this, common.msg!!)
                instance.getStore(this).clear(Constant.AUTHORIZE)
                instance.getStore(this).clear(Constant.PAYMENT_METHOD)
                instance.getStore(this).clear(Constant.PAYMENT_CARD)
                instance.getStore(this).clear(Constant.PROFILE_DOWNLOAD)
                instance.getStore(this).clear(Constant.FAV_LOCATION)
                instance.getStore(this).clear(Constant.RIDER_ID)
                instance.getStore(this).clear(Constant.RIDER_TOKEN)
                instance.getStore(this).clear(Constant.COUNRTY_CODE)
                instance.token = ""
                removeSharedDataKey()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
                finish()
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this@MainActivity).sessionExpire(this@MainActivity)
            }
        } else if (tag == Constant.USER_PROFILE_INFO) {
            val profile = response.body() as UserProfile?
            if (profile != null && profile.status.contentEquals("1")) {
                val gson = Gson()
                val json = gson.toJson(profile)
                instance.getStore(this@MainActivity)
                    .saveString(Constant.PROFILE_DATA, json)
                instance.getStore(this@MainActivity).saveString(Constant.USER_PHONE, profile.data!!.phone!!)
                instance.getStore(this@MainActivity).saveString(Constant.walletAmount, profile.data!!.balance!!)
                instance.getStore(this@MainActivity)
                    .saveBoolean(Constant.PROFILE_DOWNLOAD, false)
                setHeader(profile)
                if (profile.data?.last_completed_ride != null)
                    recentRideId = profile.data?.last_completed_ride?.ride_id!!
            } else if (profile != null && profile.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this@MainActivity).sessionExpire(this@MainActivity)
            } else Utility(this@MainActivity).showSnackBar(getString(R.string.server_error))
        }
        else if (tag == Constant.DELETE_ACCOUNT) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                showDialog(this,common.msg)
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this@MainActivity).sessionExpire(this@MainActivity)
            } else if (common != null && common.status.contentEquals("-1")) {
                showDialog(this,common.msg)
            } else Utility(this@MainActivity).showSnackBar(getString(R.string.server_error))
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        utility.showSnackBar(getString(R.string.server_error))
    }

    override fun onClickItem(pos: Int) {
        instance.getStore(this).saveString(Constant.LANGUAGE_CODE, languageList[pos].languageCode)
        instance.getStore(this@MainActivity).saveBoolean(Constant.PROFILE_DOWNLOAD,true)
        instance.getStore(this@MainActivity).saveBoolean(Constant.oneTimeCall,true)
        setLangRecreate(this, languageList[pos].languageCode)
        if (bottomSheetLangDialog?.isShowing==true)
            bottomSheetLangDialog?.dismiss()
//        this.recreate()
        startActivity(Intent(this,MapFrontActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        overridePendingTransition(0, 0)
        finish()
    }

}