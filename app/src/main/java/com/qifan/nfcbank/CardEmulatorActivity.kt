package com.qifan.nfcbank

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.qifan.nfcbank.cardEmulation.KHostApduService

/**
 * Created by Qifan on 28/11/2018.
 */
class CardEmulatorActivity : AppCompatActivity() {
    private var mNfcAdapter: NfcAdapter? = null
    private lateinit var button: Button
    private lateinit var editText: EditText
    private lateinit var textView: TextView
    private lateinit var mTurnNfcDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_emulator)

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        button = findViewById<View>(R.id.button) as Button
        editText = findViewById<View>(R.id.editText) as EditText
        textView = findViewById<View>(R.id.textView) as TextView
        initNFCFunction()

    }


    private fun initNFCFunction() {
        if (checkNFCEnable()) {
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            button.visibility = View.VISIBLE
            initService()
        } else {
            textView.visibility = View.VISIBLE
            editText.visibility = View.GONE
            button.visibility = View.GONE
            showTurnOnNfcDialog()
        }
    }

    private fun initService() {
        button.setOnClickListener {
            if (TextUtils.isEmpty(editText.text)) {
                Toast.makeText(this@CardEmulatorActivity, getString(R.string.toast_msg), Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this@CardEmulatorActivity, KHostApduService::class.java)
                intent.putExtra("ndefMessage", editText.text.toString())
                startService(intent)
            }
        }

    }

    private fun checkNFCEnable(): Boolean {
        return if (mNfcAdapter == null) {
            textView.text = getString(R.string.tv_noNfc)
            false
        } else {
            mNfcAdapter!!.isEnabled
        }
    }

    private fun showTurnOnNfcDialog() {
        mTurnNfcDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.ad_nfcTurnOn_title))
            .setMessage(getString(R.string.ad_nfcTurnOn_message))
            .setPositiveButton(
                getString(R.string.ad_nfcTurnOn_pos)
            ) { _, _ ->
                if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
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
        if (mNfcAdapter!!.isEnabled) {
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            button.visibility = View.VISIBLE
            initService()
        }
    }


}