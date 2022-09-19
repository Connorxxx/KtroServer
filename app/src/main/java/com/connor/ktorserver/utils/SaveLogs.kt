package com.connor.ktorserver.utils

import com.connor.ktorserver.App
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