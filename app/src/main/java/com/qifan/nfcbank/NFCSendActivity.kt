package com.qifan.nfcbank

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.util.Log
import kotlinx.android.synthetic.main.activity_nfcsend.*
import java.text.SimpleDateFormat
import java.util.*

class NFCSendActivity : AppCompatActivity(),NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {
    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        val text = String.format(
            "Sending A Message From Android Recipes at %s",
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(Date())
        )
        return NdefMessage(
            NdefRecord.createMime(
                "application/com.qifan.nfcbank", text.toByteArray()
            )
            /**
             * The Android Application Record (AAR) is commented out. When a device
             * receives a push with an AAR in it, the application specified in the AAR
             * is guaranteed to run. The AAR overrides the tag dispatch system.
             * You can add it back in to guarantee that this
             * activity starts when receiving a beamed message. For now, this code
             * uses the tag dispatch system.
             */
            //,NdefRecord.createApplicationRecord("com.examples.nfcbeam")
        )
    }

    override fun onNdefPushComplete(event: NfcEvent?) {
        //这个回调是在一个绑定线程上执行的，不用在这个方法中直接更新UI
        Log.i("this", "Message Sent!")
    }

    private var mNfcAdapter: NfcAdapter? = null
    private lateinit var mTurnNfcDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfcsend)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (mNfcAdapter == null) {
            mDisplay.text = "NFC is not available on this device."
        } else {
            // 注册回调来设置 NDEF 消息。这样做可以使Activity处于前台时，
            // NFC 数据推送处于激活状态。
            mNfcAdapter?.setNdefPushMessageCallback(this, this)
            // 注册回调来监听消息发送成功
            mNfcAdapter?.setOnNdefPushCompleteCallback(this, this)
        }
        checkNdefPushStatus()
    }

    private fun checkNdefPushStatus() {
        if (mNfcAdapter==null){
            showTurnOnNfcDialog()
        }else if (!mNfcAdapter!!.isEnabled) {
            startActivity( Intent(Settings.ACTION_NFC_SETTINGS))
        } else if (!mNfcAdapter!!.isNdefPushEnabled) {
            startActivity( Intent(Settings.ACTION_NFCSHARING_SETTINGS))
        }
    }

    private fun showTurnOnNfcDialog() {
        mTurnNfcDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.ad_nfcTurnOn_title))
            .setMessage(getString(R.string.ad_nfcTurnOn_message))
            .setPositiveButton(
                getString(R.string.ad_nfcTurnOn_pos)
            ) { _, _ ->
                if (Build.VERSION.SDK_INT >= 16) {
                    startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                } else {
                    startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                }
            }.setNegativeButton(getString(R.string.ad_nfcTurnOn_neg)) { _, _ ->
                onBackPressed()
            }
            .create()
        mTurnNfcDialog.show()
    }


    override fun onResume() {
        super.onResume()
        // 检查是否是一个Beam启动了这个Activity
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            processIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        //在这之后会调用 onResume 来处理这个Intent
        setIntent(intent)
    }

     fun processIntent(intent: Intent) {
        val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        //  Beam 期间只发送了一条消息
        val msg = rawMsgs[0] as NdefMessage
        // 记录 0 包含了 MIME 类型
        mDisplay.setText(String(msg.records[0].payload))
    }
}
