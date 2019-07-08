package com.kotlin

import kotlinx.coroutines.*
import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import java.io.IOException


fun main() {

    GlobalScope.launch {
       // retryIO(suspend { println(Thread.currentThread().name+"1111callback") })
        //retryIO2(suspend{print("3333")})
       //var result :Int =  test1()
        var count :Int = 0
        while (count < 5) {
            GlobalScope.async {
                //test1()
                syncHttp(block = {
                    var entity: HttpEntity = it.entity
                    println(Thread.currentThread().name + "httpcallback---" + EntityUtils.toString(entity))
                })

//            syncHttp2(block = {
//                println(Thread.currentThread().name+"httpcallback---"+it)
//            })
//            while (true){
//                delay(300L)
//                println(Thread.currentThread().name+"---222")
//            }
            }
            count++
        }

        while (true){
            delay(300L)
            println(Thread.currentThread().name+"---222")
        }
       // var result2 :Int =  test1()
       // delay(1000L)
    }
    Thread.sleep(5000L)
    println(Thread.currentThread().name+"---333")
}



suspend fun test1(): Int{
    println(Thread.currentThread().name+"-----1111")
    //delay(15000L)
    Thread.sleep(15000L)
    println(Thread.currentThread().name+"-----1111--wakeup")
    return 999
}


suspend fun <T> syncHttp2(block: suspend (result:String) -> T): T{
    var httpclient: CloseableHttpClient = HttpClientBuilder.create().build()
    var httpGet : HttpGet = HttpGet("http://localhost:8080/requestGet")
    var response : CloseableHttpResponse
    //response = httpclient.execute(httpGet)
    try {
        response = httpclient.execute(httpGet)

        var entity:HttpEntity = response.entity
        if(entity != null){
            println(EntityUtils.toString(entity))
            return block(EntityUtils.toString(entity))
        }
        // response.close()
    }catch (e:Exception){
        e.printStackTrace()
    }finally {
        try {
            if(httpclient != null){
                httpclient.close()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    return block("error")
}

 suspend fun <T> syncHttp(block: suspend (result:CloseableHttpResponse) -> T): T{
     println(Thread.currentThread().name+"-----http begin")
     var httpclient: CloseableHttpClient = HttpClientBuilder.create().build()
     var httpGet : HttpGet = HttpGet("http://localhost:8080/requestGet")
     var response : CloseableHttpResponse
     response = httpclient.execute(httpGet)
     try {
         response = httpclient.execute(httpGet)
         return block(response)
         /*var entity:HttpEntity = response.entity
         if(entity != null){
             println(EntityUtils.toString(entity))
             return block(EntityUtils.toString(entity))
         }*/
         //response.close()
     }catch (e:Exception){
        e.printStackTrace()
     }finally {
         try {
             if(httpclient != null){
                 httpclient.close()
             }
         }catch (e:Exception){
             e.printStackTrace()
         }
     }
     return block(response)
 }

suspend fun <T> retryIO(block: suspend ()-> T):T{
    var currDelay = 1500L
        println(Thread.currentThread().name+"-----1111")
        //retryIO2(suspend{println(Thread.currentThread().name+"2222callback")})
        Thread.sleep(currDelay)
        //currDelay = (currDelay * 2).coerceAtMost(60000L)
        return block()
}
/*

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
}*/
