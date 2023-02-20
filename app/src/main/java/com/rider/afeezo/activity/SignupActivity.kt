package com.rider.afeezo.activity

import android.Manifest
import android.accounts.Account
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.isValidEmail
import com.rider.afeezo.generic.Utility.Companion.logD
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Response
import java.io.IOException
import java.util.*

class SignupActivity : BaseActivity(), ResponseHandler, View.OnClickListener,
    GoogleApiClient.OnConnectionFailedListener, OnCountryChangeListener {
    private val GOOGLE = 2
    private var verified = false
    private var countryCodeAndroid: String = ""
    private var android_id: String = ""
    private var name: String = ""
    private var email: String = ""
    private var confrimPassword: String = ""
    private var referral: String = ""
    private val mGoogleApiClient: GoogleApiClient? = null
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        android_id = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        if (intent.hasExtra(Constant.USER_PHONE)) {
            etConfirmPassword.setText(intent.getStringExtra(Constant.USER_PHONE).toString())
            etConfirmPassword.isClickable = false
            etConfirmPassword.isEnabled = false
        }
        facebookBtn.setOnClickListener(this)
        googleBtn.setOnClickListener(this)
        imageOtp.setOnClickListener(this)
        verifyOtp.setOnClickListener(this)
        startBtn.setOnClickListener(this)
        codepicker?.setOnCountryChangeListener(this)
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                when {
                    p0.toString().isEmpty() -> {
                        disableBtn()
                    }
                    etEmail.text.toString().isEmpty() -> {
                        disableBtn()
                    }
                    else -> {
                        enableBtn()
                    }
                }
            }
        })
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                when {
                    p0.toString().isEmpty() -> {
                        disableBtn()
                    }
                    etName.text.toString().isEmpty() -> {
                        disableBtn()
                    }
                    else -> {
                        enableBtn()
                    }
                }
            }
        })
    }

    private fun disableBtn() {
        startBtn.alpha = 0.4f
        startBtn.isEnabled = false
    }
    private fun enableBtn() {
        startBtn.alpha = 1.0f
        startBtn.isEnabled = true
    }


    fun verifyReferral() {
        /*  if (!Utility.Companion.isNetworkConnected(this)) {
           Utility.Companion.showErrorDialog(this);
            return;
        }*/
        instance.verifyReferral(
            Constant.VERIFY_REFERRAL,
            etReferral!!.text.toString(),
            android_id,
            instance.token,
            this
        )
    }

    private fun checkAndRequestPermissions(): Boolean {
        val receiveSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        hideProgress()
    }

    override fun onClick(view: View) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        when (view.id) {
            R.id.facebookBtn -> LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            R.id.googleBtn -> signIn()
            R.id.startBtn -> if (validate()) {
                if (etReferral.text.toString().isNotEmpty()) {
                    showProgress(false)
                    instance.verifyReferral(
                        Constant.VERIFY_REFERRAL,
                        etReferral!!.text.toString(),
                        android_id,
                        instance.token,
                        this
                    )
                } else {
                    showProgress(false)
                    instance.updateUserProfileinfo(
                        Constant.UPDATE_PROFILE,
                        name,
                        email,
                        confrimPassword,
                        referral,
                        android_id,
                        "REGISTER",
                        instance.token,
                        this
                    )
                }
            }
            R.id.verifyOtp -> {
            }
            R.id.imageOtp -> {
                finish()
            }
        }
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient!!)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient!!).setResultCallback { status ->
            if (status != null) {
                logD("onResult Status Message :  " + status.statusMessage)
                logD("onResult Status Code  :  " + status.statusCode)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            handleSignInResult(result)
        }
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }


    private fun handleSignInResult(result: GoogleSignInResult?) {
        if (result == null) {
            showErrorDialog(this)
            return
        }
        logD("handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            getProfileInformation(result.signInAccount?.account!!)
        } else {
            if (result.status != null) {
                val msg = result.status.statusMessage
                if (msg != null && msg.length > 0) {
//                    Utility.Companion.showDialog(LoginActivity.this, msg);
                }
            }
        }
    }

    private fun getProfileInformation(account: Account) {
        val googleAuthTask = googleAuthTask(this, account)
        googleAuthTask.execute(null as Void?)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        logD("onConnectionFailed : " + connectionResult.errorCode)
        Utility(this).showAlert(this, connectionResult.errorMessage)
    }

    override fun onCountrySelected() {
        countryCodeAndroid = codepicker.selectedCountryCode
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.UPDATE_PROFILE) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        showToast(this, common.msg!!)
                        instance.getStore(this).saveString(Constant.ASK_PROFILE_INFO, "0")
                        instance.getStore(this).saveBoolean(Constant.oneTimeCall, true)
                        startActivity(
                            Intent(
                                this,
                                MapFrontActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        Utility(this).sessionExpire(this)
                    }
                    else -> {
                        showToast(this, common.msg!!)
                    }
                }
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.VERIFY_REFERRAL) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        verified = true
                        referralMsg.visibility = View.VISIBLE
                        referralMsg.text = common.msg
                        referralMsg.setTextColor(resources.getColor(R.color.green_dark))
                        showProgress(false)
                        instance.updateUserProfileinfo(
                            Constant.UPDATE_PROFILE,
                            name,
                            email,
                            confrimPassword,
                            referral,
                            android_id,
                            "REGISTER",
                            instance.token,
                            this
                        )
                    }
                    common.status.contentEquals("-1") -> {
                        referralMsg.visibility = View.VISIBLE
                        referralMsg.text = common.msg
                        hideProgress()
                        referralMsg.setTextColor(Color.RED)
                        Handler().postDelayed({
                            referralMsg.visibility = View.GONE
                            etReferral.setText("")
                        }, (2 * 1000).toLong())
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        Utility(this).sessionExpire(this)
                    }
                    else -> {
                        verified = true
                        showToast(this, common.msg!!)
                    }
                }
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    override fun onStop() {
        super.onStop()
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected) mGoogleApiClient.disconnect()
        }
    }


    private fun socialLogin(type: Int, token: String) {
        if (!isNetworkConnected(this)) {
            Utility(this).showAlert(this, getString(R.string.error_internet_connection))
            return
        }
    }

    private fun validate(): Boolean {
        name = etName.text.toString()
        email = etEmail.text.toString()
        referral = etReferral.text.toString()
        if (name.isEmpty()) {
            etName.setError(getString(R.string.please_enter_name), null)
            etName.requestFocus()
            return false
        } else if (email.isEmpty()) {
            etEmail.setError(getString(R.string.please_enter_email), null)
            etEmail.requestFocus()
            return false
        } else if (!isValidEmail(etEmail.text.toString())) {
            etEmail.setError(getString(R.string.error_invalid_email), null)
            etEmail.requestFocus()
            return false
        }
        return true
    }

    inner class googleAuthTask internal constructor(
        var mActivity: Activity?,
        var account: Account
    ) : AsyncTask<Void?, Void?, String?>() {
        public override fun onPreExecute() {
        }

        override fun doInBackground(vararg params: Void?): String? {
            val SCOPE =
                "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email"
            var code: String? = null
            try {
                logD("account type : " + account.type)
                code = GoogleAuthUtil.getToken(mActivity!!, account, SCOPE)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: UserRecoverableAuthException) {
                startActivityForResult(e?.intent!!, REQ_SIGN_IN_REQUIRED)
                return "error"
            } catch (e: GoogleAuthException) {
                e.printStackTrace()
                return "error"
            }
            return code
        }

        override fun onPostExecute(token: String?) {
            if (token!!.contains("error")) {
                return
            }
            if (token != null) {
                // send this token to server.
                logD("GoogleAuthCode: $token")
                socialLogin(GOOGLE, token)
                signOut()
            } else {
                showErrorDialog(this@SignupActivity)
                logD("GoogleAuthCode: null")
            }
        }

        override fun onCancelled() {}
    }

    companion object {
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        private const val RC_SIGN_IN = 9001
        private const val REQ_SIGN_IN_REQUIRED = 55664
    }
}