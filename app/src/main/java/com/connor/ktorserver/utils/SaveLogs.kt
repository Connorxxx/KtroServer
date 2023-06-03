package com.connor.ktorserver.utils

import android.util.Log
import com.connor.ktorserver.App
import com.connor.ktorserver.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object SaveLogs {
     fun saveLogs(log: String): File {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = formatter.format(Calendar.getInstance().time)
        val dateLogFile = File("${App.context.filesDir}/log", "$formattedDate-log.txt")
        dateLogFile.appendText(log)
        return dateLogFile
    }
}

fun Any.logCat(tab: String = "KTOR_SERVER_LOG") {

    if (!BuildConfig.DEBUG) return
    if (this is String) Log.d(tab, this) else Log.d(tab, this.toString())
}