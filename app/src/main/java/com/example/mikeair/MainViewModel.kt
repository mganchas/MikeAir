package com.example.mikeair

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel()
{
    var title = ObservableField<String>()
    var originStation = ObservableField<String>()
    var destStation = ObservableField<String>()
    var departure = ObservableField<String>()
    var adultCount = ObservableField<Int>()
    var teenCount = ObservableField<Int>()
    var childCount = ObservableField<Int>()

    fun loadStations() = CoroutineScope(Dispatchers.IO).launch {

    }
}