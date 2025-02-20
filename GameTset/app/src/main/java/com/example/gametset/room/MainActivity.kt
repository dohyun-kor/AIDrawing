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
import com.bumptech.glide.Glide
import com.example.gametset.R
import com.example.gametset.databinding.ActivityMainBinding
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.UserDto
import com.example.gametset.room.ui.gameRoom.PlayGameFragment
import com.example.gametset.room.ui.lobby.LobbyFragment
import com.example.gametset.room.ui.login.LoginFragment
import com.example.gametset.room.ui.lobby.MenuPopUp
import com.example.gametset.room.ui.lobby.ProfileChangePopUpFragment
import com.example.gametset.room.ui.lobby.RoomCreateModalFragment
import com.example.gametset.room.ui.login.LoginFragmentViewModel
import com.example.gametset.room.ui.login.SignupFragment
import com.example.gametset.room.ui.myroom.EditMyroomFragment
import com.example.gametset.room.ui.store.StoreFragment
import com.example.gametset.room.websocket.GameWebSocketManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.WebSocket
import com.example.gametset.room.ui.myroom.ViewMyroomFragment
import com.example.gametset.room.ui.myroom.FriendMyroomFragment_jw
import android.media.MediaPlayer
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.OnBackPressedCallback

private const val TAG = "MainActivity_싸피"

// MainActivity 클래스 정의
class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var webSocketManager: GameWebSocketManager
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

    // LoginViewModel을 일반 클래스로 초기화
    private val loginViewModel = LoginFragmentViewModel()
    public var isOpenMenu = false

    private var mediaPlayer: MediaPlayer? = null
    private var isBackgroundMusicPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        hideStatusBar() // 앱이 다시 포커스를 얻을 때 상태 바 숨기기
        disableBackButton() // 뒤로 가기 버튼 막기

        // 배경음악 초기화 및 재생
        initBackgroundMusic()

        //로그인 된 상태인지 확인
        val user = ApplicationClass.sharedPreferencesUtil.getUser()

        //로그인 상태 확인. id가 있다면 로그인 된 상태
        if (user.id != "") {
            mainActivityViewModel.setCurrentUser(ApplicationClass.sharedPreferencesUtil.getUser())
            webSocketManager = GameWebSocketManager.getInstance()
            webSocketManager.connect()
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

                R.id.navigation_myroom -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, ViewMyroomFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }

//        openFragment(8)


            _binding.toolbar.userProfile.setOnClickListener{
            openFragment(9)
        }
        _binding.toolbar.toolBarMenuBtn.setOnClickListener {
            if (!isOpenMenu) {
                isOpenMenu = true
                openFragment(3)
            } else {
                isOpenMenu = false
                supportFragmentManager.popBackStack()
            }
        }

        // LiveData 관찰 설정
        mainActivityViewModel.userProfileData.observe(this) { user ->
            // 닉네임 업데이트
            _binding.toolbar.userNicknameTextView.text = user.nickname
            _binding.toolbar.userPoint.text = user.point.toString()

            // 프로필 이미지 업데이트
            mainActivityViewModel.getOneItem(user.userProfileItemId) { storeDto ->
                storeDto?.let { item ->
                    Glide.with(this)
                        .load(item.link)
                        .placeholder(R.drawable.user_profile)
                        .error(R.drawable.user_profile)
                        .into(_binding.toolbar.userProfile)
                }
            }
        }

        // 초기 데이터 로드
        val currentUserId = ApplicationClass.sharedPreferencesUtil.getUser().userId
        mainActivityViewModel.userInfo(currentUserId)

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
        moveFragment(index)
    }

    private fun moveFragment(index: Int) {
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
                    EditMyroomFragment()
                )
                    .addToBackStack(null)
            }

            //logout
            5 -> {
                logout()
            }

            // 친구
//            6 -> {
//                transaction.add(
//                    R.id.frame_layout_main,
//                    FriendModalFragment()
//                )
//                    .addToBackStack(null)
//            }

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

            //playgame
            8 -> {
                transaction.replace(
                    R.id.frame_layout_main,
                    PlayGameFragment()
                )
                    .addToBackStack(null)
            }
            // userProfile
            9 -> {
                transaction.add(
                    R.id.frame_layout_main,
                    ProfileChangePopUpFragment()
                )
                    .addToBackStack(null)
            }

        }
        transaction.commit()
    }

    public fun logout() {

        GameWebSocketManager.getInstance().disconnect()

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
        mediaPlayer?.release()
        mediaPlayer = null
        webSocket.close(1000, "App closed")
    }

    fun openFriendMyroom(friendId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, FriendMyroomFragment_jw.newInstance(friendId))
            .addToBackStack(null)
            .commit()
    }

    private fun initBackgroundMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.mainactivty)
        mediaPlayer?.isLooping = true
        playBackgroundMusic()
    }

    fun playBackgroundMusic() {
        if (!isBackgroundMusicPlaying) {
            mediaPlayer?.start()
            isBackgroundMusicPlaying = true
        }
    }

    fun pauseBackgroundMusic() {
        if (isBackgroundMusicPlaying) {
            mediaPlayer?.pause()
            isBackgroundMusicPlaying = false
        }
    }

    // 게임방 음악 관련 메서드
    private var gameMediaPlayer: MediaPlayer? = null
    private var isGameMusicPlaying = false

    fun startGameMusic() {
        // 메인 배경음악 중지
        pauseBackgroundMusic()
        
        // 게임 음악 시작
        if (gameMediaPlayer == null) {
            gameMediaPlayer = MediaPlayer.create(this, R.raw.game_fragment)
            gameMediaPlayer?.isLooping = true
        }
        gameMediaPlayer?.start()
        isGameMusicPlaying = true
    }

    fun stopGameMusic() {
        gameMediaPlayer?.stop()
        gameMediaPlayer?.release()
        gameMediaPlayer = null
        isGameMusicPlaying = false
        
        // 메인 배경음악 다시 시작
        playBackgroundMusic()
    }

    override fun onPause() {
        super.onPause()
        // 앱이 백그라운드로 갈 때 음악 일시정지
        pauseBackgroundMusic()
        gameMediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        hideStatusBar() // 앱이 다시 포커스를 얻을 때 상태 바 숨기기
        // 앱이 다시 포그라운드로 돌아올 때
        if (gameMediaPlayer != null && isGameMusicPlaying) {
            // 게임 음악이 재생 중이었다면 게임 음악 재생
            gameMediaPlayer?.start()
        } else {
            // 아니면 배경 음악 재생
            playBackgroundMusic()
        }
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30 이상
            window.insetsController?.let {
                it.hide(WindowInsets.Type.systemBars()) // 상태 바 & 네비게이션 바 숨기기
                it.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE // 스와이프하면 잠깐 보이게
            }
        } else {
            // API 30 이하
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    )
        }
    }

    // ✅ 뒤로 가기 버튼 막기 (API 30 이상 & 이하 모두 지원)
    private fun disableBackButton() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 뒤로 가기 버튼 비활성화 (아무 동작도 하지 않음)
            }
        })
    }

    companion object {
        val SIGNUPPAGE = 0
        val LOGINPAGE = 1
        val LOBBYPAGE = 2
        val MenuBar = 3
        val MYROOMPAGE = 4
        val LOGOUT = 5
        val STOREPAGE = 6
        val FRIENDMODAL = 7

    }
}
