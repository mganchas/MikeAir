package com.example.mikeair.extensions

import com.google.android.material.textfield.TextInputEditText

fun TextInputEditText.toIntOrNull() : Int?
{
    if (this.text.toString().isEmpty()) {
        return null
    }

    return this.text.toString().toInt()
}