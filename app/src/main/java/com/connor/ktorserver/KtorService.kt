package com.connor.ktorserver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.connor.ktorserver.plugins.configureRouting
import com.connor.ktorserver.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.*
import okio.buffer
import okio.sink
import okio.source
import java.io.File

class KtorService : Service() {

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    private val configServer by lazy {
        embeddedServer(Netty, port = 16610, host = "0.0.0.0", configure = {
            connectionGroupSize = 2
            workerGroupSize = 5
            callGroupSize = 10

        }) {

            configureRouting(assetsFile())
            configureSerialization()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ktor_server", "Ktor Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, 0)
        val notification = NotificationCompat.Builder(this, "ktor_server")
            .setContentTitle("Ktor server is running")
            .setContentText("You could disable it notification")
            .setContentIntent(pi)
            .build()
        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ioScope.launch {
            configServer.start(wait = true)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onDestroy() {
        ioScope.launch {
            configServer.stop(1_000, 2_000)
            job.cancelAndJoin()
        }
        stopForeground(true)
        super.onDestroy()
    }

    private fun assetsFile(): File {
        val fileName = "upload_file.jpg"
        val inputStream = resources.assets.open(fileName)
        val file = File(this.filesDir.path, fileName)
        if (!file.exists()) {
            inputStream.source().buffer().use {
                it.readAll(file.sink())
            }
        }
        return file
    }
}