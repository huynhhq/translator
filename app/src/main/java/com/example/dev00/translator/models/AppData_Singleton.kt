package com.example.dev00.translator.models

class AppData_Singleton private constructor(){
    private var mAppData: AppData? = null

    companion object {
        private var m_instance: AppData_Singleton  = AppData_Singleton()
        @Synchronized
        fun getInstance(): AppData_Singleton {
            return m_instance
        }
    }

    fun setAppData(mAppData: AppData){
        this.mAppData = mAppData
    }

    fun getAppData(): AppData?{
        return this.mAppData
    }
}