package com.rider.afeezo.adapter

import android.app.Activity
import android.view.ViewGroup
import android.view.LayoutInflater
import com.rider.afeezo.R
import android.widget.TextView
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.activity.FaqDetailActivity
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.model.FaqCategories
import kotlinx.android.synthetic.main.item_faqs_category.view.*
import java.util.ArrayList


class FaqAdapter(
    var activity: Activity,
    var categories: ArrayList<FaqCategories.Records.Categories>
) : RecyclerView.Adapter<FaqAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_faqs_category, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val category = categories[position]
        holder.itemView.tvTitle.text = category.name
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {
            activity.startActivity(
                Intent(activity, FaqDetailActivity::class.java)
                    .putExtra(Constant.FAQ_ID, categories[adapterPosition].id)
                    .putExtra(Constant.FAQ_CATEGORY, categories[adapterPosition].name)
            )
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}