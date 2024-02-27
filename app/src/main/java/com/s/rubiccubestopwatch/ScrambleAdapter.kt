package com.s.rubiccubestopwatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScrambleAdapter(private val scrambleList: List<String>) :
    RecyclerView.Adapter<ScrambleAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNumberOfScramble: TextView = itemView.findViewById(R.id.txtNumberOfScramble)
        val txtScramble: TextView = itemView.findViewById(R.id.txtScramble)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_generate, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scramble = scrambleList[position]
        holder.txtNumberOfScramble.text = "Scramble ${position + 1}:"
        holder.txtScramble.text = scramble
    }

    override fun getItemCount(): Int {
        return scrambleList.size
    }
}
