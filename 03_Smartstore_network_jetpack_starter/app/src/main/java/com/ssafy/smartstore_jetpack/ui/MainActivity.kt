package com.ssafy.smartstore_jetpack.ui

import android.Manifest
import android.app.AlertDialog
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.ssafy.smartstore_jetpack.R
import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.base.BaseActivity
import com.ssafy.smartstore_jetpack.data.model.dto.User
import com.ssafy.smartstore_jetpack.databinding.ActivityMainBinding
import com.ssafy.smartstore_jetpack.databinding.DialogStoreEventBinding
import com.ssafy.smartstore_jetpack.ui.game.GameFragment
import com.ssafy.smartstore_jetpack.ui.home.HomeFragment
import com.ssafy.smartstore_jetpack.ui.my.MyPageFragment
import com.ssafy.smartstore_jetpack.ui.my.OrderDetailFragment
import com.ssafy.smartstore_jetpack.ui.order.CommentFragment
import com.ssafy.smartstore_jetpack.ui.order.MapFragment
import com.ssafy.smartstore_jetpack.ui.order.MenuDetailFragment
import com.ssafy.smartstore_jetpack.ui.order.OrderFragment
import com.ssafy.smartstore_jetpack.ui.side.LiveFragment
import com.ssafy.smartstore_jetpack.util.PermissionChecker
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.RangeNotifier
import org.altbeacon.beacon.Region

private const val TAG = "MainActivity_싸피"
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private lateinit var user: User

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var nAdapter: NfcAdapter
    private lateinit var pIntent: PendingIntent

    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val BEACON_UUID = "e2c56db5-dffb-48d2-b060-d0f5a71096e0" // 우리반 모두 동일값
        private const val BEACON_MAJOR = "40011" // 우리반 모두 동일값
        private const val BEACON_MINOR = "43438" // 우리반 모두 동일값
        private const val BLUETOOTH_ADDRESS = "C3:00:00:1C:5E:8D"
        private const val BEACON_DISTANCE = 5.0 // 거리
    }

    private lateinit var beaconManager: BeaconManager
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    /** permission check **/
    private val checker = PermissionChecker(this)
    private val runtimePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }

    private val region = Region(
        "estimote",
        listOf(
            Identifier.parse(BEACON_UUID),
            Identifier.parse(BEACON_MAJOR),
            Identifier.parse(BEACON_MINOR)),
        BLUETOOTH_ADDRESS
    )
    private var eventPopUpAble = true
    private var isdialog = false

    private val DIALOG_INTERVAL = 86400 * 1000 // 24시간(86400초)을 밀리초로 변환

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playCustomSound()

        user = ApplicationClass.sharedPreferencesUtil.getUser()
        mainActivityViewModel.getUserInfo(user.id)

        //BeaconManager 지정
        beaconManager = BeaconManager.getInstanceForApplication(this)
        //		estimo 비컨을 분석 하도록 하기 위하여 beacon parser 오프셋, 버전등을 setLayout으로 지정한다.
//		m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24
//		설명: 0 ~ 1 바이트는 제조사를 나타내는 필드로 파싱하지 않는다.
//		2~3 바이트는 0x02, 0x15 이다.
//		4~19 바이트들을 첫번째 ID로 매핑한다.(UUID)
//				20~21 바이트들을 두번째 ID로 매핑한다.(Major)
//				22-23 바이트들을 세번째 ID로 매핑한다.(Minor)
//				24~24 바이트들을 txPower로 매핑한다.
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

