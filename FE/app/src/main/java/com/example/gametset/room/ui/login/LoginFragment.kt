package com.example.gametset.room.ui.login

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.gametset.databinding.FragmentLoginBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.MainActivityViewModel
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.websocket.GameWebSocketManager
import com.example.gametset.room.data.model.dto.UserDto

private const val TAG = "LoginFragment_싸피"

class LoginFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentLoginBinding
    private val viewModel = LoginFragmentViewModel()
    private val mainActivityViewModel : MainActivityViewModel by activityViewModels()
    private lateinit var webSocketManager: GameWebSocketManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // 로그인 버튼
            btnLogin.setOnClickListener {
                btnLogin.isSelected = true
                val id = binding.editTextLoginID.text.toString()
                val pass = binding.editTextLoginPassword.text.toString()

                viewModel.login(id, pass) { result ->
                    when (result) {
                        is LoginResult.Success -> {
                            // SharedPreference에 사용자 정보 저장
                            ApplicationClass.sharedPreferencesUtil.addUser(result.user)
                            activity?.runOnUiThread {
                                val currentUserId = ApplicationClass.sharedPreferencesUtil.getUser().userId
                                mainActivityViewModel.userInfo(currentUserId)
                                Toast.makeText(activity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                                mainActivity.openFragment(2)
                                webSocketManager = GameWebSocketManager.getInstance()
                                webSocketManager.connect()
                            }
                            Log.d("Retrofit_", "Login Success: ${result.user.token}")
                        }
                        is LoginResult.Error -> {
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), "id 혹은 password를 확인해 주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            // 회원가입 버튼
            btnSignup.setOnClickListener {
                btnSignup.isSelected = true
                mainActivity.openFragment(1)
            }
        }
    }
}