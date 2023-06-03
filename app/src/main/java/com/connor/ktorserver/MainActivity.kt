package com.connor.ktorserver

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.connor.ktorserver.databinding.ActivityMainBinding
import com.connor.ktorserver.models.Customer
import com.connor.ktorserver.models.customerStorage
import java.io.File
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
            Log.d(TAG, "onCreate: ")
        } else {
            Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).also {
                startActivity(it)
            }
        }
        getData()
        val intent = Intent(this, KtorService::class.java)
        startService(intent)
        val localIpAddress = getIpAddressInLocalNetwork() ?: "ip null"
        binding.tvIp.text = "$localIpAddress:16610"
        val logDri = File("${this.filesDir}/log")
        if (!logDri.exists()) logDri.mkdir()
        binding.btnStop.setOnClickListener {
            val stopService = Intent(this, KtorService::class.java)
            stopService(stopService)
        }
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