package com.qifan.nfcbank

import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        if (supportNfcHceFeature()) {
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            button.visibility = View.VISIBLE
            initService()
        } else {
            textView.visibility = View.VISIBLE
            editText.visibility = View.GONE
            button.visibility = View.GONE
            // Prevent phone that doesn't support NFC to trigger dialog
            if (supportNfcHceFeature()) {
                showTurnOnNfcDialog()
            }
        }
    }

    private fun supportNfcHceFeature() =
        checkNFCEnable() && packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)

    private fun initService() {
        button.setOnClickListener {
            if (TextUtils.isEmpty(editText.text)) {
                Toast.makeText(
                    this@CardEmulatorActivity,
                    getString(R.string.toast_msg),
                    Toast.LENGTH_LONG,
                ).show()
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
            mNfcAdapter?.isEnabled == true
        }
    }

    private fun showTurnOnNfcDialog() {
        mTurnNfcDialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_rounded)
            .setTitle(getString(R.string.ad_nfcTurnOn_title))
            .setMessage(getString(R.string.ad_nfcTurnOn_message))
            .setPositiveButton(
                getString(R.string.ad_nfcTurnOn_pos),
            ) { _, _ ->
                startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
            }.setNegativeButton(getString(R.string.ad_nfcTurnOn_neg)) { _, _ ->
                onBackPressedDispatcher.onBackPressed()
            }
            .create()
        mTurnNfcDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (mNfcAdapter?.isEnabled == true) {
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            button.visibility = View.VISIBLE
            initService()
        }
    }
}
