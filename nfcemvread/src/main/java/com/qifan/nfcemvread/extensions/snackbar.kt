package com.qifan.nfcemvread.extensions

import com.google.android.material.snackbar.Snackbar
import android.view.View

/**
 * Created by Qifan on 05/12/2018.
 */

fun longShowSnackBar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}