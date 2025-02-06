package com.example.gametset.room

import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.gametset.R
import com.example.gametset.databinding.ActivityMainBinding
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.UserDto
import com.example.gametset.room.ui.lobby.FriendModalFragment
import com.example.gametset.room.ui.lobby.LobbyFragment
import com.example.gametset.room.ui.login.LoginFragment
import com.example.gametset.room.ui.lobby.MenuPopUp
import com.example.gametset.room.ui.login.LoginFragmentViewModel
import com.example.gametset.room.ui.login.SignupFragment
import com.example.gametset.room.ui.myroom.MyroomFragment
import com.example.gametset.room.ui.store.StoreFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.WebSocket

private const val TAG = "MainActivity_싸피"

// MainActivity 클래스 정의
class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    // 뷰 바인딩 객체
    private lateinit var _binding: ActivityMainBinding
    val binding: ActivityMainBinding
        get() = _binding

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

    var CurrentUser : UserDto? = null

    private lateinit var loginViewModel: LoginFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        // ViewModel 초기화
        loginViewModel = ViewModelProvider(this).get(LoginFragmentViewModel::class.java)
        // 또는
        // val loginViewModel: LoginFragmentViewModel by viewModels()

        // Observer를 사용하여 데이터 변화를 감지하고 UI를 업데이트
        loginViewModel.user.observe(this) { user ->
            // user 데이터로 UI 업데이트
            Log.d("MainActivity", "User Info: ${user}")
        }

        //로그인 된 상태인지 확인
        val user = ApplicationClass.sharedPreferencesUtil.getUser()

        //로그인 상태 확인. id가 있다면 로그인 된 상태
        if (user.id != "") {
            openFragment(2)
        } else {
            // 가장 첫 화면은 홈 화면의 Fragment로 지정
            hideBottomNav(true)
            hideToolBar(true)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, LoginFragment())
                .commit()
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.navigation_page_2

        _binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (_binding.bottomNavigation.selectedItemId == item.itemId) {
                // 현재 선택된 아이템을 다시 클릭했을 때 이벤트를 막음
                return@setOnItemSelectedListener false
            }
            when (item.itemId) {
                R.id.navigation_page_1 -> {
                    //상점
                    openFragment(7)
                    true
                }

                R.id.navigation_page_2 -> {
                    //로비
                    openFragment(2)
                    true
                }

                R.id.navigation_page_3 -> {
                    //마이페이지
                    openFragment(4)
                    true
                }

                else -> false
            }
        }

        _binding.toolbar.toolBarMenuBtn.setOnClickListener {
            openFragment(3)
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

                R.id.action_friend -> {
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
            // 상단 메뉴바
            3 -> {
                transaction.add(
                    R.id.frame_layout_main,
                    MenuPopUp()
                )
                    .addToBackStack(null)
            }

            // 마이룸
            4 -> {
                transaction.replace(
                    R.id.frame_layout_main,
                    MyroomFragment()
                )
                    .addToBackStack(null)
            }

            //logout
            5 -> {
                logout()
            }

            // 친구
            6 -> {
                transaction.add(
                    R.id.frame_layout_main,
                    FriendModalFragment()
                )
                    .addToBackStack(null)
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

    public fun logout() {
        //preference 지우기
        ApplicationClass.sharedPreferencesUtil.deleteUser()
        ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
        openFragment(0)

        //화면이동
//        val intent = Intent(this, LoginActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent)
    }

    fun hideBottomNav(state: Boolean) {
        Log.d("why", "why")
        if (state) _binding.bottomNavigation.visibility = View.GONE
        else _binding.bottomNavigation.visibility = View.VISIBLE
    }

    fun hideToolBar(isOn: Boolean) {
        if (!isOn) {
            _binding.toolbar.toolbar.visibility = View.VISIBLE
        } else {
            _binding.toolbar.toolbar.visibility = View.GONE
        }
    }

    // 앱 종료 시 WebSocket 연결 종료
    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, "App closed")
    }

    companion object{
        val SIGNUPPAGE = 0
        val LOGINPAGE = 1
        val LOBBYPAGE = 2
        val MenuBar =3
        val MYROOMPAGE = 4
        val LOGOUT = 5
        val STOREPAGE = 6
        val FRIENDMODAL = 7

    }
}
