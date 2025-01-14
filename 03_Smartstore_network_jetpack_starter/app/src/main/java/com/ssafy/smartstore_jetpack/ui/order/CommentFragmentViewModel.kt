package com.ssafy.smartstore_jetpack.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.smartstore_jetpack.data.model.dto.Comment
import com.ssafy.smartstore_jetpack.data.model.response.ProductWithCommentResponse
import com.ssafy.smartstore_jetpack.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "CommentFragmentViewMode"
class CommentFragmentViewModel(private val handle: SavedStateHandle): ViewModel() {
    var productId = handle.get<Int>("productId") ?: 0
        set(value){
            handle.set("productId", value)
            field = value
        }

    private val _productInfo = MutableLiveData<ProductWithCommentResponse>()
    val productInfo: LiveData<ProductWithCommentResponse>
        get() = _productInfo

    private val _commentInfo = MutableLiveData<ProductWithCommentResponse>()
    val commentInfo: LiveData<ProductWithCommentResponse>
        get() = _commentInfo


    fun getProductInfo(){
        getProductInfo(productId)
    }

    fun init(pId:Int){
        viewModelScope.launch {
            val updateCommentInfo = RetrofitUtil.productService.getProductWithComments(pId)
            _commentInfo.value = updateCommentInfo
        }
    }

    fun getProductInfo(pId:Int) {
        viewModelScope.launch{
            var info: ProductWithCommentResponse
            try{
                Log.d(TAG, "getProductInfo: success loading")
                info = RetrofitUtil.productService.getProductWithComments(pId)
            }catch (e:Exception){
                info = ProductWithCommentResponse()
            }
            _productInfo.value = info
        }
    }

    fun insertComment(id: Int, userId: String, pId: Int, rating: Float, comment: String,
                      userName:String){
        viewModelScope.launch {
            try{
                // 1. 댓글 삽입 요청 -> boolen 타입 return
                val isSuccess = RetrofitUtil.commentService.insert(
                    Comment(
                        id = id,
                        userId = userId,
                        productId = pId,
                        rating = 2F,
                        comment = comment,
                        userName = userName
                    )
                )

                // 2. 성공 시 댓글이 포함된 `ProductWithCommentResponse`를 요청
                if(isSuccess) {
                    val updateCommentInfo = RetrofitUtil.productService.getProductWithComments(pId)
                    _commentInfo.value = updateCommentInfo
                    getProductInfo(pId)

                }

            }catch(e: Exception) {
                // 오류 발생 시 기본값 설정
                _commentInfo.value = ProductWithCommentResponse()
                e.printStackTrace() // 오류 로그 출력
            }
        }
    }


    fun updateComment(id: Int, userId: String, pId: Int, rating: Float, comment: String,
                      userName:String){
        viewModelScope.launch {
            runCatching {
                val isSuccess = RetrofitUtil.commentService.update(
                    Comment(
                        id = id,
                        userId = userId,
                        productId = pId,
                        rating = 2F,
                        comment = comment,
                        userName = userName
                    )
                )
                if (isSuccess) {
                    val updateCommentInfo = RetrofitUtil.productService.getProductWithComments(pId)
                    _commentInfo.value = updateCommentInfo
                    getProductInfo(pId)
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun deleteComment(id: Int, pId: Int) {
        viewModelScope.launch {
            runCatching {
                val isSuccess = RetrofitUtil.commentService.delete(id)
                if (isSuccess) {
                    val updateCommentInfo = RetrofitUtil.productService.getProductWithComments(pId)
                    _commentInfo.value = updateCommentInfo
                    getProductInfo(productId)
                } else {
                    Log.d(TAG, "deleteComment: Delete Fail")
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

}