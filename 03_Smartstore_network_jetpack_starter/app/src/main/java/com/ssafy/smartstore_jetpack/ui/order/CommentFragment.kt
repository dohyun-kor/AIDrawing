package com.ssafy.smartstore_jetpack.ui.order

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass.Companion.sharedPreferencesUtil
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.data.local.SharedPreferencesUtil
import com.ssafy.smartstore_jetpack.data.model.dto.Comment
import com.ssafy.smartstore_jetpack.databinding.FragmentCommentBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import kotlin.math.*

// 하단 주문 탭
private const val TAG = "OrderFragment_싸피"

class CommentFragment :
    BaseFragment<FragmentCommentBinding>(FragmentCommentBinding::bind, R.layout.fragment_comment) {
    private lateinit var mainActivity: MainActivity
    private lateinit var commentAdapter: CommentAdapter

    private val commentViewModel: CommentFragmentViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferencesUtil = SharedPreferencesUtil(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = sharedPreferencesUtil.getUser()
        val userId = user.id
        val username = user.name
        val pId = activityViewModel.productId.value!!
        commentViewModel.init(pId)
        var comment = commentViewModel.commentInfo.value?.comments?: emptyList()
        binding.commentUserName.text = username

        // 리스너 객체 생성
        val commentActionListener = object : CommentAdapter.OnCommentActionListener {
            override fun onEditComment(comment: Comment, newText: String) {
                // 댓글 수정 처리 로직
                commentViewModel.updateComment(
                    id = comment.id,
                    userId = comment.userId,
                    pId = comment.productId,
                    rating = comment.rating,
                    comment = newText,
                    userName = comment.userName
                )
                showToast("Edit Success")
            }

            override fun onDeleteComment(comment: Comment) {
                commentViewModel.deleteComment(comment.id, pId)
                showToast("Delete Success")
            }
        }

        // 어댑터 초기화 시 this를 전달하여 리스너 연결
        commentAdapter = CommentAdapter(
            currentUserId = userId,
            commentList = comment,
            onCommentActionListener = commentActionListener // 리스너 연결
        )
        binding.commentrecyclerview.adapter = commentAdapter

        // 댓글 추가 버튼 클릭 리스너
        binding.addComment.setOnClickListener {
            val comment = binding.editcomment.text.toString() // 입력한 댓글 내용

            if (comment.isNotBlank()) {
                commentViewModel.insertComment(
                    id = 0,
                    userId = userId,
                    pId = pId,
                    rating = 0F,
                    comment = comment,
                    userName = username
                )
                // 입력란 초기화
                binding.editcomment.text.clear()
                showToast("Comment Success")
            } else {
                showToast("Please write in")
            }
        }

        // 댓글 데이터 옵저빙 및 RecyclerView 갱신
        commentViewModel.commentInfo.observe(viewLifecycleOwner) { comments ->
            commentAdapter.commentList = comments.comments
            commentAdapter.notifyDataSetChanged()
        }
    }
}