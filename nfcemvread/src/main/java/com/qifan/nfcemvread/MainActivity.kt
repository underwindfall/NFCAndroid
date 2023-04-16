package com.qifan.nfcemvread

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils
import com.qifan.nfcemvread.extensions.TextRecord
import com.qifan.nfcemvread.extensions.formattedCardNumber
import com.qifan.nfcemvread.extensions.longShowSnackBar

class MainActivity : AppCompatActivity(), MyCardNfcAsyncTask.MyCardNfcInterface {

    private var mNfcAdapter: NfcAdapter? = null
    private var mCardNfcUtils: CardNfcUtils? = null
    private var mCardNfcAsyncTask: MyCardNfcAsyncTask? = null
    private var mIntentFromCreate: Boolean = false
    private lateinit var mPendingIntent: PendingIntent

    //    private lateinit var mToolbar: Toolbar
    private lateinit var mNFCStatusTextView: TextView
    private lateinit var mCardNumber: TextView
    private lateinit var mCardHolderName: TextView
    private lateinit var mCardExpirationDate: TextView
    private lateinit var mCardType: ImageView
    private lateinit var mTurnNfcDialog: AlertDialog
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mCardContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mNFCStatusTextView = findViewById(R.id.tv_result_nfc)
        mCardNumber = findViewById(R.id.tv_cardNumber)
        mCardHolderName = findViewById(R.id.tv_cardHolderName)
        mCardExpirationDate = findViewById(R.id.tv_cardExpirationDate)
        mCardType = findViewById(R.id.iv_cardType)
        mCardContainer = findViewById(R.id.ll_cardContainer)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        createProgressbar()
        if (checkNFCEnable()) {
            if (Build.VERSION.SDK_INT <= 30) {
                // plugin is deprecated and crashes on android 12> because pending intent isn't set to
                // PendingIntent.FLAG_IMMUTABLE
                // e.g PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                // replace https://github.com/pro100svitlo/Credit-Card-NFC-Reader
                // with
                // https://github.com/devnied/EMV-NFC-Paycard-Enrollment
                mCardNfcUtils = CardNfcUtils(this)
            }
            mIntentFromCreate = true
            readNFCInfo()
        } else {
            showTurnOnNfcDialog()
        }
    }

    private fun createProgressbar() {
        mProgressBar = ProgressBar(this)
        mProgressBar.isIndeterminate = true
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

        // readNFCTextInfo(intent)
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
                        mNFCStatusTextView.text = TextRecord.parse(record).text
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
                getString(R.string.ad_nfcTurnOn_pos),
            ) { _, _ ->
                startActivity(Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
            }.setNegativeButton(getString(R.string.ad_nfcTurnOn_neg)) { _, _ ->
                onBackPressedDispatcher.onBackPressed()
            }
            .create()
        mTurnNfcDialog.show()
    }

    private fun checkNFCEnable(): Boolean {
        return if (mNfcAdapter == null) {
            mNFCStatusTextView.text = getString(R.string.tv_noNfc)
            false
        } else {
            mNfcAdapter?.isEnabled == true
        }
    }

    private fun parseCardType(cardType: String?) {
        when (cardType) {
            MyCardNfcAsyncTask.CARD_UNKNOWN -> longShowSnackBar(
                mNFCStatusTextView,
                getString(R.string.snack_unknown_bank_card),
            )
            MyCardNfcAsyncTask.CARD_VISA -> mCardType.setImageResource(R.drawable.visa_logo)
            MyCardNfcAsyncTask.CARD_MASTER_CARD -> mCardType.setImageResource(R.drawable.master_logo)
        }
    }

    override fun startNfcReadCard() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun cardIsReadyToRead() {
        mNFCStatusTextView.visibility = View.GONE
        mProgressBar.visibility = View.GONE
        mCardContainer.visibility = View.VISIBLE
        val cardNumber = formattedCardNumber(mCardNfcAsyncTask?.cardNumber)
        val expirationDate = mCardNfcAsyncTask?.cardExpireDate
        val holderName =
            mCardNfcAsyncTask?.cardHolderFirstName + mCardNfcAsyncTask?.cardHolderLastName
        val cardType = mCardNfcAsyncTask?.cardType
        mCardNumber.text = cardNumber
        mCardExpirationDate.text = expirationDate
        mCardHolderName.text = holderName
        parseCardType(cardType)
    }

    override fun finishNfcReadCard() {
        mCardNfcAsyncTask = null
    }

    override fun cardWithLockedNfc() {
        longShowSnackBar(mNFCStatusTextView, getString(R.string.snack_lockedNfcCard))
    }

    override fun doNotMoveCardSoFast() {
        longShowSnackBar(mNFCStatusTextView, getString(R.string.snack_doNotMoveCard))
    }

    override fun unknownEmvCard() {
        longShowSnackBar(mNFCStatusTextView, getString(R.string.snack_unknownEmv))
    }
}
