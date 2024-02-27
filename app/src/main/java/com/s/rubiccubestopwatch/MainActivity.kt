package com.s.rubiccubestopwatch

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

lateinit var lastMove: String
lateinit var randomMove: String
lateinit var txtStopwatch: TextView
lateinit var conLayout: ConstraintLayout
lateinit var txtScramble: TextView
private lateinit var stopwatch: Stopwatch
lateinit var imgNewScramble: ImageView
lateinit var imgAddScramble: ImageView
lateinit var imgShareScramble: ImageView
lateinit var imgHistory: ImageView
lateinit var linLayBottomMenu: LinearLayout
lateinit var txtAo5: TextView
lateinit var txtAo12: TextView
lateinit var txtAo50: TextView
lateinit var txtPlus2: TextView
lateinit var imgDelete: ImageView
lateinit var txtGenerate: TextView
lateinit var txtAddCustomTime: TextView
lateinit var rv: RecyclerView

var time: String = ""

var isRunning = false
lateinit var scramble: String

lateinit var timeDao: TimeDao
val listIds = mutableListOf<Long>()


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeDao = TimeDatabaseInitializer.getInstance(applicationContext).timeDao()

        rv = findViewById(R.id.rvLatestSolves)
        imgDelete = findViewById(R.id.imgDelete)
        txtPlus2 = findViewById(R.id.txtPlus2)
        txtAo5 = findViewById(R.id.txtAo5)
        txtAo12 = findViewById(R.id.txtAo12)
        txtAo50 = findViewById(R.id.txtAo50)
        linLayBottomMenu = findViewById(R.id.linLayBottomMenu)
        imgHistory = findViewById(R.id.imgHistory)
        imgNewScramble = findViewById(R.id.imgNewScramble)
        imgAddScramble = findViewById(R.id.imgAddScramble)
        imgShareScramble = findViewById(R.id.imgShareScramble)
        txtStopwatch = findViewById(R.id.txtStopwatch)
        conLayout = findViewById(R.id.conLayout)
        txtScramble = findViewById(R.id.txtScramble)
        txtGenerate = findViewById(R.id.txtGenerate)
        txtAddCustomTime = findViewById(R.id.txtAddCustomTime)
        lastMove = ""
        val colorBlue= ContextCompat.getColor(this, R.color.blue)
        val colorWhite= ContextCompat.getColor(this, R.color.white)
        scramble = generateRandomScramble()
        txtScramble.text = scramble
        stopwatch = Stopwatch(txtStopwatch)

        latestSolves()

        txtAddCustomTime.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_add_custom_time)
            val edtxCustomTime: EditText = dialog.findViewById(R.id.edtxCustomTime)
            val txtDone: TextView = dialog.findViewById(R.id.txtDone)
            val txtCancel: TextView = dialog.findViewById(R.id.txtCancel)

            txtDone.setOnClickListener {
                val customScramble = edtxCustomTime.text.toString()
                try {
                    val floatInput = customScramble.toFloat()
                    val truncatedTime = String.format("%.2f", floatInput).toFloat()
                    val stringInputTime = truncatedTime.toString()

                    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                    val currentDateAsString = dateFormat.format(Date())

                    GlobalScope.launch(Dispatchers.IO) {
                        timeDao.insertData(
                            TimeEntity(
                                time = stringInputTime,
                                scramble = scramble,
                                date = currentDateAsString
                            )
                        )

                        scramble = generateRandomScramble()
                        calculateAverage()
                        txtScramble.text = scramble
                        dialog.dismiss()
                        latestSolves()
                    }
                } catch (e: NumberFormatException) {
                    edtxCustomTime.error = "Please enter a valid number"
                }
            }

            txtCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }



        txtPlus2.setOnClickListener() {
            GlobalScope.launch(Dispatchers.IO) {
                val timesFromDB = timeDao.getAllData()
                listIds.clear()
                for (times in timesFromDB) {
                    listIds.add(times.id)
                }

                val timeF: Float = time.toFloat() + 2f
                val formattedTime = String.format("%.2f", timeF)

                timeDao.updateTimeById(listIds.last(), "0$formattedTime")
                txtStopwatch.text = "0$formattedTime"
                calculateAverage()
                latestSolves()
            }
        }


        imgDelete.setOnClickListener(){
           showQuestionDialog()
        }



        imgNewScramble.setOnClickListener(){
            scramble = generateRandomScramble()
            txtScramble.text = scramble
            stopwatch = Stopwatch(txtStopwatch)
        }

        calculateAverage()

        imgHistory.setOnClickListener(){
            val intent = Intent(this, AllTimesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        imgShareScramble.setOnClickListener(){
            val shareText = "Check out this interesting scramble for 3x3 rubic cube: $scramble"
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(shareIntent, "Share with"))
        }

        imgAddScramble.setOnClickListener(){
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_add_scramble)
            val edtxAddScramble: EditText = dialog.findViewById(R.id.edtxAddScramble)
            val txtDone: TextView = dialog.findViewById(R.id.txtDone)
            val txtCancel: TextView = dialog.findViewById(R.id.txtCancel)
            txtDone.setOnClickListener(){
                val newScramble = edtxAddScramble.text.toString()
                if(newScramble.isEmpty()){
                    edtxAddScramble.error = "Please enter a scramble!"
                    return@setOnClickListener
                }
                scramble = newScramble
                txtScramble.text = newScramble
                dialog.dismiss()
            }
            txtCancel.setOnClickListener(){
                dialog.dismiss()
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        txtGenerate.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_generate_scrambles)
            val edtxNumberOfScrambles: EditText = dialog.findViewById(R.id.edtxNumberOfScrambles)
            val txtDone: TextView = dialog.findViewById(R.id.txtDone)
            val txtCancel: TextView = dialog.findViewById(R.id.txtCancel)

            txtDone.setOnClickListener {
                val numberScramble = edtxNumberOfScrambles.text.toString()

                if (numberScramble.isEmpty()) {
                    edtxNumberOfScrambles.error = "Please enter a number of scrambles!"
                    return@setOnClickListener
                }

                val numberOfScrambles: Int
                try {
                    numberOfScrambles = numberScramble.toInt()

                    if (numberOfScrambles <= 0) {
                        edtxNumberOfScrambles.error = "Please enter a positive number greater than 0!"
                        return@setOnClickListener
                    }

                    if (numberOfScrambles > 2000) {
                        edtxNumberOfScrambles.error = "Please enter a number less than or equal to 2000!"
                        return@setOnClickListener
                    }

                } catch (e: NumberFormatException) {
                    edtxNumberOfScrambles.error = "Please enter a valid number!"
                    return@setOnClickListener
                }

                val intent = Intent(this, GenerateScramblesActivity::class.java)
                intent.putExtra("numberOfScrambles", numberOfScrambles)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                dialog.dismiss()
            }

            txtCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }



        conLayout.setOnClickListener(){
            if(!isRunning){
                stopwatch.start()
                isRunning = true
                rv.visibility = View.GONE
                txtScramble.visibility = View.GONE
                imgShareScramble.visibility = View.GONE
                imgAddScramble.visibility = View.GONE
                imgNewScramble.visibility = View.GONE
                txtAo5.visibility = View.GONE
                txtAo12.visibility = View.GONE
                txtAo50.visibility = View.GONE
                imgDelete.visibility = View.GONE
                txtPlus2.visibility = View.GONE
                txtGenerate.visibility = View.GONE
                txtAddCustomTime.visibility = View.GONE
            }
            else{
                stopwatch.stop()
                isRunning = false
                time = stopwatch.returnTime()
                val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                val currentDateAsString = dateFormat.format(Date())
                listIds.clear()
                GlobalScope.launch(Dispatchers.IO) {
                    timeDao.insertData(TimeEntity(time = time, scramble = scramble, date = currentDateAsString))
                    val timesFromDB = timeDao.getAllData()
                    for (times in timesFromDB) {
                        listIds.add(times.id)
                    }
//                    timeDao.updateTimeById(listIds.last(), "12.25")
                }
                scramble = generateRandomScramble()
                txtScramble.text = scramble
                rv.visibility = View.VISIBLE
                txtScramble.visibility = View.VISIBLE
                imgShareScramble.visibility = View.VISIBLE
                imgAddScramble.visibility = View.VISIBLE
                imgNewScramble.visibility = View.VISIBLE
                txtAo5.visibility = View.VISIBLE
                txtAo12.visibility = View.VISIBLE
                txtAo50.visibility = View.VISIBLE
                imgDelete.visibility = View.VISIBLE
                txtPlus2.visibility = View.VISIBLE
                txtGenerate.visibility = View.VISIBLE
                txtAddCustomTime.visibility = View.VISIBLE
                calculateAverage()
                latestSolves()
            }
        }
    }

    fun latestSolves() {
        lifecycleScope.launch(Dispatchers.IO) {
            val listLatestSolves = mutableListOf<String>()
            val timesFromDB = timeDao.getAllData()

            for (times in timesFromDB) {
                listLatestSolves.add(times.time)
            }
            listLatestSolves.reverse()
            val latestSolves = listLatestSolves.take(5)

            withContext(Dispatchers.Main) {
                val adapter = RVLatestSolves(latestSolves)
                rv.layoutManager = LinearLayoutManager(this@MainActivity)
                rv.adapter = adapter
            }
        }
    }


    fun generateRandomScramble(): String {
        val moves = listOf("U", "D", "L", "R", "F", "B")
        val count = listOf("", "", "2")
        val stringBuilder = StringBuilder()

        for (i in 1..20) {
            randomMove = moves.random()

            while (randomMove == lastMove) {
                randomMove = moves.random()
            }
            lastMove = randomMove
            val count2 = count.random()

            val randomRotation = if (Math.random() < 0.5) "" else "'"
            stringBuilder.append("$randomMove$count2$randomRotation ")
        }
        return stringBuilder.toString().trim()
    }

    fun calculateAverage() {
        GlobalScope.launch(Dispatchers.IO) {
            val allData = timeDao.getAllData()
            val listTimes = allData.map { it.time.toFloat() }.reversed()
            withContext(Dispatchers.Main) {
                if (listTimes.size >= 5) {
                    val ao5Average = String.format("%.2f", listTimes.take(5).average())
                    txtAo5.text = "Ao5: $ao5Average"
                } else {
                    txtAo5.text = "Ao5: ---"
                }
                if (listTimes.size >= 12) {
                    val ao12Average = String.format("%.2f", listTimes.take(12).average())
                    txtAo12.text = "Ao12: $ao12Average"
                }
                if (listTimes.size >= 50) {
                    val ao12Average = String.format("%.2f", listTimes.take(50).average())
                    txtAo50.text = "Ao12: $ao12Average"
                }
            }
        }
    }
    private fun showQuestionDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("")
            .setMessage("Are you sure you want to delete this solve?")

        builder.setPositiveButton("Yes") { dialog, which ->
            GlobalScope.launch(Dispatchers.IO) {
                val timesFromDB = timeDao.getAllData()
                listIds.clear()
                for (times in timesFromDB) {
                    listIds.add(times.id)
                }
                val timeF: Float = time.toFloat() + 2f
                val a = timeF.toString()
                timeDao.deleteTimeById(listIds.last())
                txtStopwatch.text = "Deleted!"
                imgDelete.visibility = View.INVISIBLE
                txtPlus2.visibility = View.INVISIBLE
                calculateAverage()
                latestSolves()
                dialog.dismiss()
            }
        }
        builder.setNegativeButton("No") { dialog, which ->
          dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



}

