package com.rider.afeezo.adapter

import android.app.Activity
import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.rider.afeezo.R
import com.bumptech.glide.Glide
import java.util.ArrayList

class SliderPagerAdapter(var activity: Activity, var image_arraylist: ArrayList<String>) :
    PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater =
            activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.layout_slider, container, false)
        val im_slider = view.findViewById<View>(R.id.im_slider) as ImageView
        Glide.with(activity).load(image_arraylist[position]).into(im_slider)
        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return image_arraylist.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}