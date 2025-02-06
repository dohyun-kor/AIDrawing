package com.example.gametset.room.ui.login

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gametset.R
import com.example.gametset.databinding.FragmentSignupBinding
import com.example.gametset.room.MainActivity
import com.example.gametset.room.data.UserDatabase
import com.example.gametset.room.data.model.dto.UserDto
import com.example.gametset.room.data.remote.RetrofitUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding

    lateinit var mainActivity: MainActivity

    private var checkedSignup = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun validatePassword(password: String): Boolean {
        val specialChars = "!@#$%^&*()-+"
        return password.any { it.isUpperCase() } &&
                password.any { it in specialChars }
    }

    private fun validateNickname(nickname: String): Boolean {
        return nickname.length <= 8
    }

    private fun showError(editText: EditText, message: String) {
        editText.error = message
        editText.setTextColor(Color.RED)
    }

    private fun resetError(editText: EditText) {
        editText.error = null
        editText.setTextColor(Color.BLACK)
    }

    private fun validateFields(): Boolean {
        val id = binding.idEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.passwordConfirmEditText.text.toString()
        val nickname = binding.nicknameEditText.text.toString()

        var isValid = true

        // 모든 필드 초기화
        resetError(binding.idEditText)
        resetError(binding.passwordEditText)
        resetError(binding.passwordConfirmEditText)
        resetError(binding.nicknameEditText)

        // 빈 필드 체크
        if (id.isEmpty()) {
            showError(binding.idEditText, "아이디를 입력해주세요")
            isValid = false
        }

        if (password.isEmpty()) {
            showError(binding.passwordEditText, "비밀번호를 입력해주세요")
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            showError(binding.passwordConfirmEditText, "비밀번호 확인을 입력해주세요")
            isValid = false
        }

        if (nickname.isEmpty()) {
            showError(binding.nicknameEditText, "닉네임을 입력해주세요")
            isValid = false
        }

        // ID 중복 체크
//        if (id.isNotEmpty() && UserDatabase.isIdExists(id)) {
//            showError(binding.idEditText, "이미 존재하는 아이디입니다")
//            isValid = false
//        }

        // 비밀번호 유효성 검사
        if (password.isNotEmpty() && !validatePassword(password)) {
            showError(binding.passwordEditText, "대문자와 특수 기호 각각 하나이상 넣으시오")
            isValid = false
        }

        // 비밀번호 확인
        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            showError(binding.passwordConfirmEditText, "비밀번호가 다릅니다")
            isValid = false
        }

        // 닉네임 검사
        if (nickname.isNotEmpty()) {
            if (!validateNickname(nickname)) {
                showError(binding.nicknameEditText, "닉네임은 8글자를 초과할 수 없습니다")
                isValid = false
            } else if (UserDatabase.isNicknameExists(nickname)) {
                showError(binding.nicknameEditText, "중복된 닉네임입니다")
                isValid = false
            }
//            else if (UserDatabase.isNicknameExists(nickname)) {
//                showError(binding.nicknameEditText, "중복된 닉네임입니다")
//                isValid = false
//            }
        }

        return isValid
    }

    private fun signup() {
        if (validateFields()) {
            val id = binding.idEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val nickname = binding.nicknameEditText.text.toString()

            Log.d("SignupFragment", "회원가입 시도: ID=${id}, PW=${password}, 닉네임=${nickname}")

            lifecycleScope.launch {
                val user = UserDto(id, password, nickname)
                runCatching {
                    Log.d("SignupFragment", "Response: ${user}")
                    val response = RetrofitUtil.userService.insert(user)

                    response
                }.onSuccess { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        Log.d("SignupFragment", "회원가입 성공: $id")

                        mainActivity.openFragment(0)
//                        parentFragmentManager.beginTransaction()
//                            .replace(R.id.fragment_container, LoginFragment())
//                            .commit()
                    } else {
                        Toast.makeText(context, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        Log.e("SignupFragment", "회원가입 실패")
                    }
                }.onFailure { e ->
                    Log.e("SignupFragment", "Error details:", e)
                    Log.e("SignupFragment", "Error message: ${e.message}")
                    if (e is retrofit2.HttpException) {
                        Log.e("SignupFragment", "Error response code: ${e.code()}")
                        Log.e("SignupFragment", "Error response message: ${e.message()}")
                    }
                    Toast.makeText(context, "회원가입 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        // 더미데이터 로그 출력 (테스트용)
//        Log.d("SignupFragment", "현재 등록된 사용자: ${UserDatabase.getAllUsers()}")

        binding.signupButton.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    if(validateFields()) {
                        var idCheck =
                            RetrofitUtil.userService.isUsedId(binding.idEditText.text.toString())

                        var nicknamecheck =
                            RetrofitUtil.userService.isUsedNickname(binding.nicknameEditText.text.toString())
                        if (idCheck || nicknamecheck) {
                            if(idCheck){
                                showError(binding.idEditText, "아이디가 중복되었습니다")
                            }
                            if(nicknamecheck){
                                showError(binding.nicknameEditText, "닉네임이 중복되었습니다")
                            }
                            checkedSignup = false
                        }
                    }
                }.onSuccess {
                    if (checkedSignup) {
                        signup()
                    }
                }
            }

            binding.cancelButton.setOnClickListener {
                // 취소 버튼 처리
                parentFragmentManager.popBackStack()
            }
        }
    }
}