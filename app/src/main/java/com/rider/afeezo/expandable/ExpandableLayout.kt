package com.rider.afeezo.expandable

import android.widget.FrameLayout
import android.widget.Scroller
import com.rider.afeezo.expandable.OnExpandListener
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import com.rider.afeezo.R
import android.view.View.MeasureSpec
import com.rider.afeezo.expandable.ExpandableLayout
import kotlin.jvm.JvmOverloads
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import kotlin.math.max

class ExpandableLayout : FrameLayout {
    var collapseHight = 0
        private set
    private var collapseTargetId = 0
    private var collapsePadding = 0
    var duration = 0
    private var portraitMeasuredHeight = -1
    private var landscapeMeasuredHeight = -1
    private var scroller: Scroller? = null
    var status: Status? = Status.COLLAPSED
        private set
    private var expandListener: OnExpandListener? = null
    private var interpolator: Interpolator? = null
    private val movingRunnable: Runnable = object : Runnable {
        override fun run() {
            if (scroller!!.computeScrollOffset()) {
                layoutParams.height = scroller!!.currY
                requestLayout()
                post(this)
                return
            }
            if (scroller!!.currY == totalCollapseHeight) {
                status = Status.COLLAPSED
                notifyCollapseEvent()
            } else {
                status = Status.EXPANDED
                notifyExpandEvent()
            }
        }
    }

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr, 0)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        refreshScroller()
        if (attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ExpandableLayout,
            defStyleAttr,
            defStyleRes
        )
        collapseHight =
            typedArray.getDimensionPixelOffset(R.styleable.ExpandableLayout_exl_collapseHeight, 0)
        collapseTargetId =
            typedArray.getResourceId(R.styleable.ExpandableLayout_exl_collapseTargetId, 0)
        collapsePadding =
            typedArray.getDimensionPixelOffset(R.styleable.ExpandableLayout_exl_collapsePadding, 0)
        duration = typedArray.getInteger(R.styleable.ExpandableLayout_exl_duration, 0)
        val initialExpanded =
            typedArray.getBoolean(R.styleable.ExpandableLayout_exl_expanded, false)
        status = if (initialExpanded) Status.EXPANDED else Status.COLLAPSED
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (!isMoving) {
            expandedMeasuredHeight = getMaxChildHeight(widthMeasureSpec)
        }
        if (isExpanded) {
            setMeasuredDimension(widthMeasureSpec, expandedMeasuredHeight)
        } else if (isCollapsed) {
            setMeasuredDimension(widthMeasureSpec, totalCollapseHeight)
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun getMaxChildHeight(widthMeasureSpec: Int): Int {
        var max = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, MeasureSpec.UNSPECIFIED)
            max = max(max, child.measuredHeight)
        }
        return max
    }

    private val totalCollapseHeight: Int
        private get() {
            if (collapseHight > 0) {
                return collapseHight + collapsePadding
            }
            val view = findViewById<View>(collapseTargetId) ?: return 0
            return getRelativeTop(view) - top + collapsePadding
        }

    private fun getRelativeTop(target: View?): Int {
        if (target == null) {
            return 0
        }
        return if (target.parent == this) {
            target.top
        } else target.top + getRelativeTop(target.parent as View)
    }

    private var expandedMeasuredHeight: Int
        get() = if (isPortrait) portraitMeasuredHeight else landscapeMeasuredHeight
        private set(measuredHeight) {
            if (isPortrait) {
                portraitMeasuredHeight = measuredHeight
            } else {
                landscapeMeasuredHeight = measuredHeight
            }
        }
    private val isPortrait: Boolean
        get() = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    private val animateDuration: Int
        get() = if (duration > 0) duration else DEFAULT_DURATION

    private fun notifyExpandEvent() {
        if (expandListener != null) {
            expandListener!!.onExpanded(this)
        }
    }

    private fun notifyCollapseEvent() {
        if (expandListener != null) {
            expandListener!!.onCollapsed(this)
        }
    }

    private fun refreshScroller() {
        val interpolator = if (interpolator != null) interpolator else DEFAULT_INTERPOLATOR
        scroller = Scroller(context, interpolator)
    }

    @JvmOverloads
    fun expand(smoothScroll: Boolean = true) {
        if (isExpanded || isMoving) {
            return
        }
        status = Status.MOVING
        val duration = if (smoothScroll) animateDuration else 0
        val collapseHeight = totalCollapseHeight
        scroller!!.startScroll(
            0,
            collapseHeight,
            0,
            expandedMeasuredHeight - collapseHeight,
            duration
        )
        if (smoothScroll) {
            post(movingRunnable)
        } else {
            movingRunnable.run()
        }
    }

    @JvmOverloads
    fun toggle(smoothScroll: Boolean = true) {
        if (isExpanded) {
            collapse(smoothScroll)
        } else {
            expand(smoothScroll)
        }
    }

    @JvmOverloads
    fun collapse(smoothScroll: Boolean = true) {
        if (isCollapsed || isMoving) {
            return
        }
        status = Status.MOVING
        val duration = if (smoothScroll) animateDuration else 0
        val expandedMeasuredHeight = expandedMeasuredHeight
        scroller!!.startScroll(
            0,
            expandedMeasuredHeight,
            0,
            -(expandedMeasuredHeight - totalCollapseHeight),
            duration
        )
        if (smoothScroll) {
            post(movingRunnable)
        } else {
            movingRunnable.run()
        }
    }

    val isExpanded: Boolean
        get() = status != null && status == Status.EXPANDED
    private val isCollapsed: Boolean
        get() = status != null && status == Status.COLLAPSED
    private val isMoving: Boolean
        get() = status != null && status == Status.MOVING

    fun setCollapseHeight(collapseHeight: Int) {
        collapseHight = collapseHeight
        requestLayout()
    }

    fun getCollapseTargetId(): Int {
        return collapseTargetId
    }

    fun setCollapseTargetId(collapseTargetId: Int) {
        this.collapseTargetId = collapseTargetId
        requestLayout()
    }

    fun setOnExpandListener(expandListener: OnExpandListener?) {
        this.expandListener = expandListener
    }

    fun setInterpolator(interpolator: Interpolator?) {
        this.interpolator = interpolator
        refreshScroller()
    }

    enum class Status {
        EXPANDED, COLLAPSED, MOVING
    }

    companion object {
        private val DEFAULT_INTERPOLATOR: Interpolator = AccelerateDecelerateInterpolator()
        private const val DEFAULT_DURATION = 500
    }
}