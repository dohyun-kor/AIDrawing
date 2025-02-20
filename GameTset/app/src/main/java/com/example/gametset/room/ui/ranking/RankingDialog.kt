package com.example.gametset.room.ui.ranking

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gametset.databinding.DialogRankingBinding
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.remote.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.view.View

private const val TAG = "RankingDialog"

class RankingDialog(context: Context) : Dialog(context) {
    private lateinit var binding: DialogRankingBinding
    private val rankingAdapter = RankingAdapter()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 다이얼로그 설정
        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        setupViews()
        loadRankingData()
    }

    private fun setupViews() {
        // RecyclerView 설정
        binding.rankingRecyclerView.apply {
            adapter = rankingAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // 닫기 버튼
        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun loadRankingData() {
        coroutineScope.launch {
            try {
                // 전체 랭킹 목록 먼저 로드
                val rankingList = RetrofitUtil.topRankingService.getRankingList()
                Log.d(TAG, "Top ranking list loaded: $rankingList")
                rankingAdapter.submitList(rankingList)

                // 내 랭킹 정보는 전체 랭킹 목록에서 찾기
                val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId
                val myRanking = rankingList.find { it.userId == userId }
                
                myRanking?.let {
                    binding.apply {
                        myRankText.text = "${rankingList.indexOf(it) + 1}위"
                        myExpText.text = "${it.exp} EXP"
                        // winRate는 전체 랭킹 목록에서는 제공되지 않으므로 생략하거나 0으로 표시
                        myWinRateText.visibility = View.GONE
                    }
                } ?: run {
                    binding.apply {
                        myRankText.text = "순위 없음"
                        myExpText.text = "0 EXP"
                        myWinRateText.visibility = View.GONE
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Failed to load ranking data", e)
                when (e) {
                    is retrofit2.HttpException -> {
                        val errorBody = e.response()?.errorBody()?.string()
                        Log.e(TAG, "HTTP Error: ${e.code()}, Body: $errorBody")
                        Toast.makeText(context, "서버 오류: ${e.code()}", Toast.LENGTH_SHORT).show()
                    }
                    is java.net.SocketTimeoutException -> {
                        Toast.makeText(context, "서버 응답 시간 초과", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "랭킹 정보를 불러오는데 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun dismiss() {
        coroutineScope.launch {
            super.dismiss()
        }
    }
} 