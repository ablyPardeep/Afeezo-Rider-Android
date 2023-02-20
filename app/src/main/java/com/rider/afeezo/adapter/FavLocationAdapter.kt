package com.rider.afeezo.adapter

import android.app.Activity
import com.rider.afeezo.interfaces.onDeleteItemListener
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.rider.afeezo.R
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.model.Locations
import java.util.ArrayList

class FavLocationAdapter(
    var activity: Activity,
    var locations: ArrayList<Locations>,
    var deletelistener: onDeleteItemListener?
) : RecyclerView.Adapter<FavLocationAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_fav_address, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val data = locations[position]
        holder.tvName.text = data.name
        holder.tvNumber.text = data.google_loc_name
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tvName: TextView = itemView.findViewById(R.id.tvName)
        var tvNumber: TextView = itemView.findViewById(R.id.tvNumber)
        var ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        override fun onClick(view: View) {
            deletelistener!!.onDeleteItem(locations[adapterPosition].id)
        }

        init {
            ivDelete.setOnClickListener(this)
            if (deletelistener == null) {
                ivDelete.visibility = View.GONE
            }
        }
    }
}