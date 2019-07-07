package com.kotlin

import kotlinx.coroutines.*
import java.io.IOException


fun main() {

    GlobalScope.launch {
        retryIO(suspend { println(Thread.currentThread().name+"1111callback") })
        //retryIO2(suspend{print("3333")})
    }
    Thread.sleep(800000L)
}


suspend fun <T> retryIO(block: suspend ()-> T):T{
    var currDelay = 3000L


            println(Thread.currentThread().name+"-----1111")

        retryIO2(suspend{println(Thread.currentThread().name+"2222callback")})
        Thread.sleep(currDelay)
        //currDelay = (currDelay * 2).coerceAtMost(60000L)
        return block()
}

suspend fun <T> retryIO2(block: suspend ()-> T):T{
    var currDelay = 1000L
    var count : Int = 0
    while (count < 3) {
            println(Thread.currentThread().name + "-----2222")
        delay(currDelay)
        count++
        //currDelay = (currDelay * 2).coerceAtMost(60000L)
    }
    return block()
}