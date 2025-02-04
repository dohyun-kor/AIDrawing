package com.example.gametset.room

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import android.widget.Toast
import com.example.gametset.databinding.FragmentLoginBinding
import com.example.gametset.room.base.ApplicationClass

class LoginFragment : Fragment() {

    lateinit var mainActivity: MainActivity
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginFragmentViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()

        binding.apply {
            //로귿인 버튼
            btnLogin.setOnClickListener{
                val id = binding.editTextLoginID.text.toString()
                val pass = binding.editTextLoginPassword.text.toString()
                viewModel.login(id, pass)
            }

            //회원 가입 버튼
            btnSignup.setOnClickListener{
                //회원가입으로 이동
                mainActivity.openFragment(1)
            }
        }
    }

    private fun registerObserver() {
        viewModel.user.observe(viewLifecycleOwner) {
            if (it.id.isEmpty()) {
                Toast.makeText(requireContext(), "id 혹은 password를 확인해 주세요.", Toast.LENGTH_SHORT).show()
            } else {
                //sharedpreference에 기록
                ApplicationClass.sharedPreferencesUtil.addUser(it)
                Toast.makeText(activity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                mainActivity.openFragment(2)
            }
        }
    }

}