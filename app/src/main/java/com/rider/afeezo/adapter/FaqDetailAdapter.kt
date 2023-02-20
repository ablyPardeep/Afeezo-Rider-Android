package com.rider.afeezo.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.model.FaqDetail.Records.Faqs
import kotlinx.android.synthetic.main.item_faq_detail.view.*

class FaqDetailAdapter(var activity: Activity, var faqs: ArrayList<Faqs>) :
    RecyclerView.Adapter<FaqDetailAdapter.MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_faq_detail, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val faqDetail = faqs[position]
        holder.itemView.tvTitle.text = faqDetail.title
        holder.itemView.tvAnswer.text = faqDetail.answer
        setExpandListener(holder, faqDetail, position)
        if (faqDetail.isExpand) {
            expand(holder)
        } else {
            collapse(holder)
        }
    }

    private fun setExpandListener(viewHolder: MyHolder, faqDetail: Faqs, pos: Int) {
        viewHolder.itemView.btnExpand.setOnClickListener {
            for (item in faqs) {
                item.isExpand = false
                notifyItemChanged(pos)
            }
            faqDetail.isExpand = !viewHolder.itemView.expandableLayout.isExpanded
            notifyItemChanged(pos)
        }
    }

    private fun expand(viewHolder: MyHolder) {
        val timerThread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    activity.runOnUiThread {
                        viewHolder.itemView.expandableLayout.expand()
                        viewHolder.itemView.btnExpand.setImageResource(R.drawable.ic_minus_circle)
                    }
                }
            }
        }
        timerThread.start()
    }

    private fun collapse(viewHolder: MyHolder) {
        val timerThread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    activity.runOnUiThread {
                        viewHolder.itemView.expandableLayout.collapse()
                        viewHolder.itemView.btnExpand.setImageResource(R.drawable.ic_add_circle)
                    }
                }
            }
        }
        timerThread.start()
    }

    override fun getItemCount(): Int {
        return faqs.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {}
    }
}