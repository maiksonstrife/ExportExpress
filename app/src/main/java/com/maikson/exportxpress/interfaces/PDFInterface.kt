package com.maikson.exportxpress.interfaces

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PDFInterface {
    @Multipart
    @POST("uploadfile.php")
    fun uploadImage(
            @Part file: MultipartBody.Part,
            @Part("filename") name: RequestBody ,
            @Part("data") data: RequestBody ,
            @Part("geoname") geoname: RequestBody ,
            @Part("cpf") cpf: RequestBody ,
            @Part("cnpj") cnpj: RequestBody
    ): Call<String>

    companion object {
        val IMAGEURL = "http://azure.infordoc.com/Maikson/Applications/mobile/"
    }
}