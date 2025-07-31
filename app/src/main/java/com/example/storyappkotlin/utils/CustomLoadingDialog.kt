package com.example.storyappkotlin.utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.example.storyappkotlin.R

class CustomLoadingDialog(context: Context) : Dialog(context) {

    init {
        val window = requireNotNull(window);
        val params: WindowManager.LayoutParams = window.attributes;

        params.gravity = Gravity.CENTER_HORIZONTAL;
        window.attributes = params;

        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);

        val view = LayoutInflater.from(context).inflate(R.layout.custom_loading_indicator, null);
        setContentView(view);
    }
}