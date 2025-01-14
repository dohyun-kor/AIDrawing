package com.ssafy.smartstore_jetpack.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.base.BaseFragment
import com.ssafy.smartstore_jetpack.databinding.FragmentLoginBinding
import com.ssafy.smartstore_jetpack.ui.LoginActivity

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind,
    R.layout.fragment_login
){
    private lateinit var loginActivity: LoginActivity
    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 타이밍 이슈 => 1. Observer 먼저 선언 한다 -> 매 변경 관리를 먼저 준비해야 함
        //              2. 라이브데이터 변경 시 나를 불러주게 선언
        registerObserver()

        // 로그인 구현
        //mvvn
        binding.btnLogin.setOnClickListener {
            // viewModel에 있는 id, pass 를 담아 보내서 viewModel fun 호출
            viewModel.login(
                binding.editTextLoginID.text.toString()
                , binding.editTextLoginPW.text.toString())

        }

        //회원가입 구현
        binding.btnJoin.setOnClickListener {
            loginActivity.openFragment(2)
        }

    }

    private fun registerObserver(){
        // user 가 정상적으로 들어온걸 observer 한다
        viewModel.user.observe(viewLifecycleOwner){
            // id 비어있음 확인
            if(it.id.isEmpty()) {// - 로그인 실패
                 showToast("Please check your ID or password.")
                // EditText 테두리를 빨간색으로 변경
                binding.editTextLoginID.setBackgroundResource(R.drawable.red_border)
                binding.editTextLoginPW.setBackgroundResource(R.drawable.red_border)

                // ImageView의 이미지를 변경
                binding.imageView.setImageResource(R.drawable.login_error) // error_image는 변경할 이미지 리소스
            }else{// - 로그인 성공
                binding.editTextLoginID.setBackgroundResource(R.drawable.textview_regular)
                binding.editTextLoginPW.setBackgroundResource(R.drawable.textview_regular)

                // ImageView 원래 이미지로 복구
                binding.imageView.setImageResource(R.drawable.login)
                //user sharedPreference 에 먼저 써주기
                // 내가 로그인 성공시 여기에 addUser 로 기록
                // 로그아웃하면 sharedPreferencesUtil 날려주기
                ApplicationClass.sharedPreferencesUtil.addUser(it)
                showToast("You have successfully logged in.")

                //로그인 성공 화면 메인액티비티로 보내주기
                // 로그인 성공 openFragment 1번 지정으로 보내주기
                loginActivity.openFragment(1)

            }
        }
    }

}