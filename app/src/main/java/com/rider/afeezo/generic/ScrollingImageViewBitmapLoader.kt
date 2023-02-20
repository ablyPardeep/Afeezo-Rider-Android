package com.rider.afeezo.generic

import android.content.Context
import android.graphics.Bitmap

interface ScrollingImageViewBitmapLoader {
    fun loadBitmap(context: Context, resourceId: Int): Bitmap
}
