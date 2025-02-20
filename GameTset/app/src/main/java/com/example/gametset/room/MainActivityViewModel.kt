package com.example.gametset.room

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.gametset.room.base.ApplicationClass
import com.example.gametset.room.data.model.dto.Friend
import com.example.gametset.room.data.model.dto.FriendRequest
import com.example.gametset.room.data.model.dto.MyItemDto
import com.example.gametset.room.data.model.dto.OneRoomDto
import com.example.gametset.room.data.model.dto.RoomChangeDto
import com.example.gametset.room.data.model.dto.SearchFriendInfo
import com.example.gametset.room.data.model.dto.StoreDto
import com.example.gametset.room.data.model.dto.UserDto
import com.example.gametset.room.data.model.response.UserResponse
import com.example.gametset.room.data.remote.RetrofitUtil
import com.example.gametset.room.data.model.dto.RoomDto
import com.example.gametset.room.data.model.dto.RoomCreateDto
import com.example.gametset.room.data.model.dto.UserProfileChangeDto
import kotlinx.coroutines.launch
import com.example.gametset.room.data.model.MyRoomItem
import com.example.gametset.room.data.model.dto.PictureDto
import com.example.gametset.room.data.model.response.MyRoomItemResponse

private val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {
    private val _currentUser = MutableLiveData<UserDto?>()
    val currentUser: LiveData<UserDto?> = _currentUser

    fun setCurrentUser(user: UserDto) {
        _currentUser.value = user
    }

    private var _searchFriendInfo = MutableLiveData<SearchFriendInfo>()
    val searchFriendInfo: LiveData<SearchFriendInfo>
        get() = _searchFriendInfo

    fun getFrindInfo(id: String) {
        viewModelScope.launch {
            runCatching {
                Log.d(TAG, "LiveData updated with userService.getIdUserId(id): 시작")
                val searchId = RetrofitUtil.userService.getUserDetailsByNickname(id)
//                Log.d(TAG, "LiveData updated with userService.getIdUserId(id): $searchId")
//                val userInfo = RetrofitUtil.userService.getUserInfo(searchId)  //검색한 user의 정보
//                Log.d(TAG, "LiveData updated with userService.getUserInfo(searchId.userId): $userInfo")
//                val itemInfo= RetrofitUtil.storeService.getOneItem(userInfo.userProfileItemId)
                val itemInfo = RetrofitUtil.storeService.getOneItem(searchId.userProfileItemId)
                Log.d(
                    TAG,
                    "LiveData updated with storeService.getOneItem(userInfo.userProfileItemId): $itemInfo"
                )
//                val friendInfo=SearchFriendInfo(userInfo.userId,userInfo.nickname,itemInfo.link)
                val friendInfo = SearchFriendInfo(searchId.userId, searchId.nickname, itemInfo.link)
                Log.d(TAG, "LiveData updated with getFrindInfo0: ${searchFriendInfo.value}")
                friendInfo
            }.onSuccess {
                _searchFriendInfo.value = it
                Log.d(TAG, "LiveData updated with getFrindInfo1: $it") // LiveData가 업데이트된 후 로그
                Log.d(
                    TAG,
                    "LiveData updated with getFrindInfo2: ${_searchFriendInfo.value}"
                ) // LiveData가 업데이트된 후 로그
                Log.d(
                    TAG,
                    "LiveData updated with getFrindInfo3: ${searchFriendInfo.value}"
                ) // LiveData가 업데이트된 후 로그
            }.onFailure { exception ->
                Log.d(TAG, "getFrindInfo onFailure: $exception") // 실패한 경우 로그
            }
        }
    }

    private val _userinfoList = MutableLiveData<List<UserResponse>>(emptyList())

    //    private val _friendList = MutableLiveData<List<Friend>>()
    val userinfoList: LiveData<List<UserResponse>>
        get() = _userinfoList

    private val _friendList = MutableLiveData<List<Friend>>(emptyList())
    val friendList: LiveData<List<Friend>> = _friendList

    private val _acceptedFriends = MutableLiveData<List<Friend>>(emptyList())
    val acceptedFriends: LiveData<List<Friend>> = _acceptedFriends

    private val _receivedRequests = MutableLiveData<List<Friend>>(emptyList())
    val receivedRequests: LiveData<List<Friend>> = _receivedRequests

    private val _sentRequests = MutableLiveData<List<Friend>>(emptyList())
    val sentRequests: LiveData<List<Friend>> = _sentRequests

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // 친구 목록 조회 및 분류
    fun getFriendList() {
        viewModelScope.launch {
            try {
                val currentUserId = ApplicationClass.sharedPreferencesUtil.getUser().userId
                val allFriends = RetrofitUtil.friendService.friendList(currentUserId)

                // 친구 상태별로 분류
                val accepted = mutableListOf<Friend>()
                val received = mutableListOf<Friend>()
                val sent = mutableListOf<Friend>()

                allFriends.forEach { friend ->
                    // 사용자 정보 조회
                    val userInfo = if (currentUserId == friend.userId) {
                        RetrofitUtil.userService.getUserInfo(friend.friendId)
                    } else {
                        RetrofitUtil.userService.getUserInfo(friend.userId)
                    }

                    // 프로필 아이템 정보 조회
                    val itemInfo = RetrofitUtil.storeService.getOneItem(userInfo.userProfileItemId)

                    // 친구 정보 업데이트
                    friend.nickname = userInfo.nickname
                    friend.userProfileUrl = itemInfo.link

                    // 상태별로 분류
                    when {
                        friend.status == "ACCEPTED" -> accepted.add(friend)
                        friend.status == "PENDING" && friend.friendId == currentUserId -> {
                            // 내가 받은 요청 (상대방이 나에게 보낸 요청)
                            received.add(friend)
                        }

                        friend.status == "PENDING" && friend.userId == currentUserId -> {
                            // 내가 보낸 요청
                            sent.add(friend)
                        }
                    }
                }

                // LiveData 업데이트
                _acceptedFriends.value = accepted
                _receivedRequests.value = received
                _sentRequests.value = sent
                _friendList.value = allFriends

                Log.d(
                    TAG,
                    "Friends updated - Accepted: ${accepted.size}, Received: ${received.size}, Sent: ${sent.size}"
                )
                Log.d(TAG, "Sent requests: $sent")
                Log.d(TAG, "Received requests: $received")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get friend list", e)
            }
        }
    }

    // 친구 요청
    fun requestFriend(friendRequest: FriendRequest) {
        viewModelScope.launch {
            try {
                val success = RetrofitUtil.friendService.friendRequest(friendRequest)
                if (success) {
                    getFriendList()  // 친구 목록 갱신
                    Log.d(TAG, "Friend request sent successfully")
                } else {
                    Log.d(TAG, "Friend request failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send friend request", e)
            }
        }
    }

    // 친구 요청 수락/거절
    fun acceptFriend(friend: Friend) {
        viewModelScope.launch {
            try {
                val success = RetrofitUtil.friendService.friendAccept(friend)
                if (success) {
                    getFriendList()  // 친구 목록 갱신
                    Log.d(TAG, "Friend request ${friend.status} successfully")
                } else {
                    Log.d(TAG, "Failed to ${friend.status} friend request")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to process friend request", e)
            }
        }
    }

    private val _itemInfo = MutableLiveData<StoreDto>()
    val itemInfo: LiveData<StoreDto>
        get() = _itemInfo

    suspend fun getItemInfo(itemId: Int): StoreDto {
        viewModelScope.launch {
            runCatching {
                val searchItem =
                    RetrofitUtil.storeService.getOneItem(itemId)
                Log.d(TAG, "getItemInfo: $searchItem")
                searchItem
            }.onSuccess {
                _itemInfo.value = it
                Log.d(TAG, "getItemInfo Success: $it") // LiveData가 업데이트된 후 로그
            }.onFailure { exception ->
                Log.d(TAG, "getItemInfo onFailure: $exception") // 실패한 경우 로그
            }
        }
        return _itemInfo.value!!
    }

    private val _roomList = MutableLiveData<List<OneRoomDto>>()
    val roomList: LiveData<List<OneRoomDto>> = _roomList

    fun getRoom() {
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.RoomService.getAllRoom()
            }.onSuccess { rooms ->
                _roomList.value = rooms
                Log.d(TAG, "Room info: $rooms")
            }.onFailure { exception ->
                Log.d(TAG, "getRoom onFailure: $exception")
            }
        }
    }


    fun createRoom(roomCreateDto: RoomCreateDto, onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val roomId = RetrofitUtil.RoomService.createRoom(roomCreateDto)
                onSuccess(roomId)
            } catch (e: Exception) {
                // 에러 처리
                Log.e("CreateRoom", "방 생성 실패", e)
            }
        }
    }

    fun getOneRoomInfo(roomId: Int, onResult: (OneRoomDto?) -> Unit) {
        viewModelScope.launch {
            try {
                val result = RetrofitUtil.RoomService.getOneRoom(roomId)
                onResult(result)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching room info", e)
                onResult(null)
            }
        }
    }

    fun putOneRoomInfo(roomId: Int, roomChangeDto: RoomChangeDto, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = RetrofitUtil.RoomService.changeRoomInfo(roomId, roomChangeDto)
                if (result) {
                    // 변경된 방의 정보만 새로 가져옴
                    getOneRoomInfo(roomId) { _ ->  // 사용하지 않는 파라미터를 _로 변경
                        Log.d(TAG, "Room info updated successfully")
                    }
                } else {
                    Log.d(TAG, "Failed to update room info")
                }
                onResult(result)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating room info", e)
                onResult(false)
            }
        }
    }


    // 친구 요청 취소
    fun cancelFriendRequest(friendId: Int) {
        viewModelScope.launch {
            try {
                val success = RetrofitUtil.friendService.cancelFriendRequest(friendId)
                if (success) {
                    getFriendList()  // 친구 목록 갱신
                    Log.d(TAG, "Friend request canceled successfully")
                } else {
                    Log.d(TAG, "Failed to cancel friend request")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cancel friend request", e)
            }
        }

    }

    private val _userProfileData = MutableLiveData<UserDto>()
    val userProfileData: LiveData<UserDto> = _userProfileData

    fun userInfo(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitUtil.userService.getUserInfo(userId)
                val userDto = UserDto(
                    userId = response.userId,
                    email = response.email ?: "",
                    id = response.id,
                    password = "",
                    nickname = response.nickname,
                    point = response.points,
                    gamesWon = response.gameswon,
                    totalGames = response.totalgames,
                    level = response.level,
                    exp = response.exp,
                    userProfileItemId = response.userProfileItemId,
                    token = ""
                )
                _userProfileData.value = userDto
            } catch (e: Exception) {
                Log.e("ProfilePopUp", "Failed to get user info", e)
            }
        }
    }

    fun getOneItem(itemId: Int, onResult: (StoreDto?) -> Unit) {
        viewModelScope.launch {
            try{
                val result = RetrofitUtil.storeService.getOneItem(itemId)
                onResult(result)
            } catch (e: Exception){
                Log.e(TAG, "Error getting a item info")
            }
        }
    }

    fun getUserItems(userId: Int, onResult: (List<MyItemDto>) -> Unit) {
        viewModelScope.launch {
            try {
                val result = RetrofitUtil.myItemService.myItemsList(userId)
                onResult(result)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load user items")
            }
        }
    }

    fun changeUserProfile(userId:Int, userProfileChangeDto: UserProfileChangeDto, onResult: (Boolean) -> Unit ) {
        viewModelScope.launch {
            try {
                val result = RetrofitUtil.userService.userProfileUpdate(userId, userProfileChangeDto)
                onResult(result)
            } catch(e:Exception) {
                Log.e(TAG, "Failed to update user profile")
            }
        }
    }

    fun checkAndUpdateNickname(userId: Int, newNickname: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val isUsed = RetrofitUtil.userService.isUsedNickname(newNickname)

                if (isUsed) {
                    onResult("중복된 닉네임입니다")
                } else {
                    val success = RetrofitUtil.userService.userNicknameUpdate(userId, newNickname)

                    if (success) {
                        userInfo(userId)
                        onResult(null)  // 성공시 null 반환
                    } else {
                        onResult("닉네임 변경에 실패했습니다")
                    }
                }
            } catch (e: Exception) {
                onResult("오류가 발생했습니다")
            }
        }
    }

    private val _roomInfo = MutableLiveData<RoomChangeDto>()
    val roomInfo: LiveData<RoomChangeDto> get() = _roomInfo

    fun setRoomInfo(roomChangeDto: RoomChangeDto) {
        _roomInfo.value = roomChangeDto
    }

    private val _friendMyRoomItems = MutableLiveData<List<PictureDto>>()
    val friendMyRoomItems: LiveData<List<PictureDto>> get() = _friendMyRoomItems

    fun getFriendMyRoom(friendId: Int) {
        viewModelScope.launch {
            try {
                Log.d("MainActivityViewModel", "Getting friend's myroom items for id: $friendId")
                val items = RetrofitUtil.myRoomService.getMyRoomItems(friendId)
                Log.d("MainActivityViewModel", "Received items: $items")
                _friendMyRoomItems.value = items
            } catch (e: Exception) {
                Log.e("MainActivityViewModel", "Error getting friend's myroom", e)
            }
        }
    }

    fun getItemImage(itemId: Int, onImageUrlReceived: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val item = RetrofitUtil.storeService.getOneItem(itemId)
                onImageUrlReceived(item.link)
            } catch (e: Exception) {
                Log.e("MainActivityViewModel", "getItemImage error", e)
            }
        }
    }

}




