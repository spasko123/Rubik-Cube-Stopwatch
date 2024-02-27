package com.s.rubiccubestopwatch

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAdapter(
    private var data1: MutableList<String>,
    private var data2: MutableList<String>,
    private var data3: MutableList<String>,
    private var data4: MutableList<Long>,
    private val timeDao: TimeDao
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.findViewById(R.id.txtTime)
        //        val textView2: TextView = itemView.findViewById(R.id.txtScramble)
        val conLayout: ConstraintLayout = itemView.findViewById(R.id.conLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_all_times, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView1.text = data1[position]

        holder.conLayout.setOnClickListener(){
            val dialog = Dialog(holder.itemView.context)
            dialog.setContentView(R.layout.dialog)

            val dialogTitle = dialog.findViewById<TextView>(R.id.txtTime)
            val dialogScramble = dialog.findViewById<TextView>(R.id.txtScramble)
            val dialogDate = dialog.findViewById<TextView>(R.id.txtDate)
            val dialogBtnDelete = dialog.findViewById<Button>(R.id.btnDelete)
            val dialogImgShare = dialog.findViewById<ImageView>(R.id.imgShare)

            dialogImgShare.setOnClickListener(){
                val shareText = "I got ${data1[position]} seconds on this scramble: ${data2[position]}"
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                holder.itemView.context.startActivity(Intent.createChooser(shareIntent, "Share with"))

            }

            dialogBtnDelete.setOnClickListener() {
                val idToDelete = data4[position]
                CoroutineScope(Dispatchers.IO).launch {
                    timeDao.deleteTimeById(idToDelete)

                    withContext(Dispatchers.Main) {
                        data1.removeAt(position)
                        data2.removeAt(position)
                        data3.removeAt(position)
                        data4.removeAt(position)
//                        val intent = Intent(holder.itemView.context, AllTimesActivity::class.java)
//                        holder.itemView.context.startActivity(intent)
                        notifyDataSetChanged()
                        txtnumOfSolves.text = "Total number of solves: ${adapter.itemCount}"
                        calculateAverage()
                        val listTimes = mutableListOf<String>()
                        val listTimesFloat = mutableListOf<Float>()

                        GlobalScope.launch(Dispatchers.IO) {
                            val allData = timeDao.getAllData()
                            for (data in allData) {
                                listIds.add(data.id)
                                listTimes.add(data.time)
                                val floatValue = String.format("%.2f", data.time.toFloat())
                                listTimesFloat.add(floatValue.toFloat())
                            }
                            listIds.reverse()
                            listTimes.reverse()
                            if(listTimes.isEmpty()){
                                txtTotalAverage.text = "Total average:--"
                                txtnumOfSolves.text = "Total number of solves: 0"
                                return@launch
                            }
                            val totalAverage = listTimesFloat.average()
                            val formattedAverage = String.format("%.2f", totalAverage)
                            txtTotalAverage.text = "Total average: $formattedAverage"
                        }
                    }
                    dialog.dismiss()
                }
            }

            dialogTitle.text = data1[position]
            dialogScramble.text = data2[position]
            dialogDate.text = data3[position]
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    fun calculateAverage() {
        GlobalScope.launch(Dispatchers.IO) {
            val allData = com.s.rubiccubestopwatch.timeDao.getAllData()
            val listTimes = allData.map { it.time.toFloat() }.reversed()
            withContext(Dispatchers.Main) {
                if (listTimes.size >= 5) {
                    val ao5Average = String.format("%.2f", listTimes.take(5).average())
                    txtA5.text = "Ao5: $ao5Average"
                } else {
                    txtA5.text = "Ao5:--"
                }
                if (listTimes.size >= 12) {
                    val ao12Average = String.format("%.2f", listTimes.take(12).average())
                    txtA12.text = "Ao12: $ao12Average"
                }
                if (listTimes.size >= 50) {
                    val ao12Average = String.format("%.2f", listTimes.take(50).average())
                    txtA50.text = "Ao50: $ao12Average"
                }
                if (listTimes.size >= 100) {
                    val ao12Average = String.format("%.2f", listTimes.take(100).average())
                    txtA100.text = "Ao100: $ao12Average"
                }
                if (listTimes.size >= 500) {
                    val ao12Average = String.format("%.2f", listTimes.take(500).average())
                    txtA500.text = "Ao500: $ao12Average"
                }
                if (listTimes.size >= 1000) {
                    val ao12Average = String.format("%.2f", listTimes.take(1000).average())
                    txtA1000.text = "Ao1000: $ao12Average"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data1.size
    }
}