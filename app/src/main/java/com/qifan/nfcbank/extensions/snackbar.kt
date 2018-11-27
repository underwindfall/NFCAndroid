package com.qifan.nfcbank.extensions

import android.support.design.widget.Snackbar
import android.view.View

/**
 * Created by Qifan on 27/11/2018.
 */

fun longShowSnackBar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}