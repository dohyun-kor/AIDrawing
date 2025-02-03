package com.example.gametset.room
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gametset.R
import android.widget.EditText
import android.graphics.Color
import android.widget.Toast
import android.util.Log
import com.example.gametset.databinding.FragmentSignupBinding
import com.example.gametset.room.data.UserDatabase
import com.example.gametset.room.model.dto.UserDto

class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding

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
        if (id.isNotEmpty() && UserDatabase.isIdExists(id)) {
            showError(binding.idEditText, "이미 존재하는 아이디입니다")
            isValid = false
        }

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
        }

        return isValid
    }

    private fun signup() {
        if (validateFields()) {
            val newUser = UserDto(
                binding.idEditText.text.toString(),
                binding.passwordEditText.text.toString(),
                binding.nicknameEditText.text.toString()
            )

            if (UserDatabase.addUser(newUser)) {
                Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()

                // LobbyFragment로 전환하고 네비게이션 바 표시
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LobbyFragment())
                    .commit()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 더미데이터 로그 출력 (테스트용)
        Log.d("SignupFragment", "현재 등록된 사용자: ${UserDatabase.getAllUsers()}")

        binding.signupButton.setOnClickListener {
            signup()
        }
    }
}