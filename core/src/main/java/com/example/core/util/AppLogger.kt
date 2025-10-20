package com.example.core.util

import android.util.Log

class AppLogger(private  val tag: String) {

    fun verbose(message: String) {
        Log.v(tag, message)
    }

    fun debug(message: String) {
        Log.d(tag, message)
    }

    fun info(message: String) {
        Log.i(tag, message)
    }

    fun warn(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.w(tag, message, throwable)
        } else {
            Log.w(tag, message)
        }
    }

    fun error(message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

}