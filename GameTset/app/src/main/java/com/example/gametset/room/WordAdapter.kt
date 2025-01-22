package com.example.gametset.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R

// Adapter 설정
class WordAdapter(private val wordList: MutableList<String>) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(R.id.wordTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.wordTextView.text = wordList[position]
    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    // RecyclerView 갱신을 위한 함수
    fun addWord(word: String, recyclerView: RecyclerView) {
        wordList.add(word)
        notifyItemInserted(wordList.size - 1)
        recyclerView.scrollToPosition(wordList.size - 1)
    }

}
