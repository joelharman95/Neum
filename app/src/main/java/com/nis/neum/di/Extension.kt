/*
 * *
 *  * Created by Nethaji on 11/9/21 2:23 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 11/9/21 2:23 PM
 *
 */

package com.nis.neum.di

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.nis.neum.R
import com.nis.neum.data.network.api.response.ResError

fun View.getCompatColor(@ColorRes colorRes: Int): Int =
    ContextCompat.getColor(this.context, colorRes)

infix fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun errorResponseMessage(resError: ResError) = resError.message.toString()

fun ImageView.loadImage(image: Any) {
    Glide.with(context)
        .load(
            when (image) {
                is Int -> ContextCompat.getDrawable(context, image)
                is Bitmap -> image
                else -> image
            }
        )
        .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground))
//        .circleCrop()
        .into(this)
}

fun Activity.showAlert(title: String, message: String) {
    val exitDialog = androidx.appcompat.app.AlertDialog.Builder(this)
    exitDialog.setTitle(title)
    exitDialog.setMessage(message)
    exitDialog.setPositiveButton(
        "OK"
    ) { dialog, which ->
        dialog.dismiss()
        dialog.cancel()
        finish()
    }
    exitDialog.show()
}

fun Activity.blockInput(progressBar: ProgressBar?) {
    progressBar?.visibility = View.VISIBLE
    window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

infix fun Activity.unblockInput(progressBar: ProgressBar?) {
    progressBar?.visibility = View.GONE
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun String.isSuccess(): Boolean = this == "1"

fun String.isFailure(): Boolean = this == "failure"