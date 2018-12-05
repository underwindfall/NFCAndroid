package com.qifan.readnfcmessage.parser;

import android.nfc.NdefRecord;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class TextRecord implements ParsedNdefRecord {

    /**
     * ISO/IANA language code
     */
    private final String mLanguageCode;

    private final String mText;

    public TextRecord(String languageCode, String text) {
        this.mLanguageCode = languageCode;
        this.mText = text;
    }

    // TODO: deal with text fields which span multiple NdefRecords
    public static TextRecord parse(NdefRecord record) {
        if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
            try {
                byte[] payload = record.getPayload();
                /*
                 * payload[0] contains the "Status Byte Encodings" field, per the
                 * NFC Forum "Text Record Type Definition" section 3.2.1.
                 *
                 * bit7 is the Text Encoding Field.
                 *
                 * if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
                 * The text is encoded in UTF16
                 *
                 * Bit_6 is reserved for future use and must be set to zero.
                 *
                 * Bits 5 to 0 are the length of the IANA language code.
                 */
                String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
                int languageCodeLength = payload[0] & 0077;
                String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
                String text =
                        new String(payload, languageCodeLength + 1,
                                payload.length - languageCodeLength - 1, textEncoding);
                return new TextRecord(languageCode, text);
            } catch (UnsupportedEncodingException e) {
                // should never happen unless we get a malformed tag.
                throw new IllegalArgumentException(e);
            }
        } else {
            return null;
        }
    }

    public static boolean isText(NdefRecord record) {
        try {
            parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String str() {
        return mText;
    }

    public String getText() {
        return mText;
    }

    /**
     * Returns the ISO/IANA language code associated with this text element.
     */
    public String getLanguageCode() {
        return mLanguageCode;
    }
}