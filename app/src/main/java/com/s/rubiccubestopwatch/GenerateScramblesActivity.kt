package com.s.rubiccubestopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GenerateScramblesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_scrambles)

        val listOfScrambles = mutableListOf<String>()

        val numberOfScrambles = intent.getIntExtra("numberOfScrambles", 0)
//        Toast.makeText(this, numberOfScrambles.toString(), Toast.LENGTH_SHORT).show()
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)


        for (i in 1..numberOfScrambles) {
            var temp: String = generateRandomScramble()
            listOfScrambles.add(temp)
            temp = ""
        }

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val adapter = ScrambleAdapter(listOfScrambles)
        recyclerView.adapter = adapter
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
}