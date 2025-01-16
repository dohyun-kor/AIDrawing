package com.example.gametset

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UI components and listeners
        setupGame()
        setupRecyclerView()

        // Clear the canvas when the button is clicked
        binding.button.setOnClickListener {
            clearCanvas()
        }
    }

    private fun setupGame() {
        // Initialize Game Settings
        val gameManager = GameManager()

        // Start Game Logic
        lifecycleScope.launch {
            gameManager.startGame()
        }
    }

    private fun setupRecyclerView() {
        // Sample word list
        val words = listOf("apple", "banana", "cherry")
        val adapter = WordGuessAdapter(words) { word ->
            // Handle word click
            Toast.makeText(this, "Clicked: $word", Toast.LENGTH_SHORT).show()
        }

        binding.wordRecyclerView.adapter = adapter
        binding.wordRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun clearCanvas() {
        // Call the clearCanvas method from DrawingView
        binding.drawingView.clearCanvas()
    }
}



class GameManager {

    private var currentScore: Int = 0
    private var timeLeft: Int = 60 // Game timer in seconds

    suspend fun startGame() {
        // Main Game Loop
        while (timeLeft > 0) {
            // Update Game State
            delay(1000)
            timeLeft--
            updateTimerUI(timeLeft)
        }
        endGame()
    }

    private fun updateTimerUI(time: Int) {
        // Code to update the UI with the remaining time

    }

    private fun endGame() {
        // Code to handle game over logic
    }

    fun calculateScore(isCorrect: Boolean, timeTaken: Int): Int {
        return if (isCorrect) {
            val bonus = (10 - timeTaken).coerceAtLeast(0)
            currentScore += 10 + bonus
            currentScore
        } else {
            currentScore
        }
    }
}

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint()
    private val path = Path()

    init {
        paint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                invalidate()
            }
        }
        return true
    }

    fun clearCanvas() {
        path.reset()
        invalidate()
    }
}

class WordGuessAdapter(private val words: List<String>, private val onWordClick: (String) -> Unit) : RecyclerView.Adapter<WordGuessAdapter.WordViewHolder>() {

    inner class WordViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {
        fun bind(word: String) {
            textView.text = word
            textView.setOnClickListener {
                onWordClick(word)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        return WordViewHolder(textView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount(): Int = words.size
}
