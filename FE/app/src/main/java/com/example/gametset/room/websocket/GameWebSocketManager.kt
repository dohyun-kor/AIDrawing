package com.example.gametset.room.websocket

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.gametset.room.ui.gameRoom.PlayGameFragment
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

private val TAG = "WebSocket"

class GameWebSocketManager private constructor() {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val drawingEventListeners = mutableListOf<(JSONObject) -> Unit>()
    private val messageEventListeners = mutableListOf<(JSONObject) -> Unit>()
    private val existingRoomUserListeners = mutableListOf<(JSONObject) -> Unit>()

    //    private val existingRoomUserListeners : ((JSONObject) -> Unit)? = null
//    private val joinListeners = mutableListOf<(JSONObject) -> Unit>()
    private var joinListeners: ((JSONObject) -> Unit)? = null
    private val hostChangeListeners = mutableListOf<(JSONObject) -> Unit>()
    private val leaveEventListeners = mutableListOf<(JSONObject) -> Unit>()
    private val colorChangeListeners = mutableListOf<(JSONObject) -> Unit>()
    private val roomInfoChangeListeners = mutableListOf<(JSONObject) -> Unit>()

    // 집 와서 한거
    private var startListeners: ((JSONObject) -> Unit)? = null
    private var topicselectListeners: ((JSONObject) -> Unit)? = null
    private var remaintimeListeners: ((JSONObject) -> Unit)? = null
    private var currentroundListeners: ((JSONObject) -> Unit)? = null
    private var topicListeners: ((JSONObject) -> Unit)? = null

    //    private var topicselectedListeners: ((JSONObject) -> Unit)? = null
    private var correctListeners: ((JSONObject) -> Unit)? = null
    private var nextroundListeners: ((JSONObject) -> Unit)? = null
    private var endgameListeners: ((JSONObject) -> Unit)? = null
    private var gamecanstartListeners: ((JSONObject) -> Unit)? = null
    private var gamecantstartListeners: ((JSONObject) -> Unit)? = null
    private var changeroominfoListeners: ((JSONObject) -> Unit)? = null
    private var scoreListeners: ((JSONObject) -> Unit)? = null
    private var winnerListeners: ((JSONObject) -> Unit)? = null
    private var joinMessageListeners: ((JSONObject) -> Unit)? = null
    private var leaveMessageListeners: ((JSONObject) -> Unit)? = null
    private var correctMessageListeners: ((JSONObject) -> Unit)? = null
    private var aiDrawingListeners: ((JSONObject) -> Unit)? = null
    private var answerMessageListeners: ((JSONObject) -> Unit)? = null
    private var strokeChangeListeners: ((JSONObject) -> Unit)? = null

    // 메인 스레드에서 실행될 Handler
    private val pingHandler = Handler(Looper.getMainLooper())
    private val pingRunnable = object : Runnable {
        override fun run() {
            webSocket?.let { socket ->
                socket.send("ping")
                Log.d("WebSocket", "Sent ping to server")
                pingHandler.postDelayed(this, 20000)
            }
        }
    }

    private fun startPinging() {
        pingHandler.postDelayed(pingRunnable, 20000)
    }

    private fun stopPinging() {
        pingHandler.removeCallbacks(pingRunnable)
    }

    // 드로잉 이벤트 리스너 등록
    fun addDrawingEventListener(listener: (JSONObject) -> Unit) {
        drawingEventListeners.add(listener)
    }

    // 드로잉 이벤트 리스너 제거
    fun removeDrawingEventListener(listener: (JSONObject) -> Unit) {
        drawingEventListeners.remove(listener)
    }

    // 모든 드로잉 이벤트 리스너 제거
    fun clearDrawingEventListeners() {
        drawingEventListeners.clear()
    }

    // 메시지 이벤트 리스너 등록
    fun addMessageEventListener(listener: (JSONObject) -> Unit) {
        messageEventListeners.add(listener)
    }

    // 메시지 이벤트 리스너 제거
    fun removeMessageEventListener(listener: (JSONObject) -> Unit) {
        messageEventListeners.remove(listener)
    }

    // 모든 메시지 이벤트 리스너 제거
    fun clearMessageEventListeners() {
        messageEventListeners.clear()
    }

    // 방에 존재하는 유저 이벤트 리스너 추가
    fun addExistingRoomUserListeners(listener: (JSONObject) -> Unit) {
        existingRoomUserListeners.add(listener)
    }

    // 방에 존재하는 유저 이벤트 리스너 삭제
    fun clearExistingRoomUserListeners() {
        existingRoomUserListeners.clear()
    }

    // 유저 입장 추가 리스너
    fun addJoinListeners(listener: (JSONObject) -> Unit) {
//        joinListeners.add(listener)
        joinListeners = listener
    }

    // 유저 입장 삭제 리스너
    fun clearJoinListeners() {
//        joinListeners.clear()
        joinListeners = null
    }

    fun addLeaveListeners(listener: (JSONObject) -> Unit) {
        leaveEventListeners.add(listener)
    }

    fun addColorChangeListeners(listener: (JSONObject) -> Unit) {
        colorChangeListeners.add(listener)
    }

