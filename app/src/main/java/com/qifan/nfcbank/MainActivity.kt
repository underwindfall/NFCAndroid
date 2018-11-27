package com.qifan.nfcbank

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.TextView
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), MyCardNfcAsyncTask.MyCardNfcInterface {

    private var mNfcAdapter: NfcAdapter? = null
    private var mCardNfcUtils: CardNfcUtils? = null
    private var mCardNfcAsyncTask: MyCardNfcAsyncTask? = null
    private var mIntentFromCreate: Boolean = false
    private lateinit var mPendingIntent: PendingIntent

    //    private lateinit var mToolbar: Toolbar
    private lateinit var mTextView: TextView
    private lateinit var mTurnNfcDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        mToolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(mToolbar)
        mTextView = findViewById(R.id.tv_result_nfc)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
//        mPendingIntent = PendingIntent.getActivity(
//            this, 0,
//            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
//        )
        if (checkNFCEnable()) {
            mCardNfcUtils = CardNfcUtils(this)
            mIntentFromCreate = true
            readNFCInfo()
        } else {
            showTurnOnNfcDialog()
        }

    }


    override fun onStart() {
        super.onStart()
//        mPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, javaClass), 0)
    }

    override fun onResume() {
        super.onResume()
        mIntentFromCreate = false
        if (mNfcAdapter != null) {
            mCardNfcUtils?.enableDispatch()
        }
//        mNfcAdapter?.enableForegroundDispatch(this, mPendingIntent, null, null)
    }


    override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) {
            mCardNfcUtils?.disableDispatch()
        }
    }


    private fun readNFCInfo() {
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        //readNFCTextInfo(intent)
        if (mNfcAdapter != null && checkNFCEnable()) {
            mCardNfcAsyncTask = MyCardNfcAsyncTask.Builder(this, intent, mIntentFromCreate).build()
        }

    }


    /**
     * this function is called to parse intent message to normal string
     * @param intent android intent conclude NdefMessage
     */
    private fun readNFCTextInfo(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
                val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
                // Process the messages array.
                messages.forEach { message ->
                    message.records.forEach { record ->
                        mTextView.text = TextRecord.parse(record).text
                    }
                }
            }
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


    private fun checkNFCEnable(): Boolean {
        return if (mNfcAdapter == null) {
            false
        } else {
            mNfcAdapter!!.isEnabled
        }
    }


    override fun startNfcReadCard() {

    }

    override fun cardIsReadyToRead() {
        Log.d(
            "NFCBANK",
            "cardNumber is ===============" + mCardNfcAsyncTask?.cardNumber +
                    "expirationDate" + mCardNfcAsyncTask?.cardExpireDate
        )
        mTextView.text = mCardNfcAsyncTask?.cardNumber
    }

    override fun finishNfcReadCard() {
        mCardNfcAsyncTask = null
    }

    override fun cardWithLockedNfc() {

    }

    override fun doNotMoveCardSoFast() {

    }

    override fun unknownEmvCard() {

    }

}
