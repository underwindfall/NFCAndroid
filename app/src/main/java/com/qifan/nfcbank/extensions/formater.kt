package com.qifan.nfcbank.extensions

/**
 * Created by Qifan on 27/11/2018.
 */

fun formattedCardNumber(cardNumber: String?): String? {
    val div = " - "
    return cardNumber?.let { card ->
        card.substring(0, 4) + div +
                card.substring(4, 8) + div +
                card.substring(8, 12) + div + card.substring(12, 16)
    }
}