//        nAdapter = NfcAdapter.getDefaultAdapter(this)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP // 내가 top에 있으면 재사용. --> onNewIntent
        }
        pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        /* permission check */
        if (!checker.checkPermission(this, runtimePermissions)) {
            checker.setOnGrantedListener{
                //퍼미션 획득 성공일때
                startScan()
            }

            checker.requestPermissionLauncher.launch(runtimePermissions)
        } else { //이미 전체 권한이 있는 경우
            startScan()
        }
        /* permission check */

        binding.home.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, HomeFragment())
                .commit()
            // 바텀 내비게이션 선택된 탭을 0번(첫 번째 항목)으로 설정
            if (binding.bottomNavigation.selectedItemId != R.id.navigation_page_1) {
                binding.bottomNavigation.selectedItemId = R.id.navigation_page_1
            }
        }

        binding.live.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, LiveFragment())
                .addToBackStack(null)
                .commit()
            this.hideActionBar(true)
            this.hideBottomNav(true)
        }

        setNdef()

        setBeacon()

//        createNotificationChannel("ssafy_channel", "ssafy")

//        checkPermissions()


        // Intent에서 navigate_to 값을 확인
        val navigateTo = intent.getStringExtra("navigate_to")
        if (navigateTo == "LiveFragment") {
            openLiveFragment()
        }


        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, HomeFragment())
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.navigation_page_1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, HomeFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, OrderFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_3 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, MyPageFragment())
                        .commit()
                    true
                }
                R.id.navigation_page_game -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, GameFragment())
                        .commit()
                    true
                }
                else -> false
            }


        }


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)


        //사이드 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.side_menu)

        navView.setNavigationItemSelectedListener {menuItem ->
            when (menuItem.itemId) {
                R.id.side_page_1 -> {
                    // "Map" 항목 클릭 시 처리할 내용
                    val fragmentTransaction = supportFragmentManager.beginTransaction()

                    // 프래그먼트가 이미 존재하는지 확인
                    val existingFragment = supportFragmentManager.findFragmentByTag("MapFragment")

                    // MapFragment가 없다면 교체하고, 있다면 그대로 유지
                    if (existingFragment == null) {
                        fragmentTransaction.replace(R.id.frame_layout_main, com.ssafy.smartstore_jetpack.ui.side.MapFragment(), "MapFragment")
                        fragmentTransaction.addToBackStack("MapFragment")
                        fragmentTransaction.commit()
                    }

                    // 드로어를 닫는다
                    drawerLayout.closeDrawer(GravityCompat.START)
                    binding.bottomNavigation.visibility = View.GONE

                    true
                }
                R.id.side_page_2 -> {
                    // "Call" 항목 클릭 시 처리할 내용
                    initShowDialog()

                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.side_page_3 -> {
                    openFragment(5)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }


        binding.bottomNavigation.setOnItemReselectedListener { item ->
            // 재선택시 다시 랜더링 하지 않기 위해 수정
            if(binding.bottomNavigation.selectedItemId != item.itemId){
                binding.bottomNavigation.selectedItemId = item.itemId
            }
        }
    }

    fun initShowDialog(){
        // AlertDialog 생성
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_call, null)


        //intent 생성해서 전화번호 넘기기
        val phoneNumber = "0547777777"

        val callText = dialogView.findViewById<TextView>(R.id.call2)

        callText.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }


        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // 다이얼로그 표시
        alertDialog.show()



    }


    private fun playCustomSound() {
        // MediaPlayer 초기화 및 파일 연결
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.main).apply {
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { // 삼점 메뉴 클릭 시
                drawerLayout.openDrawer(GravityCompat.START)  // 사이드 메뉴 열기 (GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }



    override fun onResume() {
        super.onResume()
        // 표준 dispatch System 보다 먼저 수행.
//        nAdapter.enableForegroundDispatch(this, pIntent, null, null)

        val preferences = getSharedPreferences("appState", Context.MODE_PRIVATE)
        val lastFragment = preferences.getString("lastActiveFragment", "HomeFragment")

        if (lastFragment == "MapFragment") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_main, MapFragment())
                .commit()
        }
        playCustomSound()
    }

    override fun onPause() {
        super.onPause()
//        nAdapter.disableForegroundDispatch(this)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout_main)
        if (currentFragment is MapFragment) {
            val preferences = getSharedPreferences("appState", Context.MODE_PRIVATE)
            preferences.edit()
                .putString("lastActiveFragment", "MapFragment")
                .apply()
        }

        mediaPlayer?.pause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val action = intent.action

        if (action == NfcAdapter.ACTION_TAG_DISCOVERED || action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag != null) {
                Log.d(TAG, "NFC 태그 감지: ${tag.id.contentToString()}")

                // NFC 태그 데이터 추출
                val rawData = extractNfcData(tag)
                if (rawData != null && rawData.startsWith("Table:")) {
                    val tableNumber = rawData.substringAfter("Table:").trim()
                    Log.d(TAG, "Extracted table number: $tableNumber")
                    showToast("Register Number $tableNumber Table.")
                    // ViewModel 상태 변경
                    mainActivityViewModel.changeState(true, tableNumber)

                    //주문 완료 order 프래그먼트로 이동
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout_main, OrderFragment())
                        .commit()


                } else {
                    showToast("Invalid NFC tag.")
                }
            }
        }
    }

    private fun extractNfcData(tag: Tag): String? {
        // 태그 데이터를 읽는 로직 구현
        // 여기서는 간단히 mock 데이터를 반환 (실제로는 NDEF 메시지 파싱 필요)
        return "Table:1" // 예제 데이터
    }

    fun openFragment(index:Int, key:String, value:Int){
        moveFragment(index, key, value)
    }

    fun openFragment(index: Int) {
        moveFragment(index, "", 0)
    }

    private fun moveFragment(index:Int, key:String, value:Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(index){
            //장바구니
            1 -> {
                // 재주문
                if(key.isNotBlank() && value != 0){
                    transaction.replace(
                        R.id.frame_layout_main,
                        OrderFragment()
                    )
                        .addToBackStack(null)
                }else{
                    transaction.replace(
                        R.id.frame_layout_main,
                        OrderFragment()
                    )
                        .addToBackStack(null)
                }
            }
            //주문 상세 보기
            2 -> transaction.replace(R.id.frame_layout_main, OrderDetailFragment())
                .addToBackStack(null)
            //메뉴 상세 보기
            3 -> transaction.replace(R.id.frame_layout_main, MenuDetailFragment())
                .addToBackStack(null)
            //map으로 가기
            4 -> transaction.replace(R.id.frame_layout_main, MapFragment())
                .addToBackStack(null)
            //logout
            5 -> {
                logout()
            }
            //comment로 가기
            6 -> transaction.replace(R.id.frame_layout_main, CommentFragment())
                .addToBackStack(null)
        }
        transaction.commit()
    }

    private fun logout(){
        //preference 지우기
        ApplicationClass.sharedPreferencesUtil.deleteUser()
        ApplicationClass.sharedPreferencesUtil.deleteUserCookie()

        //화면이동
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent)
    }

    fun hideBottomNav(state : Boolean){
        if(state) binding.bottomNavigation.visibility = View.GONE
        else binding.bottomNavigation.visibility = View.VISIBLE
    }

    fun hideActionBar(state : Boolean){
        if(state) binding.toolbar.visibility = View.GONE
        else binding.toolbar.visibility = View.VISIBLE
    }

    private fun setNdef(){

    }

    private fun setBeacon(){

    }

    private fun requestEnableBLE(){
        val callBLEEnableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBLEActivity.launch(callBLEEnableIntent)
        Log.d(TAG, "requestEnableBLE: ")
    }

    private val requestBLEActivity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() ){
        // 사용자의 블루투스 사용이 가능한지 확인
        if (bluetoothAdapter.isEnabled) {
            startScan()
        }
    }


    private fun startScan() {
        if ( !bluetoothAdapter.isEnabled ) {
            requestEnableBLE()
        }


        // 리전에 비컨이 있는지 없는지..정보를 받는 클래스 지정
        beaconManager.addMonitorNotifier(monitorNotifier)
        beaconManager.startMonitoring(region)

        //detacting되는 해당 region의 beacon정보를 받는 클래스 지정.
        beaconManager.addRangeNotifier(rangeNotifier)
        beaconManager.startRangingBeacons(region)
    }

    //모니터링 결과를 처리할 Notifier를 지정.
    // region에 해당하는 beacon 유무 판단
    var monitorNotifier: MonitorNotifier = object : MonitorNotifier {
        override fun didEnterRegion(region: Region) { //발견 함.
            Log.d(TAG, "I just saw an beacon for the first time!")
        }

        override fun didExitRegion(region: Region) { //발견 못함.
            Log.d(TAG, "I no longer see an beacon")
        }

        override fun didDetermineStateForRegion(state: Int, region: Region) { //상태변경
            Log.d(TAG, "I have just switched from seeing/not seeing beacons: $state")
        }
    }

    // 매초마다 해당 리전의 beacon 정보들을 collection으로 제공받아 처리한다.
    var rangeNotifier: RangeNotifier = object : RangeNotifier {
        override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
            beacons?.run {
                if (isNotEmpty()) {
                    forEach { beacon ->
                        if (beacon.distance <= BEACON_DISTANCE) {
                            if (shouldShowDialog()) { // 24시간 체크
                                showDialog()
                                saveDialogTime() // 다이얼로그 시간을 저장
                            }
                        }
                    }
                }
            }
        }
    }

    // 다이얼로그 표시 메서드
    private fun showDialog() {
        // 다이얼로그 바인딩 설정
        val dialogBinding = DialogStoreEventBinding.inflate(layoutInflater)


        // 다이얼로그 발생 전에 서버에서 코인 받는 메서드 작성
        mainActivityViewModel.updatecoin(user.id, 1000)
        Toast.makeText(this, "Earn 1000 coins!", Toast.LENGTH_SHORT).show()


        // 다이얼로그 빌더 생성
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss() // 다이얼로그 닫기
            }

        // 다이얼로그 표시
        dialogBuilder.create().show()
    }


    private fun saveDialogTime() {
        val sharedPreferences = getSharedPreferences("beacon_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("last_dialog_time", System.currentTimeMillis()) // 현재 시간 저장
        editor.apply() // 변경 사항 저장
    }

    private fun shouldShowDialog(): Boolean {
        val sharedPreferences = getSharedPreferences("beacon_prefs", Context.MODE_PRIVATE)
        val lastDialogTime = sharedPreferences.getLong("last_dialog_time", 0) // 마지막 다이얼로그 시간 불러오기
        val currentTime = System.currentTimeMillis() // 현재 시간
        val DIALOG_INTERVAL = 86400 * 1000 // 24시간(86400초)을 밀리초로 변환

        // 마지막 다이얼로그 표시 시간과 현재 시간 비교
        return currentTime - lastDialogTime >= DIALOG_INTERVAL
    }
    // BottomNavigationView에서 배지 설정하기
    fun updateBadge() {
        mainActivityViewModel.refreshBadge()
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // "Order" 항목의 MenuItem을 가져옴 (여기서는 index 1에 있다고 가정)
        val menuItem = bottomNav.menu.findItem(R.id.navigation_page_2)

        // 해당 메뉴 항목에 배지가 없으면 새로 추가
        val badge = bottomNav.getOrCreateBadge(menuItem.itemId)
        badge.isVisible = mainActivityViewModel.cartItemCount > 0  // 상품 갯수가 0보다 클 때만 배지 표시
        badge.number = mainActivityViewModel.cartItemCount  // 배지에 상품 갯수 표시
        badge.backgroundColor = ContextCompat.getColor(this, R.color.red)  // 배지 색상 설정
    }


    //FCM 기능 구현
    private fun openLiveFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_main, LiveFragment())
            .addToBackStack(null)
            .commit()
    }


}


