package com.example.mikeair.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.toUsFormat() : String? = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(this)