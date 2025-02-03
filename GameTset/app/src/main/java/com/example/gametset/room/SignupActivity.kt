package com.example.gametset.room

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater

import androidx.appcompat.app.AppCompatActivity

import com.example.gametset.R

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