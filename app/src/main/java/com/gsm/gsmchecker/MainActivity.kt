package com.gsm.gsmchecker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellSignalStrengthGsm
import android.telephony.CellSignalStrengthLte
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val permissionReadPhoneState = Manifest.permission.READ_PHONE_STATE
    private val permissionFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val permissionRequestCode = 1
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // İzinlerin kontrolü yapılır, eğer izin yoksa kullanıcıdan istenir
        if (ContextCompat.checkSelfPermission(this, permissionReadPhoneState) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, permissionFineLocation) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permissionReadPhoneState, permissionFineLocation), permissionRequestCode)
        } else {
            // İzinler verildiyse GSM bağlantı kontrolü yapılır
            startTimer()
        }
    }

    private fun checkGsmConnection() {
        Log.i("GSM", "checkGsmConnection")

        // İzinler kontrol edilir, eğer izin yoksa checkGsmConnection() yöntemi sonlandırılır
        if (ContextCompat.checkSelfPermission(this, permissionFineLocation) != PackageManager.PERMISSION_GRANTED) {
            Log.i("GSM", "checkGsmConnection -1")
            return
        }

        // TelephonyManager kullanarak GSM bağlantı kontrolü yapılır
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val cellInfoList = telephonyManager.allCellInfo

        if (cellInfoList != null) {
            var gsmSignalFound = false
            var signalStrengthDbm = 0
            var typeOfGSM = ""
            for (cellInfo in cellInfoList) {
                Log.i("GSM", cellInfo.toString())
                if (cellInfo is CellInfoLte) {
                    val cellSignalStrengthLte = cellInfo.cellSignalStrength as CellSignalStrengthLte
                    signalStrengthDbm = cellSignalStrengthLte.dbm
                    gsmSignalFound = true
                    typeOfGSM = "LTE"
                    break
                } else if (cellInfo is CellInfoGsm) {
                    val cellSignalStrengthGsm = cellInfo.cellSignalStrength as CellSignalStrengthGsm
                    signalStrengthDbm = cellSignalStrengthGsm.dbm
                    gsmSignalFound = true
                    typeOfGSM = "GSM"
                    break
                }            }
            if (!gsmSignalFound) {
                // GSM sinyali yoksa kullanıcıya uygun bir mesaj gösterilir
                Toast.makeText(this, "GSM signal not found", Toast.LENGTH_SHORT).show()
                return
            }
            Toast.makeText(this, "GSM signal found: $signalStrengthDbm dBm ($typeOfGSM)", Toast.LENGTH_SHORT).show()
        } else {
            // GSM sinyali yoksa kullanıcıya uygun bir mesaj gösterilir
            Toast.makeText(this, "GSM signal not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // İzinler kontrol edilir, verildiyse GSM bağlantı kontrolü yapılır
        if (requestCode == permissionRequestCode && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            startTimer()
        } else {
            // İzin verilmedi
            Toast.makeText(this, "TelephonyManager permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    checkGsmConnection()
                }
            }
        }, 0, 6000)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}