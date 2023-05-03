package com.gsm.gsmchecker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val permission = Manifest.permission.READ_PHONE_STATE
    private val permissionRequestCode = 1
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TelephonyManager izni kontrolü
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            // İzin verilmedi, izin isteği yapılıyor
            ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
        } else {
            // İzin verilmiş, GSM bağlantı kontrolü yapılıyor
            checkGsmConnection()
        }
    }

    private fun checkGsmConnection() {
        // TelephonyManager kullanarak GSM bağlantı kontrolü yapılır
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (telephonyManager.simState == TelephonyManager.SIM_STATE_READY) {
            // GSM bağlantısı var
            Toast.makeText(this, "GSM bağlantısı var", Toast.LENGTH_SHORT).show()
        } else {
            // GSM bağlantısı yok
            Toast.makeText(this, "GSM bağlantısı yok", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, GSM bağlantı kontrolü yapılıyor
                checkGsmConnection()

                // Timer'ı başlat
                startTimer()
            } else {
                // İzin verilmedi
                Toast.makeText(this, "TelephonyManager izni verilmedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startTimer() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    checkGsmConnection()
                    Toast.makeText(this@MainActivity, "GSM Connnection Checked", Toast.LENGTH_SHORT).show()
                }
            }
        }, 0, 6000)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Uygulama kapatıldığında Timer'ı durdur
        timer.cancel()
    }
}