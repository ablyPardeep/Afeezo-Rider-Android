package com.rider.afeezo.adapter

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.interfaces.OnMenuItemClickCallBack
import com.rider.afeezo.model.MenuItemCustom
import kotlinx.android.synthetic.main.menu_item_layout.view.*
import java.text.DecimalFormat

class MenuAdapter(
    private val finishParams: ArrayList<MenuItemCustom>,
    private val activity: Context,
    private val listener: OnMenuItemClickCallBack
) : RecyclerView.Adapter<MenuAdapter.MyHolder>() {

    private var value = DecimalFormat("###.##")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.menu_item_layout, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return finishParams.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = finishParams[position]
        holder.itemView.tvItemName.text = item.name
        holder.itemView.setOnClickListener {
            listener.onClickItem(item.id!!)
        }
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}

