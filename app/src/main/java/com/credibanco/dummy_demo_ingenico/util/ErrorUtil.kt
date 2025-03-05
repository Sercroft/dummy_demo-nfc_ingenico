package com.credibanco.dummy_demo_ingenico.util

import com.usdk.apiservice.aidl.BaseError

object ErrorUtil {

    fun getErrorDetail(error: Int): String {
        val message = getErrorMessage(error)
        if (error < 0) {
            return "$message[$error]"
        }
        return message + String.format("[0x%02X]", error)
    }

    fun getErrorMessage(error: Int): String {
        val message = when (error) {
            BaseError.SERVICE_CRASH -> "SERVICE_CRASH"
            BaseError.REQUEST_EXCEPTION -> "REQUEST_EXCEPTION"
            BaseError.ERROR_CANNOT_EXECUTABLE -> "ERROR_CANNOT_EXECUTABLE"
            BaseError.ERROR_INTERRUPTED -> "ERROR_INTERRUPTED"
            BaseError.ERROR_HANDLE_INVALID -> "ERROR_HANDLE_INVALID"
            else -> "Unknown error"
        }
        return message
    }
}