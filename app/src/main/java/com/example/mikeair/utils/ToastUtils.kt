package com.example.mikeair.utils

import android.content.Context
import android.widget.Toast
import com.example.mikeair.R

class ToastUtils
{
    companion object
    {
        fun showToast(context: Context, text : String)
        {
            with(context) {
                Toast.makeText(
                    this,
                    text,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        fun showToast(context: Context, resource : Int) {
            showToast(context, context.resources.getString(resource))
        }

        fun showGenericErrorToast(context: Context) {
            showToast(context, R.string.toast_generic_error)
        }
    }
}