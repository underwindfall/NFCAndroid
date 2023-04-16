package com.qifan.readnfcmessage

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.qifan.readnfcmessage.parser.NdefMessageParser

class MainActivity : AppCompatActivity() {
    private var mNfcAdapter: NfcAdapter? = null
    private var mPendingIntent: PendingIntent? = null
    private lateinit var mTvView: TextView

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (checkNFCEnable()) {
            mPendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            mTvView.text = getString(R.string.tv_noNfc)
        }
    }

    override fun onResume() {
        super.onResume()
        mNfcAdapter?.enableForegroundDispatch(this, mPendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                // Process the messages array.
                parserNDEFMessage(messages)
            }
        }
    }

    private fun parserNDEFMessage(messages: List<NdefMessage>) {
        val builder = StringBuilder()
        val records = NdefMessageParser.parse(messages[0])
        val size = records.size

        for (i in 0 until size) {
            val record = records[i]
            val str = record.str()
            builder.append(str).append("\n")
        }
        mTvView.text = builder.toString()
    }

    private fun initView() {
        mTvView = findViewById<View>(R.id.nfc_activity_tv_info) as TextView
    }

    private fun checkNFCEnable(): Boolean {
        return if (mNfcAdapter == null) {
            mTvView.text = getString(R.string.tv_noNfc)
            false
        } else {
            mNfcAdapter?.isEnabled == true
        }
    }
}
