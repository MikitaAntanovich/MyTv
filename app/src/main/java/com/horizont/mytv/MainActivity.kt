package com.horizont.mytv

import android.Manifest
import android.content.Intent
import android.cultraview.tv.CtvCommon
import android.cultraview.tv.CtvDataProvider
import android.cultraview.tv.CtvNetwork
import android.cultraview.tv.CtvSystem
import android.cultraview.tv.data.CtvSource
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.horizont.mytv.adapter.AdapterOutputSignal
import com.horizont.mytv.interfaces.IOutputSignalListener
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainActivity : FragmentActivity(), IOutputSignalListener {

    private lateinit var phoneNumber: EditText
    private lateinit var plateNetwork: ImageView
    private lateinit var settingNetwork: Button
    private lateinit var networkName: TextView
    private lateinit var yourPhoneLayout: ConstraintLayout
    private lateinit var smsLayout: ConstraintLayout
    private lateinit var subscription: CheckBox
    private var recyclerViewOutputSignal: RecyclerView? = null

    private lateinit var sourceList: ArrayList<CtvSource>
    private var noNetwork: Boolean = false
    private var enterSmsLayout: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceList = CtvCommon.getInstance().sourceList
        recyclerViewOutputSignal = findViewById(R.id.output_variants)
        recyclerViewOutputSignal?.adapter =
            AdapterOutputSignal(sourceList, this)

        subscription = findViewById(R.id.subscription)
        subscription.setOnClickListener {
            if (subscription.isChecked)
                Log.i("subs", "active")
            else Log.i("subs", "inactive")
            CtvDataProvider.getInstance()
                .putBoolean("customized", "ChSubscriptionSuccess", subscription.isChecked)

        }
        smsLayout = findViewById(R.id.sms_layout)
        yourPhoneLayout = findViewById(R.id.enter_your_phone)
        networkName = findViewById(R.id.network_name)
        networkName.isSelected = true
        networkName.text = NetworkManager.getWifiName(this)

        phoneNumber = findViewById(R.id.identity_number)

        val phoneOk: ImageButton = findViewById(R.id.btn_phone_ok)
        phoneOk.setOnClickListener { checkInternet() }

        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 111
        )

