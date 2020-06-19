package com.example.mikeair.flightSearch

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.api.WebApi
import com.example.model.airports.Station
import com.example.model.airports.StationList
import com.example.model.flights.FlightSearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainViewModel : ViewModel() {
    companion object {
        private val TAG = MainViewModel::class.java.simpleName

        private const val defaultRoundTrip = false
        private const val defaultToUs = "AGREED"
        private const val defaultFlexDays = 3
    }

    var originStation = ObservableField<String>()
    var destStation = ObservableField<String>()

    var departureDay = ObservableInt()
    var departureMonth = ObservableInt()
    var departureYear = ObservableInt()
    var adults = ObservableInt()
    var teens = ObservableInt()
    var children = ObservableInt()

    var isLoading = ObservableBoolean(false)

    var stations = listOf<Station>()
        private set

    var updateStations = MutableLiveData<Boolean?>()
        private set

    var showGenericError = MutableLiveData<Boolean?>()
        private set

    var showMissingFieldsError = MutableLiveData<Boolean?>()
        private set

    fun getStations() = CoroutineScope(Dispatchers.IO).launch {
        isLoading.set(true)

        WebApi.stationsService
            .getStations()
            .enqueue(object : Callback<StationList>
            {
                override fun onResponse(call: Call<StationList>, response: Response<StationList>) {
                    val responseResult = response.body()?.stations
                    if (responseResult == null) {
                        showGenericError.value = true
                        isLoading.set(false)
                        return
                    }

                    stations = responseResult
                    isLoading.set(false)
                    updateStations.value = true
                }

                override fun onFailure(call: Call<StationList>, t: Throwable) {
                    isLoading.set(false)
                    showGenericError.value = true
                }
            })
    }

    fun getDepartureDateDay() = departureDay.get()
    fun setDepartureDateDay(newValue : Int) = departureDay.set(newValue)

    fun getDepartureDateMonth() = departureMonth.get()
    fun setDepartureDateMonth(newValue : Int) = departureMonth.set(newValue)

    fun getDepartureDateYear() = departureYear.get()
    fun setDepartureDateYear(newValue : Int) = departureYear.set(newValue)

    fun getAdultCount() = adults.get()
    fun setAdultCount(newValue : Int) = adults.set(newValue)

    fun getTeenCount() = teens.get()
    fun setTeenCount(newValue : Int) = teens.set(newValue)

    fun getChildCount() = children.get()
    fun setChildCount(newValue : Int) = children.set(newValue)

    fun setSelectedOriginStation(position : Int) {
        originStation.set(stations[position].name)
    }

    fun setSelectedDestStation(position : Int) {
        destStation.set(stations[position].name)
    }

    fun getStationNames() = stations.map { it.name }

    fun clearGenericErrorEvent() {
        showGenericError.value = null
    }

    fun clearMissingFieldsErrorEvent() {
        showMissingFieldsError.value = null
    }

    fun clearUpdateStationsEvent() {
        updateStations.value = null
    }

    fun searchFlights() = CoroutineScope(Dispatchers.IO).launch {
        if (!isFormValid())
        {
            withContext(Dispatchers.Main) {
                showMissingFieldsError.value = true
            }
            return@launch
        }

        isLoading.set(true)
        WebApi.flightsService
            .getFlight(
                origin = getCodeFromStationName(originStation.get()),
                destination = getCodeFromStationName(destStation.get()),
                dateOut = getFormattedDate(),
                dateIn = null,
                flexDaysBeforeIn = defaultFlexDays,
                flexDaysBeforeOut = defaultFlexDays,
                flexDaysIn = defaultFlexDays,
                flexDaysOut = defaultFlexDays,
                adultCount = adults.get(),
                teenCount = teens.get(),
                childrenCount = children.get(),
                roundTrip = defaultRoundTrip,
                toUs = defaultToUs
            )
            .enqueue(object : Callback<FlightSearch>
            {
                override fun onResponse(call: Call<FlightSearch>, response: Response<FlightSearch>) {
                    val responseResult = response.body()
                    if (responseResult == null) {
                        showGenericError.value = true
                        isLoading.set(false)
                        return
                    }

                    //stations = responseResult
                    isLoading.set(false)
                    //updateStations.value = true
                }

                override fun onFailure(call: Call<FlightSearch>, t: Throwable) {
                    isLoading.set(false)
                    showGenericError.value = true
                }
            })
    }

    private fun getFormattedDate() =
        Calendar.getInstance().apply {
            set(departureYear.get(), departureMonth.get(), departureDay.get())
        }
        .toString()

    private fun getCodeFromStationName(name : String?) : String? {
        if (name.isNullOrEmpty()) {
            return null
        }
        return stations.firstOrNull { it.name == name }?.code
    }

    private fun isFormValid() =
        originStation.get() != null &&
        destStation.get() != null &&
        departureDay.get() > 0 &&
        departureMonth.get() > 0 &&
        departureYear.get() > 0 &&
        (adults.get() > 0 || teens.get() > 0 || children.get() > 0)
}