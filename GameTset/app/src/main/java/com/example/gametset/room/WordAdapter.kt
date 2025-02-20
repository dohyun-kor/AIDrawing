package com.example.gametset.room

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R

// Adapter 설정
class WordAdapter(private val wordList: MutableList<CharSequence>) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

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

    // ✅ 파란색 메시지 추가
    fun addBlueWord(message: String, targetWord: String, recyclerView: RecyclerView, context: Context) {
        val spannable = SpannableString(message)
        val color = ContextCompat.getColor(context, R.color.blue) // 색상 가져오기

        val startIndex = message.indexOf(targetWord)
        if (startIndex != -1) { // targetWord가 존재할 때만 적용
            val endIndex = startIndex + targetWord.length
            spannable.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        wordList.add(spannable)
        notifyItemInserted(wordList.size - 1)
        recyclerView.scrollToPosition(wordList.size - 1)
    }


    // ✅ 빨간색 메시지 추가
    fun addRedWord(message: String, targetWord: String, recyclerView: RecyclerView, context: Context) {
        val spannable = SpannableString(message)
        val color = ContextCompat.getColor(context, R.color.red) // 색상 가져오기

        val startIndex = message.indexOf(targetWord)
        if (startIndex != -1) { // targetWord가 존재할 때만 적용
            val endIndex = startIndex + targetWord.length
            spannable.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        wordList.add(spannable)
        notifyItemInserted(wordList.size - 1)
        recyclerView.scrollToPosition(wordList.size - 1)
    }

    // ✅ 초록색 메시지 추가
    fun addGreenWord(message: String, recyclerView: RecyclerView, context: Context) {
        val spannable = SpannableString(message)
        val color = ContextCompat.getColor(context, R.color.ssafy_green) // 🔹 Context 사용해서 색상 가져오기
        spannable.setSpan(ForegroundColorSpan(color), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        wordList.add(spannable)
        notifyItemInserted(wordList.size - 1)
        recyclerView.scrollToPosition(wordList.size - 1)
    }

    fun addAnswerWord(message: String, recyclerView: RecyclerView, context: Context) {
        val spannable = SpannableString(message)
        val color = ContextCompat.getColor(context, R.color.answer) // 🔹 Context 사용해서 색상 가져오기
        spannable.setSpan(ForegroundColorSpan(color), 0, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        wordList.add(spannable)
        notifyItemInserted(wordList.size - 1)
        recyclerView.scrollToPosition(wordList.size - 1)
    }
}
