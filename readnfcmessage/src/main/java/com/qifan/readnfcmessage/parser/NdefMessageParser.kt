package com.qifan.readnfcmessage.parser

import android.nfc.NdefMessage
import android.nfc.NdefRecord

/**
 * Created by Qifan on 05/12/2018.
 */

object NdefMessageParser {

    fun parse(message: NdefMessage): List<ParsedNdefRecord> {
        return getRecords(message.records)
    }

    private fun getRecords(records: Array<NdefRecord>): List<ParsedNdefRecord> {
        val elements = ArrayList<ParsedNdefRecord>()

        for (record in records) {
            if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record))
            } else {
                elements.add(object : ParsedNdefRecord {
                    override fun str(): String {
                        return String(record.payload)
                    }
                })
            }
        }

        return elements
    }
}
