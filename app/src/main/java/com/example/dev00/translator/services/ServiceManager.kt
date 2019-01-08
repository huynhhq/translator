package com.example.dev00.translator.services

import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.helpers.Credentials
import com.example.dev00.translator.interfaces.IGoogle
import com.example.dev00.translator.interfaces.IYandex

class ServiceManager {
    companion object {

        private fun getURL(mode: Int): String{
            if(mode.equals(Constants.YANDEX_API)){
                return Credentials.BASE_ADDRESS_YANDEX
            }
            return Credentials.BASE_ADDRESS_GOOGLE
        }

        fun getService(mode: Int?): Any {
            if(mode!!.equals(Constants.YANDEX_API)){
                return RetrofitClient.getClient(getURL(mode))!!.create(IYandex::class.java)
            }
            return RetrofitClient.getClient(getURL(mode))!!.create(IGoogle::class.java)
        }
    }
}