    // hostChange 리스너 추가 메서드
    fun addHostChangeListener(listener: (JSONObject) -> Unit) {
        hostChangeListeners.add(listener)
    }

    // hostChange 리스너 제거 메서드
    fun clearHostChangeListeners() {
        hostChangeListeners.clear()
    }

    // 집 와서 한거
    fun addStartListeners(listener: (JSONObject) -> Unit) {
        startListeners = listener
    }

    fun clearStartListeners() {
        startListeners = null
    }

    fun addTopicSelectListeners(listener: (JSONObject) -> Unit) {
        topicselectListeners = listener
    }

    fun clearTopicSelectListeners() {
        topicselectListeners = null
    }

    fun addRemainTimeListeners(listener: (JSONObject) -> Unit) {
        remaintimeListeners = listener
    }

    fun clearRemainTimeListeners() {
        remaintimeListeners = null
    }

    fun addCurrentRoundListeners(listener: (JSONObject) -> Unit) {
        currentroundListeners = listener
    }

    fun clearCurrentRoundListeners() {
        currentroundListeners = null
    }

    fun addTopicListeners(listener: (JSONObject) -> Unit) {
        topicListeners = listener
    }

    fun clearTopicListeners() {
        topicListeners = null
    }

//    fun addTopicSelectedListeners(listener: (JSONObject) -> Unit) {
//        topicselectedListeners = listener
//    }

//    fun clearTopicSelectedListeners() {
//        topicselectedListeners = null
//    }

    fun addCorrectListeners(listener: (JSONObject) -> Unit) {
        correctListeners = listener
    }

    fun clearCorrectListeners() {
        correctListeners = null
    }

    fun addNextRoundListeners(listener: (JSONObject) -> Unit) {
        nextroundListeners = listener
    }

    fun clearNextRoundListeners() {
        nextroundListeners = null
    }

    fun addEndGameListeners(listener: (JSONObject) -> Unit) {
        endgameListeners = listener
    }

    fun clearEndGameListeners() {
        endgameListeners = null
    }

    fun addGameCanStartListeners(listener: (JSONObject) -> Unit) {
        gamecanstartListeners = listener
    }

    fun clearGameCanStartListeners() {
        gamecanstartListeners = null
    }

    fun addGameCantStartListeners(listener: (JSONObject) -> Unit) {
        gamecantstartListeners = listener
    }

    fun clearGameCantStartListeners() {
        gamecantstartListeners = null
    }

    fun addChangeRoomInfoListeners(listener: (JSONObject) -> Unit) {
        changeroominfoListeners = listener
    }

    fun clearChangeRoomInfoListeners() {
        changeroominfoListeners = null
    }

    fun addScoreListeners(listener: (JSONObject) -> Unit) {
        scoreListeners = listener
    }

    fun clearScoreListeners() {
        scoreListeners = null
    }

    fun addWinnerListeners(listener: (JSONObject) -> Unit) {
        winnerListeners = listener
    }

    fun clearWinnerListeners() {
        winnerListeners = null
    }

    fun addjoinMessageListeners(listener: (JSONObject) -> Unit) {
        joinMessageListeners = listener
    }

    fun clearjoinMessageListeners() {
        joinMessageListeners = null
    }

    fun addleaveMessageListeners(listener: (JSONObject) -> Unit) {
        leaveMessageListeners = listener
    }

    fun clearleaveMessageListeners() {
        leaveMessageListeners = null
    }

    fun addcorrectMessageListeners(listener: (JSONObject) -> Unit) {
        correctMessageListeners = listener
    }

    fun clearcorrectMessageListeners() {
        correctMessageListeners = null
    }

    fun addAiDrawingListeners(listener: (JSONObject) -> Unit) {
        aiDrawingListeners = listener
    }

    fun clearAiDrawingListeners() {
        aiDrawingListeners = null
    }

    fun addanswerMessageListeners(listener: (JSONObject) -> Unit) {
        answerMessageListeners = listener
    }

    fun clearanswerMessageListeners() {
        answerMessageListeners = null
    }

    fun addStrokeChangeListeners(listener: (JSONObject) -> Unit) {
        strokeChangeListeners = listener
    }

    fun clearStrokeChangeListeners() {
        strokeChangeListeners = null
    }

    private inner class EventWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d("WebSocket", "Connection opened")
            startPinging()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val json = JSONObject(text)
                val event = json.optString("event", "")

                Log.d(TAG, "onMessage: json = $text")
                Log.d(TAG, "onMessage: json = $event")

