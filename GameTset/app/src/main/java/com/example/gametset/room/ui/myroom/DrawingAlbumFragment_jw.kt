package com.example.gametset.room.ui.myroom

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.gametset.databinding.FragmentDrawingAlbumJwBinding
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.remote.RetrofitUtil
import com.example.gametset.room.data.remote.PictureService
import com.example.gametset.room.data.model.dto.PictureDto
import kotlinx.coroutines.launch
import com.example.gametset.R
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ProgressBar
import android.util.Log
import android.app.AlertDialog
import java.io.EOFException

class DrawingAlbumFragment_jw : DialogFragment() {
    private var _binding: FragmentDrawingAlbumJwBinding? = null
    private val binding get() = _binding!!
    private val drawingAdapter = DrawingAlbumAdapter_jw()

    // 그림 선택 콜백 인터페이스
    interface OnPictureSelectedListener {
        fun onPictureSelected(pictureDto: PictureDto)
    }

    private var pictureSelectedListener: OnPictureSelectedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrawingAlbumJwBinding.inflate(inflater, container, false)
        
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }

        setupRecyclerView()
        setupClickListeners()
        loadUserDrawings()
        
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = drawingAdapter
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        drawingAdapter.setOnItemClickListener(object : DrawingAlbumAdapter_jw.OnItemClickListener {
            override fun onItemClick(pictureDto: PictureDto) {
                pictureSelectedListener?.onPictureSelected(pictureDto)
                dismiss()
            }

            override fun onDeleteClick(pictureDto: PictureDto, position: Int) {
                showDeleteConfirmDialog(pictureDto, position)
            }
        })
    }

    private fun loadUserDrawings() {
        val userId = ApplicationClass.sharedPreferencesUtil.getUser().userId
        Log.d("DrawingAlbumFragment", "Loading pictures for userId: $userId")
        
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.progressBar?.visibility = View.VISIBLE
                
                val response = RetrofitUtil.pictureService.getPicturesByUserId(userId)
                Log.d("DrawingAlbumFragment", "Response: ${response.raw()}")
                
                if (response.isSuccessful) {
                    val pictures = response.body()
                    Log.d("DrawingAlbumFragment", "Pictures received: ${pictures?.size}")
                    
                    pictures?.let { 
                        drawingAdapter.submitList(it)
                        Log.d("DrawingAlbumFragment", "Pictures submitted to adapter")
                        
                        // RecyclerView 상태 확인
                        binding.recyclerView.post {
                            Log.d("DrawingAlbumFragment", "RecyclerView items count: ${drawingAdapter.itemCount}")
                        }
                    }
                } else {
                    Log.e("DrawingAlbumFragment", "Failed to load pictures: ${response.code()}")
                    Log.e("DrawingAlbumFragment", "Error body: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "그림 목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DrawingAlbumFragment", "Error loading pictures", e)
                e.printStackTrace()
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar?.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun showDeleteConfirmDialog(pictureDto: PictureDto, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("그림 삭제")
            .setMessage("이 그림을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                deletePicture(pictureDto, position)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun deletePicture(pictureDto: PictureDto, position: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                
                val response = RetrofitUtil.pictureService.deletePicture(pictureDto.pictureId)
                
                if (response.isSuccessful) {
                    // 현재 리스트에서 아이템 제거
                    val currentList = drawingAdapter.currentList.toMutableList()
                    currentList.removeAt(position)
                    drawingAdapter.submitList(currentList)
                    
                    Toast.makeText(context, "그림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("DrawingAlbum", "Failed to delete picture: ${response.code()}")
                    Toast.makeText(context, "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("DrawingAlbum", "Failed to delete picture", e)
                // 서버가 204 No Content를 반환하는 경우에도 성공으로 처리
                if (e is EOFException) {
                    // 현재 리스트에서 아이템 제거
                    val currentList = drawingAdapter.currentList.toMutableList()
                    currentList.removeAt(position)
                    drawingAdapter.submitList(currentList)
                    
                    Toast.makeText(context, "그림이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    fun setOnPictureSelectedListener(listener: OnPictureSelectedListener) {
        pictureSelectedListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = DrawingAlbumFragment_jw()
    }
} 