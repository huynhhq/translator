package com.example.dev00.translator.interfaces

interface ConversionCallback {
    fun onSuccess(result: String)

    fun onCompletion()

    fun onErrorOccured(errorMessage: String)
}