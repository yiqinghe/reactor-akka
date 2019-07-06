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

        try {
            println(Thread.currentThread().name+"-----1111")
            var ioException: IOException = IOException()
            throw ioException
            return block()
        }catch (e: IOException){
            e.printStackTrace()
        }
        retryIO2(suspend{println(Thread.currentThread().name+"2222callback")})
        delay(currDelay)
        //currDelay = (currDelay * 2).coerceAtMost(60000L)
        return block()
}

suspend fun <T> retryIO2(block: suspend ()-> T):T{
    var currDelay = 1000L
    while (true) {
        try {
            println(Thread.currentThread().name + "-----2222")
            return block()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        delay(currDelay)
        //currDelay = (currDelay * 2).coerceAtMost(60000L)
        return block()
    }
}