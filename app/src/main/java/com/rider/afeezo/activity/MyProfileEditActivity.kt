package com.rider.afeezo.activity

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.isValidEmail
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import com.rider.afeezo.model.UserProfile
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import kotlinx.android.synthetic.main.activity_my_edit_profile.*
import kotlinx.android.synthetic.main.file_dialog.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response
import java.io.File
import java.io.IOException

class MyProfileEditActivity : BaseActivity(), ResponseHandler, View.OnClickListener {
    private var name: String? = null
    private var email: String? = null
    private val REQUEST_TAKE_PHOTO = 123
    private var bottomSheetDialog: BottomSheetDialog? = null
    private val CUSTOM_REQUEST_CODE = 532
    private var photoPaths: ArrayList<Uri> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_edit_profile)
        utility = Utility(this)
        base_activity_toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.divider))
        setToolbar(base_activity_toolbar, resources.getString(R.string.my_profile))
        if (intent.hasExtra("jsonData")) {
            val profile = intent.getSerializableExtra("jsonData") as UserProfile
            setUserData(profile)
        } else {
            userProfileInfo
        }
        tvChangeProfileImage.setOnClickListener(this)
        submitBtn.setOnClickListener(this)
    }


    private val userProfileInfo: Unit
        get() {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(false)
            instance.userProfileInfo(Constant.USER_PROFILE_INFO, instance.token, this)
        }


    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.USER_PROFILE_INFO) {
            val profile = response.body() as UserProfile?
            if (profile != null && profile.status.contentEquals("1")) {
                setUserData(profile)
            } else if (profile != null && profile.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.UPDATE_PROFILE_PIC) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        Utility.showImage(this, userImage, common.image)
                        instance.getStore(this).saveBoolean(Constant.PROFILE_DOWNLOAD, true)
                        showToast(this, common.msg!!)
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    else -> showToast(this, common.msg!!)
                }
            } else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        } else if (tag == Constant.UPDATE_PROFILE) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                showToast(this, common.msg!!)
                instance.getStore(this).saveBoolean(Constant.PROFILE_DOWNLOAD, true)
                onBackPressed()
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (common != null) showToast(this, common.msg!!) else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        } else if (tag == Constant.REMOVE_PROFILE_PIC) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                showToast(this, common.msg!!)
                userImage.setImageResource(R.drawable.user_placeholder)
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (common != null) showToast(this, common.msg!!) else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        }
    }

    /**
     * method used to set user data
     *
     * @param userData
     */
    private fun setUserData(userData: UserProfile) {
        Utility.showImage(this, userImage, userData.data?.user_image)
        etName.setText(userData.data?.name)
        etName.setSelection(etName.text.length)
        etPhone.setText(userData.data?.phone)
        etEmail.setText(userData.data?.email)
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    /**
     * method used to select image picker dialog
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun imagePickerDialog() {
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        val readStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val cameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (listPermissionsNeeded.size > 0) ActivityCompat.requestPermissions(
            this,
            listPermissionsNeeded.toTypedArray(),
            1
        ) else showFileDialog()
    }

    /**
     * method used to remove profile image from API
     */
    private fun removePic() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.removeProfileImage(Constant.REMOVE_PROFILE_PIC, instance.token, this)
    }

    /**
     * method used to update user profile info from API
     */
    private fun updateProfileData() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.updateUserProfileinfo(
            Constant.UPDATE_PROFILE,
            name!!,
            email!!,
            "",
            "",
            "",
            "UPDATE",
            instance.token,
            this
        )
    }
    //    Pattern regex = Pattern.compile("[A-Za-z]");
    /**
     * method used to validate fields
     *
     * @return
     */
    private fun validate(): Boolean {
        name = etName.text.toString()
        email = etEmail.text.toString()
        if (name!!.trim { it <= ' ' }.isEmpty()) {
            etName.error = getString(R.string.please_enter_name)
            etName.requestFocus()
            return false
        } else if (email!!.trim { it <= ' ' }.isEmpty()) {
            etEmail.error = getString(R.string.please_enter_email)
            etEmail.requestFocus()
            return false
        } else if (!isValidEmail(etEmail!!.text.toString())) {
            etEmail.error = getString(R.string.error_invalid_email)
            etEmail.requestFocus()
            return false
        }
        return true
    }

    /**
     * method used to upload profile from API
     */
    private fun uploadProfilePic(file: File?) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.uploadProfilePic(Constant.UPDATE_PROFILE_PIC, instance.token, file!!, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CUSTOM_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                val dataList: List<Uri>? =
                    data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                if (dataList != null) {
                    photoPaths = ArrayList()
                    photoPaths.addAll(dataList)
                    if (dataList[0] != null) {
                        openCropper(dataList[0])
                    }
                }
            }
        } else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(this, data)
            openCropper(imageUri)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                    if (!isNetworkConnected(this)) {
                        showErrorDialog(this)
                        return
                    }
                    utility.storeImage(bitmap, Constant.USER_IMAGE_NAME)
                    uploadProfilePic(utility.getOutputMediaFile(Constant.USER_IMAGE_NAME))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                showToast(this, error.message!!)
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            val file =
                File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "pickImageResult.jpeg")
            val uri =
                FileProvider.getUriForFile(this, this.packageName.toString() + ".provider", file)
            if (uri != null) {
                openCropper(uri)
            }
        }
    }

    private fun openCropper(uri: Uri) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.OVAL)
            .setAllowFlipping(false)
            .setAllowRotation(false)
            .setAspectRatio(1, 1)
            .start(this)
    }

    private fun uploadPhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
// Launch the photo picker and allow the user to choose only images
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            val maxCount = 1
            FilePickerBuilder.instance
                .setMaxCount(maxCount)
                .setSelectedFiles(photoPaths)
                .setActivityTitle("Please select media")
                .enableVideoPicker(true)
                .enableCameraSupport(false)
                .showGifs(true)
                .showFolderView(false)
                .enableSelectAll(false)
                .enableImagePicker(true)
                .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .pickPhoto(this, CUSTOM_REQUEST_CODE)
        }
    }

    private fun dispatchPictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(this.packageManager)
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "pickImageResult.jpeg")
        val uri = FileProvider.getUriForFile(this, this.packageName.toString() + ".provider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_TAKE_PHOTO)
    }


    private fun showFileDialog() {
        val view: View = layoutInflater.inflate(R.layout.file_dialog, null)
        view.imageTV.setOnClickListener {
            dispatchPictureIntent()
            bottomSheetDialog?.cancel()
        }
        view.browseTV.setOnClickListener {
            uploadPhoto()
            bottomSheetDialog?.cancel()
        }
        view.removeBtn.setOnClickListener {
            removePic()
            bottomSheetDialog?.cancel()
        }
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showFileDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvChangeProfileImage ->
                imagePickerDialog()
            R.id.submitBtn -> if (validate()) {
                utility.hideSoftKeyboard()
                updateProfileData()
            }
        }
    }

    // Registers a photo picker activity launcher in single-select mode.
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            openCropper(uri)
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
}