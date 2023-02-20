package com.rider.afeezo.adapter

import android.app.Activity
import com.rider.afeezo.model.RideIssue.RideIssues
import com.rider.afeezo.interfaces.onClickListItemListener
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.rider.afeezo.R
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class RideIssueAdapter(
    var activity: Activity,
    var rideIssues: ArrayList<RideIssues>,
    var itemListener: onClickListItemListener
) : RecyclerView.Adapter<RideIssueAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_ride_issue,
            parent, false
        )
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val issues = rideIssues[position]
        holder.tvTitle.text = issues.name
    }

    override fun getItemCount(): Int {
        return rideIssues.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        override fun onClick(view: View) {
            itemListener.onClickItem(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}