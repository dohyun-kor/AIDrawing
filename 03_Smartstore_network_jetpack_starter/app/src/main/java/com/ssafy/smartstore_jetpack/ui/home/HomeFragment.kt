package com.ssafy.smartstore_jetpack.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.data.model.dto.Choice
import com.ssafy.smartstore_jetpack.data.model.dto.Message
import com.ssafy.smartstore_jetpack.data.model.dto.ShoppingCart
import com.ssafy.smartstore_jetpack.data.remote.RetrofitUtil
import com.ssafy.smartstore_jetpack.databinding.FragmentHomeBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import com.ssafy.smartstore_jetpack.ui.my.OrderListAdapter
import com.ssafy.smartstore_jetpack.ui.order.MenuAdapter
import com.ssafy.smartstore_jetpack.util.CommonUtils
import kotlinx.coroutines.launch
import com.ssafy.smartstore_jetpack.data.model.response.OpenAiResponse
import java.util.Objects

private const val TAG = "HomeFragment_싸피"

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private var homeAdapter: HomeListAdapter = HomeListAdapter(emptyList())
    private lateinit var mainActivity: MainActivity
    private val viewModel: HomeFragmentViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var id: String
    private var isButtonClicked = false // 버튼 클릭 여부를 확인할 플래그


    private lateinit var searchView: SearchView
    private lateinit var allButton: Button
    private lateinit var coffeeButton: Button
    private lateinit var adeButton: Button
    private lateinit var foodButton: Button


    private lateinit var menuAdapter: MenuAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        searchView = binding.searchName
        allButton = binding.all
        coffeeButton = binding.coffee
        adeButton = binding.ade
        foodButton = binding.food


        viewModel.onemonthorder.observe(viewLifecycleOwner) {
            homeAdapter.list = CommonUtils.calcTotalPrice(it)
            Log.d(TAG, "onViewCreated: ${homeAdapter.list}")
            // 기본적으로 모든 상품을 보여주기
//            menuAdapter.productList = productList
//            menuAdapter.filteredList = productList
            menuAdapter.filterByType("")  // 모든 상품 표시

            menuAdapter.notifyDataSetChanged()
            homeAdapter.notifyDataSetChanged()
        }

        setupObservers()


        menuAdapter = MenuAdapter(arrayListOf()){productId ->

            activityViewModel.setProductId(productId)
            // 3 -> //메뉴 상세 보기 로 간다
            mainActivity.openFragment(3)
        }


        // 리싸이클러 뷰 2개 짜리
        binding.recyclerViewMenu.apply {
            layoutManager = GridLayoutManager(context,2)
            adapter = menuAdapter
        }

        //FAB클릭 시 다이얼로그 띄우기
        initFAB()

        setupRecyclerView()

        setupSearchView()
        setupFilterButtons()

        // 데이터 가져오기
        viewModel.fetchProductList()

    }


    private fun setupRecyclerView() {
        menuAdapter = MenuAdapter(emptyList()) { productId ->
            // 상품 클릭 이벤트 처리
            Log.d(TAG, "Clicked product ID: $productId")
            activityViewModel.setProductId(productId)
            // 3 -> //메뉴 상세 보기 로 간다
            mainActivity.openFragment(3)
        }

        binding.recyclerViewMenu.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = menuAdapter
        }
    }

    private fun setupObservers() {
        viewModel.filteredProductList.observe(viewLifecycleOwner) { filteredList ->
            menuAdapter.updateList(filteredList)
        }
        viewModel.filterType.observe(viewLifecycleOwner) { filterType ->
            updateFilterButtons(filterType)
        }
    }

    private fun setupSearchView() {
        binding.searchName.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setFilterName(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setFilterName(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupFilterButtons() {
        binding.all.setOnClickListener {
            viewModel.setFilterType("")
        }
        binding.coffee.setOnClickListener {
            viewModel.setFilterType("coffee")
        }
        binding.ade.setOnClickListener {
            viewModel.setFilterType("ade")
        }
        binding.food.setOnClickListener {
            viewModel.setFilterType("food")
        }
    }


    private fun initFAB() {
        val fab: FloatingActionButton = binding.homefab
        fab.setOnClickListener {
            // 다이얼로그 설정
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_ai_chat)
            dialog.setCancelable(true)

            val lottieAnimationView: LottieAnimationView = dialog.findViewById(R.id.beenzino)
            val fortuneTextView: TextView = dialog.findViewById(R.id.fortuneText)

            // XML에서 기본 텍스트는 이미 설정되어 있음 (초기화에서 따로 설정 안 해도 됨)
            val tempResponse = OpenAiResponse(
                choices = listOf(
                    Choice(
                        message = Message(content = "오늘의 금전운 말해드릴게요.")
                    )
                )
            )

            viewModel.chat.value = tempResponse

            // 다이얼로그를 먼저 화면에 보여줌
            dialog.show()

            // LiveData 옵저빙: 응답을 받았을 때 UI 업데이트
            viewModel.chat.observe(viewLifecycleOwner) { openAiResponse ->
                Log.d(TAG, "Received response: $openAiResponse")

                // 기본 텍스트를 덮어쓰는 대신 조건에 따라 텍스트 업데이트
                if (openAiResponse != null && openAiResponse.choices?.isNotEmpty() == true) {
                    val fortuneText = openAiResponse.choices[0].message.content
                    fortuneTextView.text = fortuneText ?: "응답을 받을 수 없습니다."
                } else {
                    fortuneTextView.text = "금전운을 불러오는 데 실패했습니다."
                }
            }

            // Lottie 애니메이션 클릭 시 API 호출
            lottieAnimationView.setOnClickListener {
                viewModel.fetchFortune()
            }
        }
    }



    private fun updateFilterButtons(filterType: String) {
        allButton.setBackgroundColor(
            if (filterType.isEmpty()) ContextCompat.getColor(requireContext(), R.color.main_theme)
            else ContextCompat.getColor(requireContext(), R.color.sub_theme)
        )

        coffeeButton.setBackgroundColor(
            if (filterType == "coffee") ContextCompat.getColor(requireContext(), R.color.main_theme)
            else ContextCompat.getColor(requireContext(), R.color.sub_theme)
        )

        adeButton.setBackgroundColor(
            if (filterType == "ade") ContextCompat.getColor(requireContext(), R.color.main_theme)
            else ContextCompat.getColor(requireContext(), R.color.sub_theme)
        )

        foodButton.setBackgroundColor(
            if (filterType == "food") ContextCompat.getColor(requireContext(), R.color.main_theme)
            else ContextCompat.getColor(requireContext(), R.color.sub_theme)
        )
    }


//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        binding?.recyclerViewMenu?.layoutManager?.onSaveInstanceState()?.let {
//            outState.putParcelable("recyclerViewState", it)
//        }
//
//        // 버튼 필터 상태 저장
//        outState.putString("filterType", viewModel.filterType.value)
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//
//        // 리사이클러 뷰 상태 복원
//        savedInstanceState?.getParcelable<Parcelable>("recyclerViewState")?.let {
//            binding.recyclerViewMenu.layoutManager?.onRestoreInstanceState(it)
//        }
//
//        // 버튼 필터 상태 복원
//        savedInstanceState?.getString("filterType")?.let { filterType ->
//            viewModel.setFilterType(filterType) // ViewModel 상태 복원
//            updateFilterButtons(filterType) // 버튼 UI 상태 복원
//        }
//    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // ViewModel에 RecyclerView 상태 저장
        binding.recyclerViewMenu.layoutManager?.onSaveInstanceState()?.let {
            viewModel.saveRecyclerViewState(it)
        }
        // 버튼 필터 상태 저장
        outState.putString("filterType", viewModel.filterType.value)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        // RecyclerView 상태 복원
        viewModel.recyclerViewState.value?.let {
            binding.recyclerViewMenu.layoutManager?.onRestoreInstanceState(it)
        }

        // 필터 상태 복원
        viewModel.filterType.value?.let { filterType ->
            updateFilterButtons(filterType)
        }
        viewModel.filterName.value?.let { filterName ->
            binding.searchName.setQuery(filterName, false)
        }
    }



    override fun onResume() {
        super.onResume()

        // ViewModel 상태를 기반으로 UI 업데이트
        viewModel.filterType.value?.let { filterType ->
            updateFilterButtons(filterType) // 버튼 상태 복원
            menuAdapter.filterByType(filterType) // 필터링된 데이터 복원
        }
        viewModel.filterName.value?.let { filterName ->
            binding.searchName.setQuery(filterName, false) // SearchView 상태 복원
        }
    }





//    private fun initData(){
//        // 상품목록 조회.
//
//        // 뷰모델 보내는 걸 권장 -> 뷰모델 생성시 Observer 필수 생성
//        // 뷰모델 없이 바로 상품목록 호출해도 된다
//        lifecycleScope.launch {
//            // 전체 상품 조회
//            runCatching {
//                RetrofitUtil.productService.getProductList()
//
//            }.onSuccess {
//                // 어댑터에 들어있는 productList 바꾸면 됨
//                menuAdapter.productList = it
//                //diffUtil 안씀 -> 데이터 변경 알림 보내줘야한다
//                menuAdapter.notifyDataSetChanged()
//            }.onFailure {
//                // 빈 통 null 보내면 안됨.
//                menuAdapter.productList = arrayListOf()
//            }
//
//        }
//
//
//    }



}