package com.example.gametset.room.ui.myroom

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gametset.R
import com.example.gametset.room.data.model.dto.PictureDto
import java.text.SimpleDateFormat
import java.util.*

class DrawingAlbumAdapter_jw : ListAdapter<PictureDto, DrawingAlbumAdapter_jw.ViewHolder>(DiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(pictureDto: PictureDto)
        fun onDeleteClick(pictureDto: PictureDto, position: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drawing_jw, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picture = getItem(position)
        Log.d("DrawingAlbumAdapter", "Binding picture at position $position: $picture")
        holder.bind(picture)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val topicText: TextView = itemView.findViewById(R.id.drawingTopic)
        private val dateText: TextView = itemView.findViewById(R.id.drawingDate)

        fun bind(picture: PictureDto) {
            Glide.with(itemView.context)
                .load(picture.imageUrl)
                .into(imageView)

            topicText.text = picture.description
            dateText.text = formatDate(System.currentTimeMillis())

            imageView.setOnClickListener {
                listener?.onItemClick(picture)
            }

            deleteButton.setOnClickListener {
                listener?.onDeleteClick(picture, adapterPosition)
            }
        }

        private fun formatDate(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            return format.format(date)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<PictureDto>() {
        override fun areItemsTheSame(oldItem: PictureDto, newItem: PictureDto) = 
            oldItem.pictureId == newItem.pictureId
        
        override fun areContentsTheSame(oldItem: PictureDto, newItem: PictureDto) = 
            oldItem == newItem
    }
} 