package com.maikson.exportxpress.interfaces

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LOGINInterface {
    @Multipart
    @POST("loginserver.php")
    fun userLogin(
            @Part("userid") userid: RequestBody ,
            @Part("cnpjid") cnpjid: RequestBody ,
            @Part("password") password: RequestBody
    ): Call<String>

    companion object {
        val IMAGEURL = "http://azure.infordoc.com/Maikson/Applications/mobile/"
    }
}