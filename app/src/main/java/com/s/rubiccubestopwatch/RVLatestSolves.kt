package com.s.rubiccubestopwatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RVLatestSolves(private val listLatestSolves: List<String>) :
    RecyclerView.Adapter<RVLatestSolves.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtLatestTime: TextView = itemView.findViewById(R.id.txtLatestTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_last_solves, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scramble = listLatestSolves[position]
        holder.txtLatestTime.text = scramble
    }


    override fun getItemCount(): Int {
        return listLatestSolves.size
    }
}