//        val sendSmsAgain: TextView = findViewById(R.id.repeat_sms)
//        sendSmsAgain.setOnClickListener {
//            Toast.makeText(
//                applicationContext,
//                resources.getText(R.string.sms_send_again),
//                Toast.LENGTH_LONG
//            ).show()
//        }

        plateNetwork = findViewById(R.id.internet_connection)
        settingNetwork = findViewById(R.id.settings_network)
        settingNetwork.setOnClickListener { openNetworkSettings() }
        setNetworkStatus(NetworkManager.isNetworkAvailable(this))
        checkNetwork()
    }

    override fun onBackPressed() {
        if (noNetwork) {
            setNoNetwork(false)
        } else if (enterSmsLayout) {
            setSMSLayout(false, true)
        } else
            super.onBackPressed()
    }

    private fun openNetworkSettings() {
        setNoNetwork(false)
        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
    }

    private fun checkNetwork() {
        Handler().postDelayed({
            setNetworkStatus(NetworkManager.isNetworkAvailable(this))
            checkNetwork()
        }, 2000)
    }

    private fun setNetworkStatus(network: Boolean) {
        if (network)
            setNetworkStatus(
                true,
                R.color.network_ok,
                R.drawable.rectangle_plate_network_ok,
                R.string.network_is_connected
            )
        else
            setNetworkStatus(
                false,
                R.color.network_not_found,
                R.drawable.rectangle_plate_network_not_found,
                R.string.network_not_found
            )
    }

    private fun setNetworkStatus(
        connected: Boolean,
        network: Int,
        rectanglePlateNetwork: Int,
        networkIs: Int
    ) {
        val networkStatus: TextView = findViewById(R.id.network_status)
//        val settingsNetwork: TextView = findViewById(R.id.settings_network)

        if (!networkStatus.text.equals(resources.getText(networkIs))) {
//            settingsNetwork.setTextColor(resources.getColor(network, theme))
            plateNetwork.setImageResource(rectanglePlateNetwork)
            networkStatus.text = resources.getText(networkIs)
            if (connected)
                networkName.text = NetworkManager.getWifiName(this)
            else
                networkName.text = ""
        }
    }

    private fun checkInternet() {
        if (NetworkManager.isNetworkAvailable(this)) {
            setNetworkStatus(true)
            if (phoneNumber.text.length == 10) {
                logJson()
                setSMSLayout(true, false)
            } else Toast.makeText(
                applicationContext,
                resources.getText(R.string.enter_correct_number),
                Toast.LENGTH_LONG
            ).show()
        } else {
            setNetworkStatus(false)
            setNoNetwork(true)
        }
    }

    private fun setSMSLayout(sms: Boolean, phone: Boolean) {
        val noSubsLayout: ConstraintLayout = findViewById(R.id.no_subs_layout)
        val checkSms: ImageButton = findViewById(R.id.btn_sms_ok)
        val smsBackup: ImageButton = findViewById(R.id.sms_backup)
        smsBackup.setOnClickListener { setSMSLayout(false, true) }

        if (sms) {
            enterSmsLayout = true
            yourPhoneLayout.animate().apply {
                duration = 500
                y(-resources?.displayMetrics?.heightPixels!!.toFloat())
            }
            smsLayout.y = resources?.displayMetrics?.heightPixels!!.toFloat()
            smsLayout.visibility = VISIBLE
            smsLayout.animate().apply {
                duration = 500
                y(0f)
            }
            checkSms.setOnClickListener { checkSms() }
        } else {
            enterSmsLayout = false
            smsLayout.animate().apply {
                duration = 500
                y(-resources?.displayMetrics?.heightPixels!!.toFloat())
            }
            if (phone) {
                yourPhoneLayout.animate().apply {
                    duration = 500
                    y(0f)
                }
            } else {
                if (subscription.isChecked) {
                    val subsLayout: ConstraintLayout = findViewById(R.id.subs_layout)
                    subsLayout.y = resources?.displayMetrics?.heightPixels!!.toFloat()
                    subsLayout.visibility = VISIBLE
                    subsLayout.animate().apply {
                        duration = 500
                        y(0f)
                    }
                } else {
                    noSubsLayout.y = resources?.displayMetrics?.heightPixels!!.toFloat()
                    noSubsLayout.visibility = VISIBLE
                    noSubsLayout.animate().apply {
                        duration = 500
                        y(0f)
                    }
                }
            }
        }
    }

    private fun checkSms() {
        if (NetworkManager.isNetworkAvailable(this)) {
            val smsCode: TextView = findViewById(R.id.sms_code)
            if (smsCode.text.length == 4) {
                setSmsCode(smsCode.text.toString() == "1234")
            } else Toast.makeText(
                applicationContext,
                resources.getText(R.string.enter_correct_sms),
                Toast.LENGTH_LONG
            ).show()
        } else {
            setNetworkStatus(false)
            setNoNetwork(true)
        }
    }

    private fun setSmsCode(correct: Boolean) {
        if (correct) {
            setSMSLayout(false, false)
        } else {
            Toast.makeText(
                applicationContext,
                resources.getText(R.string.incorrect_sms_code),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setNoNetwork(network: Boolean) {
        val networkLayout: ConstraintLayout = findViewById(R.id.network_not_found_layout)
        val connect: TextView = findViewById(R.id.connect)

        if (network) {
            noNetwork = true
            networkLayout.y = resources?.displayMetrics?.heightPixels!!.toFloat()
            networkLayout.visibility = VISIBLE
            networkLayout.animate().apply {
                duration = 500
                y(0f)
            }
            connect.setOnClickListener { openNetworkSettings() }
        } else {
            noNetwork = false
            networkLayout.animate().apply {
                duration = 500
                y(resources?.displayMetrics?.heightPixels!!.toFloat())
            }
        }
    }

    private fun logJson() {
        val u = User(
            "+7" + phoneNumber.text.toString(),
            CtvNetwork.getInstance().wireMac + "",
            NetworkManager.getMacAddress(this),
            CtvSystem.getInstance().deviceHardwareInfo.toString() + "",
            Build.MODEL,
            Build.ID,
            Build.BRAND,
            Build.TYPE,
            Build.MANUFACTURER,
            Build.BOARD,
            Build.DISPLAY,
            Build.FINGERPRINT
        )
        val jsonAbout = Json.encodeToString(u)
        Log.i("network_ok", jsonAbout)
    }

    fun onBackPressed(view: View) {
        onBackPressed()
    }

    override fun onCLickOutputSignal(numOutput: Int) {
        Log.i("onCLickOutputSignal", numOutput.toString())
    }
}