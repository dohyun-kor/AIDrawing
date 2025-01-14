package com.ssafy.smartstore_jetpack.data.model.dto

data class ShoppingCart(
    val menuId: Int,
    val menuImg: String,
    val menuName: String,
    var menuCnt: Int,
    val menuPrice: Int,
    var totalPrice: Int = menuCnt*menuPrice,
    val type: String,
    val size : Int,
    val shot : Int
){
    fun addDupMenu(shoppingCart: ShoppingCart){
        this.menuCnt++
        this.totalPrice = this.menuCnt * this.menuPrice
    }

}