package com.kotlin

import kotlinx.coroutines.*


fun main() {
    GlobalScope.launch {
        delay(1000L)
        print("World!")
    }
    print("hello,")
    Thread.sleep(2000L)
}