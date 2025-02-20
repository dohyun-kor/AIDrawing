package com.example.gametset.room.ui.lobby

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gametset.R
import com.example.gametset.databinding.RoomAdapterItemBinding
import com.example.gametset.room.data.model.dto.OneRoomDto
import com.example.gametset.room.data.model.dto.RoomDto

class LobbyRecyclerViewAdapter : RecyclerView.Adapter<LobbyRecyclerViewAdapter.ViewHolder>() {
    private var rooms = listOf<OneRoomDto>()
    private var onItemClickListener: ((OneRoomDto) -> Unit)? = null
    
    fun setOnItemClickListener(listener: (OneRoomDto) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: RoomAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: OneRoomDto) {
            binding.room = room
            binding.executePendingBindings()

            if (room.mode == "AI") {
                binding.modeImage.setImageResource(R.drawable.aimode)
            }
            else{
                binding.modeImage.setImageResource(R.drawable.usermode)
            }

            if (room.status == "PLAY" || room.nowPlayers >= room.maxPlayers) {
                binding.roomStatus.setImageResource(R.drawable.redlight)
                binding.roomBox.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.roomDisable)
                )
//                binding.roomBox.isClickable = false
                binding.root.isEnabled = false // ❗ 비활성화 확실하게 설정
            } else {
                binding.roomStatus.setImageResource(R.drawable.greenlight)
                binding.roomBox.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.white)
                )
//                binding.roomBox.isClickable = true
                binding.root.isEnabled = true // ❗ 활성화 상태로 변경
            }

            itemView.setOnClickListener {
                onItemClickListener?.invoke(room)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RoomAdapterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    override fun getItemCount(): Int = rooms.size

    fun updateRooms(newRooms: List<OneRoomDto>) {
        rooms = newRooms
        notifyDataSetChanged()
    }
}