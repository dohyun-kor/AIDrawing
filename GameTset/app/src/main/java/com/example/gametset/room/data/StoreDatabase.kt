package com.example.gametset.room.data

import com.example.gametset.room.data.model.dto.StoreDto

object StoreDatabase {
    fun generateDummyData(): List<StoreDto> {
        val itemDataList = mutableListOf<StoreDto>()

        // category "one" 아이템 생성 (아이템 1~10)
        for (i in 1..10) {
            val itemId = "item${String.format("%02d", i)}"
            val name = "아이템 ${('A' + (i - 1)).toChar()}"
            val category = "one"
            val price = 1000 + (i * 500)  // 아이템마다 다른 가격
            val description = "$name 설명"
            val link = ""

            itemDataList.add(StoreDto(itemId, name, category, price, description, link))
        }

        // category "two" 아이템 생성 (아이템 11~20)
        for (i in 1..10) {  // 1부터 시작하도록 변경
            val itemId = "item${String.format("%02d", i + 10)}"
            val name = "아이템 ${('A' + (i - 1)).toChar()}"  // A부터 다시 시작
            val category = "two"
            val price = 2000 + (i * 500)  // 다른 가격대 설정
            val description = "$name 설명"
            val link = ""

            itemDataList.add(StoreDto(itemId, name, category, price, description, link))
        }

        return itemDataList
    }
}