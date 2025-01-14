package com.ssafy.smartstore_jetpack.ui.login

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.data.model.dto.User
import com.ssafy.smartstore_jetpack.data.remote.RetrofitUtil
import com.ssafy.smartstore_jetpack.databinding.FragmentJoinBinding
import com.ssafy.smartstore_jetpack.ui.LoginActivity
import kotlinx.coroutines.launch

class JoinFragment : BaseFragment<FragmentJoinBinding>(
    FragmentJoinBinding::bind,
    R.layout.fragment_join
){

    private lateinit var loginActivity: LoginActivity

    // false 면 중복확인 안한 상태
    private var checkedId = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //id 중복 확인 버튼
        binding.btnConfirm.setOnClickListener{
            lifecycleScope.launch {
                runCatching {
                    RetrofitUtil.userService.isUsedId(binding.editTextJoinID.text.toString())

                }.onSuccess {
                    if(it == false) {
                        changedEditTextSucessBoard()
                        showToast("사용 가능한 ID 입니다.")
                        changedEditTextSucessBoard()
                        binding.btnConfirm.setBackgroundResource(R.drawable.button_regular_green)
                        checkedId = true
                    }else{
                        changedEditTextFailBoard()
                        showToast("사용 불가능한 ID 입니다.")
                        binding.btnConfirm.setBackgroundResource(R.drawable.button_regular_red)
                        checkedId = false
                    }
                }.onFailure {
                    checkedId = false
                }

            }

        }

        // 회원가입 버튼
        binding.btnJoin.setOnClickListener {
            if (binding.editTextJoinPW.text.toString()
                    .isEmpty() || binding.editTextJoinPW.text.toString()
                    .isEmpty() || binding.editTextJoinName.text.toString().isEmpty()
            ) {
                showToast("빈 칸을 입력해주세요.")
            } else {
                if (checkedId == true) {

                    lifecycleScope.launch {
                        runCatching {
                            RetrofitUtil.userService.insert(
                                User(binding.editTextJoinID.text.toString()
                                    , binding.editTextJoinName.text.toString()
                                    , binding.editTextJoinPW.text.toString()
                                    , 0
                                    , 0
                                )
                            )
                        }.onSuccess {
                            //true면 회원가입 성공
                            if(it == true) {
                                showToast("회원가입 성공")
                                loginActivity.openFragment(3)
                            }
                        }.onFailure {
                            showToast("오류 발생")
                            // 빈 유저 정보 넣기
                            RetrofitUtil.userService.insert(User())
                        }
                    }

                }else{
                    binding.btnConfirm.setBackgroundResource(R.drawable.button_regular_red)
                    showToast("ID 를 체크해주세요.")
                }
            }
        }
    }

    private fun changedEditTextSucessBoard() {
        binding.editTextJoinID.setBackgroundResource(R.drawable.textview_regular)
//        binding.btnConfirm.setBackgroundResource(R.drawable.button_regular_green)

    }

    private fun changedEditTextFailBoard(){
        binding.editTextJoinID.setBackgroundResource(R.drawable.textview_regular_red)
        binding.btnJoin.setBackgroundResource(R.drawable.button_regular_red)
    }
}