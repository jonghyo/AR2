package net.kboy.sceneformsample.activity.util

import kotlinx.coroutines.experimental.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody



class HttpUtil {
    //叩きたいREST APIのURLを引数とします
    fun httpGET(url : String): Deferred<String?> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT, null, {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url(url)
                .build()

        val response = client.newCall(request).execute()
        return@async response.body()?.string() //asyncを使って非同期処理にします
    })

    fun httpPOST(url : String, body: String): Deferred<String?> = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT, null, {
        val client = OkHttpClient()
        val MIMEType = MediaType.parse("application/json; charset=utf-8")
        val requestBody = RequestBody.create(MIMEType, body)
        val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

        val response = client.newCall(request).execute()
        return@async response.body()?.string() //asyncを使って非同期処理にします
    })
}
