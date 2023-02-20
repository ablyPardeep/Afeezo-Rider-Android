package com.rider.afeezo.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.interfaces.onClickListItemListener
import com.rider.afeezo.model.Contacts
import kotlinx.android.synthetic.main.item_select_contact.view.*

class ContactAdapter(
    var activity: Activity,
    var contacts: ArrayList<Contacts>,
    var itemListener: onClickListItemListener
) : RecyclerView.Adapter<ContactAdapter.MyHolder>() {
    var selectedIndex = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_select_contact, parent,
            false
        )
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val cont = contacts[position]
        holder.itemView.tvName.text = cont.name
        holder.itemView.tvNumber.text = cont.number
        holder.itemView.radioBtn.isChecked = position == selectedIndex
        holder.itemView.radioBtn.setOnClickListener(
            onStateChangedListener(
                holder.itemView.radioBtn,
                position
            )
        )
    }

    private fun onStateChangedListener(checkBox: RadioButton, position: Int): View.OnClickListener {
        return View.OnClickListener {
            selectedIndex = if (checkBox.isChecked) {
                position
            } else {
                -1
            }
            itemListener.onClickItem(selectedIndex)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {
//            deletelistener.onDeleteItem(contacts.get(getAdapterPosition()).getId());
        }
        init {
            itemView.setOnClickListener(this)
        }
    }
}