package com.ssafy.smartstore_jetpack.ui.order

import android.app.AlertDialog
import android.app.ProgressDialog.show
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.RatingBar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.data.local.SharedPreferencesUtil
import com.ssafy.smartstore_jetpack.data.model.dto.Comment
import com.ssafy.smartstore_jetpack.data.model.dto.ShoppingCart
import com.ssafy.smartstore_jetpack.data.model.dto.User
import com.ssafy.smartstore_jetpack.data.model.response.ProductWithCommentResponse
import com.ssafy.smartstore_jetpack.data.remote.RetrofitUtil
import com.ssafy.smartstore_jetpack.databinding.FragmentMenuDetailBinding
import com.ssafy.smartstore_jetpack.ui.MainActivity
import com.ssafy.smartstore_jetpack.ui.MainActivityViewModel
import com.ssafy.smartstore_jetpack.util.CommonUtils
import kotlinx.coroutines.launch
import java.text.DecimalFormat

//메뉴 상세 화면 . Order탭 - 특정 메뉴 선택시 열림
private const val TAG = "MenuDetailFragment_싸피"
class MenuDetailFragment :  BaseFragment<FragmentMenuDetailBinding>(FragmentMenuDetailBinding::bind, R.layout.fragment_menu_detail){
    // id 가져오기
    private lateinit var sharedPreferencesUtil: SharedPreferencesUtil

    private lateinit var mainActivity: MainActivity
    private var productsize = 500
    private var productshot = 500

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: MenuDetailFragmentViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferencesUtil = SharedPreferencesUtil(context)
        mainActivity = context as MainActivity
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.productId = activityViewModel.productId.value!!
        mainActivity.hideBottomNav(true)
        mainActivity.hideActionBar(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerObserver()
        viewModel.getProductInfo()
        initListener()
    }

    private fun registerObserver(){
        viewModel.productInfo.observe(viewLifecycleOwner) {
            Log.d(TAG, "registerObserver: $it")
            setScreen(it)
        }
    }


    // 초기 화면 설정
    private fun setScreen(response: ProductWithCommentResponse){
        Log.d(TAG, "setScreen: $response")
        Glide.with(this)
            .load("${ApplicationClass.MENU_IMGS_URL}${response.productImg}")
            .into(binding.menuImage)

        binding.txtMenuName.text = response.productName
        if(response.type == "food"){
            changeText(true)
        }

        cal()
    }


    private var count = 1

    private fun initListener(){
        binding.btnAddCount.setOnClickListener {
            count++
            binding.menucnt.text = count.toString()
            cal()
        }

        binding.btnMinusCount.setOnClickListener {
            if (count > 1) count-- else count=1
            binding.menucnt.text = count.toString()
            cal()
        }

        binding.btnAddList.setOnClickListener {
            viewModel.productInfo.value?.let { item ->
                ShoppingCart(
                    menuId = viewModel.productId,
                    menuImg = item.productImg,
                    menuName = item.productName,
                    menuCnt = count,
                    menuPrice = item.productPrice,
                    type = item.type,
                    size = productsize,
                    shot = productshot
                )
            }?.apply {
                activityViewModel.addShoppingList(this)
                showToast("The item is now in your cart.")
                mainActivity.updateBadge()
            }
            parentFragmentManager.popBackStack()
        }

        binding.size20.setOnClickListener {
            buttoncolor(20)
        }
        binding.size25.setOnClickListener {
            buttoncolor(25)
        }
        binding.size35.setOnClickListener {
            buttoncolor(35)
        }
        binding.singleshot.setOnClickListener {
            buttoncolor(1)
        }
        binding.doubleshot.setOnClickListener {
            buttoncolor(2)
        }
        binding.gocomment.setOnClickListener {
            mainActivity.openFragment(6)
        }
        binding.backbtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }




    }
    private fun cal(){
        var price = viewModel.productInfo.value?.let {
            (it.productPrice + productsize + productshot)*count
        }

        binding.totalprice.text = "₩ " + price.toString()
    }

    private fun changeText(state : Boolean){
        if(state){
            binding.TallText.text = "Small"
            binding.GrandeText.text = "Medium"
            binding.VentiText.text = "Large"
            binding.shot.text = "set"
            binding.textDouble.text = "Combo"
            binding.image500.setImageResource(R.drawable.smallmeal)
            val layoutParams = binding.image500.layoutParams
            layoutParams.width = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40f,
                binding.root.context.resources.displayMetrics
            ).toInt()
            layoutParams.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40f,
                binding.root.context.resources.displayMetrics
            ).toInt()
            binding.image500.layoutParams = layoutParams

            binding.image1000.setImageResource(R.drawable.mediummeal)

            binding.image1500.setImageResource(R.drawable.largemeal)
            val layoutParams2 = binding.image1500.layoutParams
            layoutParams2.width = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                60f,
                binding.root.context.resources.displayMetrics
            ).toInt()
            layoutParams2.height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                60f,
                binding.root.context.resources.displayMetrics
            ).toInt()
            binding.image1500.layoutParams = layoutParams2

        }
    }

    private fun buttoncolor(size: Int) {
        val context = binding.root.context // Context 가져오기
        when (size) {
            1 -> {
                binding.singleshot.backgroundTintList = ContextCompat.getColorStateList(context, R.color.sub_theme)
                binding.doubleshot.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                productshot = 500
            }
            2 -> {
                binding.singleshot.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                binding.doubleshot.backgroundTintList = ContextCompat.getColorStateList(context, R.color.sub_theme)
                productshot = 1000
            }
            20 -> {
                binding.size20.backgroundTintList = ContextCompat.getColorStateList(context, R.color.sub_theme)
                binding.size25.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                binding.size35.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                productsize = 500
            }
            25 -> {
                binding.size20.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                binding.size25.backgroundTintList = ContextCompat.getColorStateList(context, R.color.sub_theme)
                binding.size35.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                productsize = 1000
            }
            35 -> {
                binding.size20.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                binding.size25.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
                binding.size35.backgroundTintList = ContextCompat.getColorStateList(context, R.color.sub_theme)
                productsize = 1500
            }
        }
        cal()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
        mainActivity.hideActionBar(false)
    }
}