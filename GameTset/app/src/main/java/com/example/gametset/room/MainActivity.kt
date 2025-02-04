package com.example.gametset.room

import android.content.Intent
import android.graphics.Color
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gametset.R
import com.example.gametset.databinding.ActivityMainBinding
import com.example.gametset.room.base.ApplicationClass
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

private const val TAG = "MainActivity_싸피"

// MainActivity 클래스 정의
class MainActivity : AppCompatActivity() {

    // 뷰 바인딩 객체
    private lateinit var binding: ActivityMainBinding

    // 시뮬레이션된 경로 목록
    private lateinit var simulatedPaths: List<Path>

    // WebSocket 객체
    private lateinit var webSocket: WebSocket

    // 어댑터 객체
    private lateinit var wordAdapter: WordAdapter

    // 단어 목록
    private val wordList = mutableListOf<String>()

    // 단어 목록
    private var currentColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //로그인 된 상태인지 확인
        val user = ApplicationClass.sharedPreferencesUtil.getUser()

        //로그인 상태 확인. id가 있다면 로그인 된 상태
        if (user.id != ""){
            openFragment(2)
        } else {
            // 가장 첫 화면은 홈 화면의 Fragment로 지정
            hideBottomNav(true)
            hideToolBar(true)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, LoginFragment())
                .commit()
        }

        openFragment(2)

        binding.toolbar.toolBarMenuBtn.setOnClickListener {
            showPopupMenu(it)
        }

    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.tool_bar_menu_sw, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    Toast.makeText(this, "설정 선택됨", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_friend-> {
                    Toast.makeText(this, "친구 선택됨", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_ranking -> {
                    Toast.makeText(this, "랭킹 선택됨", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    fun openFragment(index: Int) {
        moveFragment(index, "", 0)
    }

    private fun moveFragment(index: Int, key: String, value: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        when (index) {

            // 로그인
            0 -> {
                transaction.replace(
                    R.id.frame_layout_main,
                    LoginFragment()
                )
                    .addToBackStack(null)
            }

            //회원가입
            1 -> {
                transaction.replace(
                    R.id.frame_layout_main,
                    SignupFragment()
                )
                    .addToBackStack(null)
            }

            //로비
            2 -> {
                transaction.replace(
                    R.id.frame_layout_main,
                    LobbyFragment()
                )
                    .addToBackStack(null)
            }
            //logout
            5 -> {
                logout()
            }
            //order로가기
//            6 -> {
//                transaction
//                    .replace(R.id.frame_layout_main, OrderFragment())
//                    .commit()
//                binding.bottomNavigation.selectedItemId = R.id.navigation_page_2
//            }
            //store상세페이지로 가기
            7 -> {
                transaction.replace(
                    R.id.frame_layout_main,
                    StoreFragment()
                )
                    .addToBackStack(null)
            }
        }
        transaction.commit()
    }

    private fun logout() {
//        //preference 지우기
//        ApplicationClass.sharedPreferencesUtil.deleteUser()
//        ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
//
//        //화면이동
//        val intent = Intent(this, LoginActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        startActivity(intent)
    }

    fun hideBottomNav(state: Boolean) {
        Log.d("why", "why")
        if (state) binding.bottomNavigation.visibility = View.GONE
        else binding.bottomNavigation.visibility = View.VISIBLE
    }

    fun hideToolBar(isOn: Boolean){
        if(!isOn){
            binding.toolbar.toolbar.visibility = View.VISIBLE
        }else{
            binding.toolbar.toolbar.visibility = View.GONE
        }
    }

    // 앱 종료 시 WebSocket 연결 종료
    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "App closed")
    }
}
