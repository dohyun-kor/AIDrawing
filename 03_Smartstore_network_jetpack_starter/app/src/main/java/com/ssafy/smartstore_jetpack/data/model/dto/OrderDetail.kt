package com.ssafy.smartstore_jetpack.data.model.dto

data class OrderDetail (
    var id: Int,
    var orderId: Int,
    var productId: Int,
    var quantity: Int,
    var csize : Int,
    var cshot : Int, ) {

    var unitPrice:Int = 0
    var img:String = ""
    var productName:String = ""


    constructor(productId: Int, quantity: Int , csize: Int, cshot: Int) :this(0, 0, productId, quantity, csize, cshot)
    constructor():this(0,0,0,0)

}
