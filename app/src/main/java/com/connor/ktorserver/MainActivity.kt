package com.connor.ktorserver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.connor.ktorserver.models.Customer
import com.connor.ktorserver.models.customerStorage
import com.connor.ktorserver.plugins.configureRouting
import com.connor.ktorserver.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.buffer
import okio.sink
import okio.source
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getData()
        lifecycleScope.launch(Dispatchers.IO) {
            configServer()
        }
    }

    private fun configServer() {
        embeddedServer(Netty, port = 16610, host = "0.0.0.0") {
            configureRouting(assetsFile())
            configureSerialization()
        }.start(wait = true)
    }

    private fun assetsFile(): File {
        val fileName = "upload_file.jpg"
        val inputStream = resources.assets.open(fileName)
        val file = File(this.filesDir.path, fileName)
        inputStream.source().buffer().use {
            it.readAll(file.sink())
        }
        return file
    }

    private fun getData(): List<Customer> {
        val customer = Customer("1", "f", "f", "f")
        customerStorage.add(customer)
        return customerStorage
    }
}