package com.ssafy.smartstore_jetpack.ui.home

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ssafy.smartstore_jetpack.data.model.dto.Choice
import com.ssafy.smartstore_jetpack.data.model.dto.Message
import com.ssafy.smartstore_jetpack.data.model.dto.Product
import com.ssafy.smartstore_jetpack.data.model.response.OpenAiResponse
import com.ssafy.smartstore_jetpack.data.model.response.OrderResponse
import com.ssafy.smartstore_jetpack.data.remote.OpenAiService
import com.ssafy.smartstore_jetpack.data.remote.RetrofitUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "HomeFragmentViewModel_싸피"
class HomeFragmentViewModel : ViewModel() {
    private val _onemonthorder = MutableLiveData<List<OrderResponse>>()
    val onemonthorder: LiveData<List<OrderResponse>>
        get() = _onemonthorder

    fun getonemonthorder(id: String) {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.orderService.getLastMonthOrder(id)
            }.onSuccess {
                _onemonthorder.value = it
            }.onFailure {
                _onemonthorder.value = listOf()
            }
        }
    }

    val sorder = MutableLiveData<OrderResponse>()
    fun selectorder(orderId: Int) {
        viewModelScope.launch {
            val result = RetrofitUtil.orderService.getOrderDetail(orderId)
            sorder.postValue(result)
        }
    }

    private val _productList = MutableLiveData<List<Product>>() // 전체 상품 리스트
    val productList: LiveData<List<Product>> get() = _productList

    private val _filterType = MutableLiveData<String>("") // 타입 필터
    val filterType: LiveData<String> get() = _filterType

    private val _filterName = MutableLiveData<String>("") // 이름 필터
    val filterName: LiveData<String> get() = _filterName

    private val _recyclerViewState = MutableLiveData<Parcelable?>()
    val recyclerViewState: LiveData<Parcelable?> get() = _recyclerViewState

    private val _filteredProductList = MediatorLiveData<List<Product>>() // 필터된 상품 리스트
    val filteredProductList: LiveData<List<Product>> get() = _filteredProductList

    init {
        // LiveData 소스 추가
        _filteredProductList.addSource(_productList) { applyFilter() }
        _filteredProductList.addSource(_filterType) { applyFilter() }
        _filteredProductList.addSource(_filterName) { applyFilter() }
    }

    // 상품 목록 가져오기
    fun fetchProductList() {
        viewModelScope.launch {
            try {
                val products = RetrofitUtil.productService.getProductList()
                _productList.postValue(products)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch product list: ${e.message}")
                _productList.postValue(emptyList())
            }
        }
    }

    // 필터 타입 설정
    fun setFilterType(type: String) {
        _filterType.value = type
    }

    // 필터 이름 설정
    fun setFilterName(name: String) {
        _filterName.value = name
    }

    fun saveRecyclerViewState(state: Parcelable?) {
        _recyclerViewState.value = state
    }

    // 필터링 로직
    private fun applyFilter() {
        val allProducts = _productList.value.orEmpty()
        val typeFilter = _filterType.value.orEmpty()
        val nameFilter = _filterName.value.orEmpty()

        _filteredProductList.value = allProducts.filter { product ->
            (typeFilter.isEmpty() || product.type.equals(typeFilter, ignoreCase = true)) &&
                    (nameFilter.isEmpty() || product.name.contains(nameFilter, ignoreCase = true))
        }
    }






    private val _chat = MutableLiveData<OpenAiResponse>(OpenAiResponse())
    val chat: MutableLiveData<OpenAiResponse> get() = _chat

    private val apiKey = "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" // 실제 API 키

    // API 호출하는 메소드
    fun fetchFortune() {


        val tempResponse = OpenAiResponse(
            choices = listOf(
                Choice(
                    message = Message(content = "오늘의 금전운 불러오는 중...")
                )
            )
        )
        _chat.value = tempResponse
        // 메시지 데이터 준비
        val messages = listOf(
            mapOf("role" to "system", "content" to "You are a helpful assistant."),
            mapOf("role" to "user", "content" to "오늘의 금전운에 대해 한 줄로 말해 주세요.")
        )

        // JSON 객체 준비
        val gson = Gson()
        val requestPayload = mapOf(
            "model" to "gpt-4o-mini",
            "messages" to messages,
            "max_tokens" to 50
        )

        // Gson을 통해 JSON 문자열로 변환
        val jsonBody = gson.toJson(requestPayload)
        val jsonMediaType = "application/json".toMediaType()
        val requestBody = RequestBody.create(jsonMediaType, jsonBody)

        // Retrofit 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(OpenAiService::class.java)

        // API 호출
        service.getFortune("Bearer $apiKey", requestBody).enqueue(object : Callback<OpenAiResponse> {
            override fun onResponse(call: Call<OpenAiResponse>, response: Response<OpenAiResponse>) {
                if (response.isSuccessful) {
                    // 성공적으로 응답 받았을 경우
                    _chat.postValue(response.body())
                    Log.d("YourViewModel", "Received response: ${response.body()}")
                } else {
                    // 응답 실패 시
                    _chat.postValue(OpenAiResponse(emptyList()))
                    Log.d("YourViewModel", "Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<OpenAiResponse>, t: Throwable) {
                // 네트워크 오류나 기타 실패 시
                _chat.postValue(OpenAiResponse(emptyList()))
                Log.d("YourViewModel", "API call failed: ${t.message}")
            }
        })
    }



}
