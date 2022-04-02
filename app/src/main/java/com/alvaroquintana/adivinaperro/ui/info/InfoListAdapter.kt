package com.alvaroquintana.adivinaperro.ui.info

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alvaroquintana.domain.Dog
import com.alvaroquintana.adivinaperro.R
import com.alvaroquintana.adivinaperro.common.inflate
import com.alvaroquintana.adivinaperro.utils.glideLoadBase64
import com.alvaroquintana.adivinaperro.utils.glideLoadURL

class InfoListAdapter(
    val context: Context,
    var infoList: MutableList<Dog>) : RecyclerView.Adapter<InfoListAdapter.InfoListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoListViewHolder {
        val view = parent.inflate(R.layout.item_info, false)
        return InfoListViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoListViewHolder, position: Int) {
        val dog = infoList[position]

        glideLoadBase64(context,  dog.icon, holder.flagImage)

        val nameLocalize = dog.name
        holder.nameText.text = nameLocalize

    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    fun getItem(position: Int): Dog {
        return infoList[position]
    }

    fun update(modelList: MutableList<Dog>){
        infoList = modelList
    }

    class InfoListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameText: TextView = view.findViewById(R.id.nameText)
        var flagImage: ImageView = view.findViewById(R.id.flagImage)
    }
}