package com.connor.ktorserver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.connor.ktorserver.databinding.ActivityMainBinding
import com.connor.ktorserver.models.Customer
import com.connor.ktorserver.models.customerStorage
import java.io.File
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

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
        val logDri = File("${this.filesDir}/log")
        if (!logDri.exists()) logDri.mkdir()
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