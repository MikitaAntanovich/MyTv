package com.horizont.mytv.adapter

import android.cultraview.tv.data.CtvSource
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.horizont.mytv.R
import com.horizont.mytv.interfaces.IOutputSignalListener

class AdapterOutputSignal(
    private val outputSignalList: ArrayList<CtvSource>,
    private val iOutputSignalListener: IOutputSignalListener
) :
    RecyclerView.Adapter<AdapterOutputSignal.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var output: Button = itemView.findViewById(R.id.output)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.output_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.output.text = outputSignalList[position].toString()
        holder.output.setOnClickListener {
            iOutputSignalListener.onCLickOutputSignal(position)
        }
    }

    override fun getItemCount() = outputSignalList.size
}