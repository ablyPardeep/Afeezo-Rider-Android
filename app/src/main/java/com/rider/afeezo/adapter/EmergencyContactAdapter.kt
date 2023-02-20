package com.rider.afeezo.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.interfaces.onDeleteItemListener
import com.rider.afeezo.model.Contacts
import kotlinx.android.synthetic.main.item_emer_contact.view.*

class EmergencyContactAdapter(
    var activity: Activity,
    var contacts: ArrayList<Contacts>,
    var deletelistener: onDeleteItemListener
) : RecyclerView.Adapter<EmergencyContactAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_emer_contact, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val cont = contacts[position]
        holder.itemView.tvName.text = cont.name
        holder.itemView.tvNumber.text = cont.phone
        if (!cont.name.isNullOrEmpty()) {
            holder.itemView.tvInitial.text = if (cont.name!!.length > 1) {
                holder.itemView.tvInitial.visibility = View.VISIBLE
                cont.name?.substring(0, 1)
            } else {
                holder.itemView.tvInitial.visibility = View.GONE
                ""
            }
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {
            deletelistener.onDeleteItem(contacts[adapterPosition].id)
        }

        init {
            itemView.ivDelete.setOnClickListener(this)
        }
    }
}