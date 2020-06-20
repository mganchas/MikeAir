package com.example.mikeair.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ScopeUtils
{
    companion object
    {
        fun ioScope() = CoroutineScope(Dispatchers.IO)
        fun mainScope() = CoroutineScope(Dispatchers.Main)
        fun defaultScope() = CoroutineScope(Dispatchers.Default)
    }
}