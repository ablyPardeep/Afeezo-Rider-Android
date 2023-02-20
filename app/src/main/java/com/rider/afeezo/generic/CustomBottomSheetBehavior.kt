package com.rider.afeezo.generic

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import androidx.coordinatorlayout.widget.CoordinatorLayout

import com.google.android.material.bottomsheet.BottomSheetBehavior


class CustomBottomSheetBehavior<V : View?> : BottomSheetBehavior<V> {
    private var enableCollapse = false

    constructor()
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun setEnableCollapse(enableCollapse: Boolean) {
        this.enableCollapse = enableCollapse
    }

    fun setAllowDragging(draggable: Boolean) {
        this.isDraggable = draggable
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        event: MotionEvent
    ): Boolean {
        return if (enableCollapse) {
            false
        } else super.onInterceptTouchEvent(parent, child, event)
    }
}