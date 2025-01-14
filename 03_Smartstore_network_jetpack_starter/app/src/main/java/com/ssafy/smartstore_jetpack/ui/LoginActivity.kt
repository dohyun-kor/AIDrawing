package com.ssafy.smartstore_jetpack.ui

import android.content.Intent
import android.media.MediaPlayer
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.base.BaseActivity
import com.ssafy.smartstore_jetpack.databinding.ActivityLoginBinding
import com.ssafy.smartstore_jetpack.ui.login.JoinFragment
import com.ssafy.smartstore_jetpack.ui.login.LoginFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //로그인 된 상태인지 확인
        val user = ApplicationClass.sharedPreferencesUtil.getUser()

        //로그인 상태 확인. id가 있다면 로그인 된 상태
        if (user.id != ""){
            openFragment(1)
        } else {
            // 가장 첫 화면은 홈 화면의 Fragment로 지정
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_login, LoginFragment())
                .commit()
        }

        lifecycleScope.launch {
            delay(1000)
            playCustomSound()
        }

        getNFCData(intent)

    }

    private fun getNFCData(intent: Intent) {

        val action = intent.action

        if (action == NfcAdapter.ACTION_NDEF_DISCOVERED || action == NfcAdapter.ACTION_TECH_DISCOVERED || action == NfcAdapter.ACTION_TAG_DISCOVERED) {

            val messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

            messages?.forEach {

                val message = it as NdefMessage

                message.records.forEach {

                    val type = String(it.type)

                    when(type) {

                        "T" -> {
                            val menuIntent = Intent(this, LoginActivity::class.java)
                            startActivity(menuIntent)
                        }

                        // https://m.naver.com
                        "U" -> {
                            val uri = it.toUri()
                            val urlIntent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(urlIntent)
                        }
                    }
                }
            }
        }
    }

    fun openFragment(int: Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(int){
            1 -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
            }
            2 -> transaction.replace(R.id.frame_layout_login, JoinFragment())
                .addToBackStack(null)

            3 -> {
                // 회원가입한 뒤 돌아오면, 2번에서 addToBackStack해 놓은게 남아 있어서,
                // stack을 날려 줘야 한다. stack날리기.
                supportFragmentManager.popBackStack()
                transaction.replace(R.id.frame_layout_login, LoginFragment())
            }
        }
        transaction.commit()
    }


    private fun playCustomSound() {
        // MediaPlayer 초기화 및 파일 연결
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.login).apply {
                isLooping = true // 무한 루프 설정
                start() // MP3 재생 시작
            }
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start() // 중지된 경우 재생
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // MediaPlayer 해제
        mediaPlayer?.release()
        mediaPlayer = null
    }


    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        playCustomSound()
    }


}
