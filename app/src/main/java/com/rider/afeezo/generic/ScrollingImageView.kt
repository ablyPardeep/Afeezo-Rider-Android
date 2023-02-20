package com.rider.afeezo.generic

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import com.rider.afeezo.R
import android.util.TypedValue
import android.view.View
import java.util.*
import kotlin.math.max

/**
 * Created by thijs on 08-06-15.
 */
class ScrollingImageView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var bitmaps: List<Bitmap>? = null
    private var speed = 0f
    private lateinit var scene: IntArray
    private var arrayIndex = 0
    private var maxBitmapHeight = 0
    private val clipBoundsCustom = Rect()
    private var offset = 0f
    private var isStarted = false
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), maxBitmapHeight)
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (canvas == null || bitmaps!!.isEmpty()) {
            return
        }
        canvas.getClipBounds(clipBoundsCustom)
        while (offset <= -getBitmap(arrayIndex).width) {
            offset += getBitmap(arrayIndex).width.toFloat()
            arrayIndex = (arrayIndex + 1) % scene.size
        }
        var left = offset
        var i = 0
        while (left < clipBoundsCustom.width()) {
            val bitmap = getBitmap((arrayIndex + i) % scene.size)
            val width = bitmap.width
            canvas.drawBitmap(bitmap, getBitmapLeft(width.toFloat(), left), 0f, null)
            left += width.toFloat()
            i++
        }
        if (isStarted && speed != 0f) {
            offset -= Math.abs(speed)
            postInvalidateOnAnimation()
        }
    }

    private fun getBitmap(sceneIndex: Int): Bitmap {
        return bitmaps!![scene[sceneIndex]]
    }

    private fun getBitmapLeft(layerWidth: Float, left: Float): Float {
        return if (speed < 0) {
            clipBoundsCustom.width() - layerWidth - left
        } else {
            left
        }
    }

    /**
     * Start the animation
     */
    fun start() {
        if (!isStarted) {
            isStarted = true
            postInvalidateOnAnimation()
        }
    }

    /**
     * Stop the animation
     */
    fun stop() {
        if (isStarted) {
            isStarted = false
            invalidate()
        }
    }

    fun setSpeed(speed: Float) {
        this.speed = speed
        if (isStarted) {
            postInvalidateOnAnimation()
        }
    }

    companion object {
        var BITMAP_LOADER: ScrollingImageViewBitmapLoader =
            object : ScrollingImageViewBitmapLoader {
                override fun loadBitmap(context: Context, resourceId: Int): Bitmap {
                    return BitmapFactory.decodeResource(context.resources, resourceId)
                }
            }
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ParallaxView, 0, 0)
        var initialState = 0
        try {
            initialState = ta.getInt(R.styleable.ParallaxView_initialState, 0)
            speed = ta.getDimension(R.styleable.ParallaxView_speed, 10f)
            val sceneLength = ta.getInt(R.styleable.ParallaxView_sceneLength, 1000)
            val randomnessResourceId = ta.getResourceId(R.styleable.ParallaxView_randomness, 0)
            var randomness = IntArray(0)
            if (randomnessResourceId > 0) {
                randomness = resources.getIntArray(randomnessResourceId)
            }
            val type =
                if (isInEditMode) TypedValue.TYPE_STRING else ta.peekValue(R.styleable.ParallaxView_src).type
            if (type == TypedValue.TYPE_REFERENCE) {
                val resourceId = ta.getResourceId(R.styleable.ParallaxView_src, 0)
                val typedArray = resources.obtainTypedArray(resourceId)
                try {
                    var bitmapsSize = 0
                    for (r in randomness) {
                        bitmapsSize += r
                    }
                    bitmaps = ArrayList(max(typedArray.length(), bitmapsSize))
                    for (i in 0 until typedArray.length()) {
                        var multiplier = 1
                        if (randomness.isNotEmpty() && i < randomness.size) {
                            multiplier = max(1, randomness[i])
                        }
                        val bitmap =
                            BITMAP_LOADER.loadBitmap(getContext(), typedArray.getResourceId(i, 0))
                        for (m in 0 until multiplier) {
                            (bitmaps as ArrayList<Bitmap>).add(bitmap)
                        }
                        maxBitmapHeight = max(bitmap.height, maxBitmapHeight)
                    }
                    val random = Random()
                    scene = IntArray(sceneLength)
                    for (i in scene.indices) {
                        scene[i] = random.nextInt(bitmaps!!.size)
                    }
                } finally {
                    typedArray.recycle()
                }
            } else if (type == TypedValue.TYPE_STRING) {
                val bitmap = BITMAP_LOADER.loadBitmap(
                    getContext(),
                    ta.getResourceId(R.styleable.ParallaxView_src, 0)
                )
                if (bitmap != null) {
                    bitmaps = listOf(bitmap)
                    scene = intArrayOf(0)
                    maxBitmapHeight = bitmaps!![0].height
                } else {
                    bitmaps = emptyList()
                }
            }
        } finally {
            ta.recycle()
        }
        if (initialState == 0) {
            start()
        }
    }
}