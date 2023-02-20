package com.rider.afeezo.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder
import com.rider.afeezo.interfaces.onClickListItemListener
import com.rider.afeezo.model.LanguagePojo
import kotlinx.android.synthetic.main.list_item_language.view.*


class LanguageAdapter(private val ctx: Context, val languageList: ArrayList<LanguagePojo>, var listner: onClickListItemListener) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {
    private var selected: RadioButton? = null

    var listners: onClickListItemListener

    init {
        this.listners = listner
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title: TextView = itemView.title
        var radiobtn: RadioButton = itemView.radiobtn



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(ctx).inflate(R.layout.list_item_language, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        println("languageList ${languageList.size}")
        return languageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = languageList.get(position)

        holder.radiobtn.isChecked = DataHolder.instance?.getStore(ctx)?.getString(Constant.LANGUAGE_CODE).equals(language.languageCode)


        holder.title.setText(language.languageName)
        holder.itemView.setOnClickListener(View.OnClickListener {
            if (selected != null) {
                selected!!.isChecked = false
            }
            holder.radiobtn.setChecked(true)
            listner.onClickItem(position)
            selected = holder.radiobtn
        })


       /* holder.itemView.setOnClickListener(View.OnClickListener { v: View? ->
            DataHolder.instance!!.getStore(ctx).saveString(Constants.LANGUAGE_ID,languageList.get(position).language_id.toString())
            DataHolder.instance!!.getStore(ctx).saveString(Constants.LANGUAGE_CODE,languageList.get(position).language_code)

            MyApplication.setLocale(ctx, languageList.get(position).language_code)
            DataHolder.instance!!.languageId = languageList.get(position).language_id.toString()
            listner.addedOnclick(position,position, false)
        })*/

    }


}