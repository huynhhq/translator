package com.example.dev00.translator.interfaces

import com.example.dev00.translator.models.GoogleResponse
import com.example.dev00.translator.models.YandexResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IGoogle {

    @POST("/language/translate/v2?")
    fun translate(@Query("key") googleApiKey: String
                  , @Query("q") text: String
                  , @Query("target") target: String): Call<GoogleResponse>
}