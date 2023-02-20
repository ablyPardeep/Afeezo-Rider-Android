package com.rider.afeezo.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.model.RewardPointsArr.RewardPoints
import kotlinx.android.synthetic.main.item_rewards_list.view.*

class RewardsAdapter(
    var activity: Activity,
    var rewardData: ArrayList<RewardPoints>,
    var currencySymbol: String
) : RecyclerView.Adapter<RewardsAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_rewards_list, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val rewardPoints = rewardData[position]
        holder.itemView.tvPoints.text = rewardPoints.points
        if (rewardPoints.points!!.startsWith("-")) {
            holder.itemView.tvPoints.setTextColor(activity.resources.getColor(R.color.wallet_red_color))
        } else {
            holder.itemView.tvPoints.setTextColor(activity.resources.getColor(R.color.green_color))
        }
        holder.itemView.tvDesc.text = rewardPoints.comments
        holder.itemView.tvDate.text = rewardPoints.date
    }

    override fun getItemCount(): Int {
        return rewardData.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {}

        init {
            itemView.setOnClickListener(this)
        }
    }
}