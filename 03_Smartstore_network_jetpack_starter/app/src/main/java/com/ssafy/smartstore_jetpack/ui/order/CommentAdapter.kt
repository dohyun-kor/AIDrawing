package com.ssafy.smartstore_jetpack.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore_jetpack.data.model.dto.Comment
import com.ssafy.smartstore_jetpack.databinding.ListItemCommentBinding


class CommentAdapter(
    var currentUserId: String
    , var commentList: List<Comment>
    , private val onCommentActionListener: OnCommentActionListener
) :RecyclerView.Adapter<CommentAdapter.CommentHolder>(){

    // 인터페이스 정의
    interface OnCommentActionListener {
        fun onEditComment(comment: Comment, newText: String)
        fun onDeleteComment(comment: Comment)
    }


    inner class CommentHolder(private val binding: ListItemCommentBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(comment: Comment){
            // 댓글 작성자 Id 와 현재 사용자 Id 를 비교
            if(comment.userId == currentUserId){
                binding.editcomment.visibility = View.VISIBLE
                binding.deletecomment.visibility = View.VISIBLE

                // 수정 버튼 클릭 리스너
                binding.editcomment.setOnClickListener {
                    binding.editcomment.visibility = View.GONE
                    binding.deletecomment.visibility = View.GONE
                    binding.commentComment.visibility = View.GONE
                    binding.commentEditcomment.visibility = View.VISIBLE
                    binding.editbtn.visibility = View.VISIBLE
                    binding.editcancel.visibility = View.VISIBLE
                    binding.commentEditcomment.setText(comment.comment)
                }

                // 수정취소 버튼 클릭 리스너
                binding.editcancel.setOnClickListener {
                    binding.editcomment.visibility = View.VISIBLE
                    binding.deletecomment.visibility = View.VISIBLE
                    binding.commentComment.visibility = View.VISIBLE
                    binding.commentEditcomment.visibility = View.GONE
                    binding.editbtn.visibility = View.GONE
                    binding.editcancel.visibility = View.GONE
                }

                // 수정저장 버튼 클릭 리스너
                binding.editbtn.setOnClickListener {
                    val updatedComment = binding.commentEditcomment.text.toString()
                    onCommentActionListener.onEditComment(comment, updatedComment)
                    resetToDefaultState() // 원래 상태로 되돌림
                }


                // 삭제 버튼 클릭 리스너
                binding.deletecomment.setOnClickListener {
                    onCommentActionListener.onDeleteComment(comment)
                    binding.editcomment.visibility = View.GONE
                    binding.deletecomment.visibility = View.GONE
                }


            }
            binding.commentUserName.text = comment.userName
            binding.commentComment.text = comment.comment

        }

        private fun resetToDefaultState() {
            // 원래 상태로 되돌리기
            binding.editcomment.visibility = View.VISIBLE
            binding.deletecomment.visibility = View.VISIBLE
            binding.commentComment.visibility = View.VISIBLE
            binding.commentEditcomment.visibility = View.GONE
            binding.editbtn.visibility = View.GONE
            binding.editcancel.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val binding = ListItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

}

