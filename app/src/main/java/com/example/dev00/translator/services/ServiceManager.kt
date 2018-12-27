package com.example.dev00.translator.services

import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.interfaces.IYandex

class ServiceManager {
    companion object {

        private final fun getURL(mode: Int): String{
            if(mode.equals(Constants.YANDEX_API)){
                return Constants.YANDEX_URL
            }
            return Constants.GOOGLE_URL
        }

        fun getService(mode: Int): IYandex {
            if(mode.equals(Constants.YANDEX_API)){
                return RetrofitClient.getClient(getURL(mode))!!.create(IYandex::class.java!!)
            }
            return RetrofitClient.getClient(getURL(mode))!!.create(IYandex::class.java!!)
        }
    }
}