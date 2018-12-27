package com.example.dev00.translator.interfaces

import com.example.dev00.translator.models.YandexResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IYandex {

    @GET("/api/v1.5/tr.json/translate?")
    fun translate(@Query("key") yandexKey: String
                  , @Query("text") text: String
                  , @Query("lang") languagePair: String): Call<YandexResult>

}