package com.example.gametset.room

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.example.gametset.R
import com.example.gametset.room.ui.login.SignupFragment

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // SignupFragment를 띄우는 코드
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignupFragment())
                .commit()
        }
    }
}