                when (event) {
                    "ping" -> {
                        webSocket.send("pong")
                        Log.d(TAG, "Ping received, sent pong")
                    }

                    "pong" -> {
                        Log.d(TAG, "Pong received")
                    }

                    "draw", "cleardrawing" -> {
//                      드로잉 이벤트 리스너들에게 메시지 전달
                        drawingEventListeners.forEach { listener ->
                            Log.d(TAG, "leave: $json")
                            listener(json)
                        }
                    }

                    "chat" -> {
                        // 메시지 이벤트 리스너들에게 메시지 전달
                        messageEventListeners.forEach { listener ->
                            listener(json)
                        }
                    }

                    "existinguser" -> {
//                        Log.d(TAG, "Existing user event received: $json")
                        existingRoomUserListeners.forEach { listener ->
                            listener(json)
                        }
                    }

                    "join" -> {
//                        joinListeners.forEach { listener ->
//                            listener(json)
//                            Log.d(TAG, "$listener")
//                        }
                        joinListeners?.invoke(json)
                    }

                    "leave" -> {
                        leaveEventListeners.forEach { listener ->
                            listener(json)
                            Log.d(TAG, "leave: $json")
                        }
                    }

                    "hostchange" -> {
                        hostChangeListeners.forEach { listener ->
                            listener(json)
                        }
                    }

                    "colorchange" -> {
                        colorChangeListeners.forEach { listener ->
                            listener(json)
                        }
                    }

                    // 집 와서 한거
                    "start" -> {    // 현재 스타트 안 날아옴
                        Log.d(TAG, "start: $json")
                        startListeners?.invoke(json)
                    }

                    "topicselect" -> {
                        Log.d(TAG, "topicselect: $json")
                        topicselectListeners?.invoke(json)
                    }

                    "remaintime" -> {
                        Log.d(TAG, "remaintime: $json")
                        remaintimeListeners?.invoke(json)
                    }

                    "currentround" -> {
                        Log.d(TAG, "currentround: $json")
                        currentroundListeners?.invoke(json)
                    }

                    "topic" -> {    // 현재 토픽 안 날아옴
                        Log.d(TAG, "topic: $json")
                        Log.d(TAG, "topic: $topicListeners")
                        topicListeners?.invoke(json)
                    }

//                    "topicselected" -> {
//                        Log.d(TAG, "topicselected: $json")
//                        topicselectedListeners?.invoke(json)
//                    }

                    "correct" -> {
                        Log.d(TAG, "correct: $json")
                        correctListeners?.invoke(json)
                    }

                    "nextround" -> {
                        Log.d(TAG, "nextround: $json")
                        nextroundListeners?.invoke(json)
                    }

                    "endgame" -> {
                        Log.d(TAG, "endgame: $json")
                        endgameListeners?.invoke(json)
                    }

                    "gamecanstart" -> {
                        Log.d(TAG, "gamecanstart: $json")
                        gamecanstartListeners?.invoke(json)
                    }

                    "gamecantstart" -> {
                        Log.d(TAG, "gamecantstart: $json")
                        gamecantstartListeners?.invoke(json)
                    }

                    "changeroominfo" -> {
                        Log.d(TAG, "changeroominfo: $json")
                        changeroominfoListeners?.invoke(json)
                    }

                    "score" -> {
                        Log.d(TAG, "score: $json")
                        scoreListeners?.invoke(json)
                    }

                    "winner" -> {
                        Log.d(TAG, "winner: $json")
                        winnerListeners?.invoke(json)
                    }

                    "correctchat" -> {
                        Log.d(TAG, "winner: $json")
                        correctMessageListeners?.invoke(json)
                    }
                    // 필요한 경우 다른 이벤트 처리 추가

                    "joinmessage" -> {
                        Log.d(TAG, "winner: $json")
                        joinMessageListeners?.invoke(json)
                    }

                    "leavemessage" -> {
                        Log.d(TAG, "winner: $json")
                        leaveMessageListeners?.invoke(json)
                    }

                    "aidrawing" -> {
                        Log.d(TAG, "winner: $json")
                        aiDrawingListeners?.invoke(json)
                    }

                    "answerchat" -> {
                        Log.d(TAG, "winner: $json")
                        answerMessageListeners?.invoke(json)
                    }

                    "strokechange"-> {
                        Log.d(TAG, "winner: $json")
                        strokeChangeListeners?.invoke(json)
                    }
                }
            } catch (e: Exception) {
                Log.e("WebSocket", "Error parsing message", e)
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e("WebSocket", "Connection failed", t)
            stopPinging()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "Connection closing: $reason")
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d("WebSocket", "Connection closed: $reason")
            stopPinging()
        }
    }

    companion object {
        private const val WS_URL = "wss://i12d108.p.ssafy.io/api/ws"
//        private const val WS_URL = "ws://192.168.100.203:9987/api/ws"

        @Volatile
        private var instance: GameWebSocketManager? = null

        fun getInstance(): GameWebSocketManager {
            return instance ?: synchronized(this) {
                instance ?: GameWebSocketManager().also { instance = it }
            }
        }
    }

    fun connect(): WebSocket {
        val request = Request.Builder()
            .url(WS_URL)
            .build()

        webSocket = client.newWebSocket(request, EventWebSocketListener())
        return webSocket!!
    }

    fun getWebSocket(): WebSocket? = webSocket

    fun disconnect() {
        stopPinging()
        webSocket?.close(1000, "App closed")
        webSocket = null
    }

    fun sendMessage(message: String) {
        webSocket?.send(message) ?: run {
            Log.e("WebSocket", "WebSocket is not connected")
        }
    }
}