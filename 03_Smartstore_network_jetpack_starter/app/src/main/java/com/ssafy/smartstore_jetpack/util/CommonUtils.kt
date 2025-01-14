package com.ssafy.smartstore_jetpack.util

import com.ssafy.smartstore_jetpack.base.ApplicationClass
import com.ssafy.smartstore_jetpack.data.model.response.OrderResponse
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CommonUtils {

    //천단위 콤마
    fun makeComma(num: Int): String {
        val comma = DecimalFormat("#,###")
        return "₩ ${comma.format(num)}"
    }

    //날짜 포맷 출력
    fun dateformatYMDHM(time:Date):String{
        val format = SimpleDateFormat("yyyy.MM.dd. HH:mm", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    fun dateformatYMD(time: Date):String{
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    fun dateformatMD(time: Date):String{
        val format = SimpleDateFormat("MMMM dd", Locale.US)
        format.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return format.format(time)
    }

    // 시간 계산을 통해 완성된 제품인지 확인
    fun isOrderCompleted(order: OrderResponse): String {
        return if( checkTime(order.orderDate.time))  "Complete" else "waiting..."
    }

    private fun checkTime(time:Long):Boolean{
        val curTime = (Date().time+60*60*9*1000)

        return (curTime - time) > ApplicationClass.ORDER_COMPLETED_TIME
    }

    // 주문 목록에서 총가격, 주문 개수 구하여 List로 반환한다.
    fun calcTotalPrice(orderList: List<OrderResponse>): List<OrderResponse>{
        orderList.forEach { order ->
            calcTotalPrice(order)
        }
        return orderList
    }

    fun calcTotalPrice(order: OrderResponse): OrderResponse {
        order.details.forEach { detail ->
            order.totalPrice += detail.sumPrice
            order.orderCount += detail.quantity
        }
        order.totalPrice -= order.details[0].coin
        return order
    }
}