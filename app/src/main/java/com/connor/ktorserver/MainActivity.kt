package com.connor.ktorserver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.connor.ktorserver.databinding.ActivityMainBinding
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
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData()
        val intent = Intent(this, KtorService::class.java)
        startService(intent)
        val localIpAddress = getIpAddressInLocalNetwork() ?: "ip null"
        binding.tvIp.text = "$localIpAddress:16610"
    }

    private fun getData(): List<Customer> {
        val customer = Customer("1", "f", "f", "f")
        customerStorage.add(customer)
        return customerStorage
    }

    private fun getIpAddressInLocalNetwork(): String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces().iterator().asSequence()
        val localAddresses = networkInterfaces.flatMap {
            it.inetAddresses.asSequence()
                .filter { inetAddress ->
                    inetAddress.isSiteLocalAddress && !inetAddress.hostAddress.contains(":") &&
                            inetAddress.hostAddress != "127.0.0.1"
                }
                .map { inetAddress -> inetAddress.hostAddress }
        }
        return localAddresses.firstOrNull()
    }
}