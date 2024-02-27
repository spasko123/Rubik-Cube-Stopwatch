package com.s.rubiccubestopwatch

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

lateinit var adapter: MyAdapter
lateinit var imgHome: ImageView
lateinit var txtnumOfSolves: TextView
lateinit var txtA5: TextView
lateinit var txtA12: TextView
lateinit var txtA50: TextView
lateinit var txtA100: TextView
lateinit var txtA500: TextView
lateinit var txtA1000: TextView
lateinit var txtTotalAverage: TextView
lateinit var imgDeleteAll: ImageView
lateinit var txtNoPrevSolves: TextView
lateinit var imgNoPrevSolves: ImageView
lateinit var imgMenu: ImageView
lateinit var conLayoutNoPrevSolves: ConstraintLayout

class AllTimesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_times)

        imgMenu = findViewById(R.id.imgMenu)
        val recyclerView: RecyclerView = findViewById(R.id.rvAllTimes)
        txtA5 = findViewById(R.id.txtA5)
        txtA12 = findViewById(R.id.txtA12)
        txtA50 = findViewById(R.id.txtA50)
        txtA100 = findViewById(R.id.txtA100)
        txtA500 = findViewById(R.id.txtA500)
        txtA1000 = findViewById(R.id.txtA1000)
        txtnumOfSolves = findViewById(R.id.txtnumOfSolves)
        txtTotalAverage = findViewById(R.id.txtTotalAverage)
        imgHome = findViewById(R.id.imgHome)
        imgDeleteAll = findViewById(R.id.imgDeleteAll)
        txtNoPrevSolves = findViewById(R.id.txtNoPrevSolves)
        imgNoPrevSolves = findViewById(R.id.imgNoPrevSolves)
        conLayoutNoPrevSolves = findViewById(R.id.conLayoutNoPrevSolves)

        calculateAverage()

        imgHome.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        imgDeleteAll.setOnClickListener(){
            showQuestionDialog()
        }

        imgMenu.setOnClickListener(){
            showPopupMenu()
        }

        val timeDao = TimeDatabaseInitializer.getInstance(applicationContext).timeDao()
        val listTimes = mutableListOf<String>()
        val listScramble = mutableListOf<String>()
        val listDate = mutableListOf<String>()
        val listIds = mutableListOf<Long>()
        val listTimesFloat = mutableListOf<Float>()

        GlobalScope.launch(Dispatchers.IO) {
            val allData = timeDao.getAllData()
            for (data in allData) {
                listIds.add(data.id)
                listTimes.add(data.time)
                listScramble.add(data.scramble)
                listDate.add(data.date)
                val floatValue = String.format("%.2f", data.time.toFloat())
                listTimesFloat.add(floatValue.toFloat())
            }
            listIds.reverse()
            listTimes.reverse()
            listScramble.reverse()
            listDate.reverse()

            if(listTimes.isEmpty()){
                txtTotalAverage.text = "Total average:--"
                txtnumOfSolves.text = "Total number of solves: 0"
                return@launch
            }
            val totalAverage = listTimesFloat.average()
            val formattedAverage = String.format("%.2f", totalAverage)

            txtTotalAverage.text = "Total average: $formattedAverage"
            txtnumOfSolves.text = "Total number of solves: ${listIds.size}"

            withContext(Dispatchers.Main) {
                if(listIds.isEmpty()){
//                    Toast.makeText(this@AllTimesActivity, "List is empty", Toast.LENGTH_SHORT).show()
                    conLayoutNoPrevSolves.visibility = View.VISIBLE

                }
                conLayoutNoPrevSolves.visibility = View.GONE
                adapter = MyAdapter(listTimes, listScramble, listDate, listIds, timeDao)
                recyclerView.layoutManager = GridLayoutManager(this@AllTimesActivity, 3)
                recyclerView.adapter = adapter
            }
        }
    }
    fun calculateAverage() {
        GlobalScope.launch(Dispatchers.IO) {
            val allData = timeDao.getAllData()
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



    private fun showQuestionDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("")
            .setMessage("Are you sure you want to delete all solves?")

        builder.setPositiveButton("Yes") { dialog, which ->
            GlobalScope.launch(Dispatchers.IO) {
                val timesFromDB = timeDao.getAllData()
                val listIds = mutableListOf<Long>()
                for (times in timesFromDB) {
                    timeDao.deleteTimeById(times.id)
                }
                val intent = Intent(this@AllTimesActivity, AllTimesActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun showPopupMenu() {
        val popupMenu = PopupMenu(this, imgMenu)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.sort_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_item1 -> {
                    // Handle item 1 click
                    true
                }
                R.id.action_item2 -> {
                    // Handle item 2 click
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

}
