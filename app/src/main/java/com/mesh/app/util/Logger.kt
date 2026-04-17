package com.mesh.app.util

import android.util.Log

object Logger {
    private const val TAG = "MeshProtocol"

    fun d(message: String, tr: Throwable? = null) = Log.d(TAG, message, tr)
    fun i(message: String, tr: Throwable? = null) = Log.i(TAG, message, tr)
    fun w(message: String, tr: Throwable? = null) = Log.w(TAG, message, tr)
    fun e(message: String, tr: Throwable? = null) = Log.e(TAG, message, tr)
}
