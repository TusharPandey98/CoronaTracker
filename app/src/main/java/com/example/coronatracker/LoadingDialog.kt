package com.example.coronatracker

import android.app.Activity
import android.app.AlertDialog

class LoadingDialog(private var activity: Activity) {
    private lateinit var alertDialog: AlertDialog
    private val builder = AlertDialog.Builder(activity)

    fun startLoadingDialog() {
        val layoutInflater = activity.layoutInflater
        builder.setCancelable(true)
        builder.setView(layoutInflater.inflate(R.layout.custom_loading_dailog, null))
        alertDialog = builder.show()
    }

}