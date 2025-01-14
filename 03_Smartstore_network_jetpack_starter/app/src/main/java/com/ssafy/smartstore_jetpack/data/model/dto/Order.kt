package com.ssafy.smartstore_jetpack.data.model.dto

data class Order (
    var id: Int,
    var userId: String,
    var orderTable: String,
    var orderTime: String,
    var completed: String,
    val details: ArrayList<OrderDetail> = ArrayList(),
    val coin: Int) {

    constructor():this(0,"","","","", ArrayList(), 0)
}
