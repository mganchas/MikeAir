package com.example.mikeair.flightSearch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.api.WebApi
import com.example.mikeair.R
import com.example.mikeair.databinding.FlightSearchActivityBinding
import com.example.mikeair.extensions.toIntOrNull
import com.example.mikeair.flightResults.FlightResultsActivity
import com.example.mikeair.utils.ScopeUtils
import com.example.mikeair.utils.ToastUtils
import com.example.model.airports.api.Station
import com.example.model.airports.app.StationList
import com.example.model.flights.api.*
import com.example.model.flights.app.FlightResult
import com.example.model.flights.app.FlightResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FlightSearchActivity : AppCompatActivity() {
    companion object {
        private val TAG = FlightSearchActivity::class.java.simpleName

        private const val defaultRoundTrip = false
        private const val defaultToUs = "AGREED"
        private const val defaultFlexDays = 3

        private const val bundleKeyOrigin = "bundleKeyOrigin"
        private const val bundleKeyDestination = "bundleKeyDestination"
        private const val bundleKeyDepartureDate = "bundleKeyDepartureDate"
        private const val bundleKeyAdults = "bundleKeyAdults"
        private const val bundleKeyTeens = "bundleKeyTeens"
        private const val bundleKeyChildren = "bundleKeyChildren"
    }

    private val flightResultsKey: String by lazy {
        getString(R.string.intent_key_flight_results)
    }

    private lateinit var binding: FlightSearchActivityBinding

    private var originStation: String? = null
    private var destinationStation: String? = null
    private var departureDate: Date? = null
    private var adults: Int? = null
    private var teens: Int? = null
    private var children: Int? = null

    var stations = listOf<Station>()
    private lateinit var flightApiData: FlightSearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FlightSearchActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }

        setDepartureDateCalendar()
        setSearchButton()
        getStations()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")

        if (stations.isEmpty()) {
            Log.d(TAG, "onResume() stations empty")
            getStations()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "onSaveInstanceState()")

        outState.run {
            putString(bundleKeyOrigin, originStation)
            putString(bundleKeyDestination, destinationStation)
            putSerializable(bundleKeyDepartureDate, departureDate)
            putSerializable(bundleKeyAdults, adults)
            putSerializable(bundleKeyTeens, teens)
            putSerializable(bundleKeyChildren, children)
        }

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState()")

        with(savedInstanceState) {
            originStation = this.getString(bundleKeyOrigin)
            destinationStation = this.getString(bundleKeyDestination)
            departureDate = this.getSerializable(bundleKeyDepartureDate) as Date?
            adults = this.getSerializable(bundleKeyAdults) as Int?
            teens = this.getSerializable(bundleKeyTeens) as Int?
            children = this.getSerializable(bundleKeyChildren) as Int?
        }
    }

    private fun setSearchButton() {
        Log.d(TAG, "setSearchButton()")
        binding.searchBtn.setOnClickListener {
            Log.d(TAG, "setSearchButton().click()")
            fillValuesFromInputs()
            searchFlights()
        }
    }

    private fun setDepartureDateCalendar() {
        Log.d(TAG, "setDepartureDateCalendar()")

        // default value
        departureDate = Date(binding.departureDate.date)

        binding.departureDate.setOnDateChangeListener { _, year, month, day ->
            Log.d(TAG, "setDepartureDateCalendar().onDateChanged()")
            departureDate = Calendar.getInstance().apply {
                set(year, month, day)
            }.time
        }
    }

    private fun getStations() = ScopeUtils.ioScope().launch {
        Log.d(TAG, "getStations()")
        onLoadingStarted()

        WebApi.stationsService
            .getStations()
            .enqueue(object : Callback<StationList> {
                override fun onResponse(call: Call<StationList>, response: Response<StationList>) {
                    Log.d(TAG, "getStations().onResponse()")

                    val responseResult = response.body()?.stations
                    if (responseResult == null) {
                        showGenericError()
                        onLoadingFinished()
                        return
                    }

                    stations = responseResult.sortedBy { it.name }
                    onLoadingFinished()
                    onStationsLoaded()
                }

                override fun onFailure(call: Call<StationList>, t: Throwable) {
                    Log.d(TAG, "getStations().onFailure()")
                    onLoadingFinished()
                    showGenericError()
                }
            })
    }

    private fun onStationsLoaded() {
        Log.d(TAG, "onStationsLoaded()")

        val stationNames = getStationNames()
        val originStationsAdapter = ArrayAdapter(this, R.layout.stations_item, stationNames)
        val destStationsAdapter = ArrayAdapter(this, R.layout.stations_item, stationNames)

        with(binding.originStationAutoComplete) {
            setAdapter(originStationsAdapter)
            setOnItemClickListener { _, _, i, _ -> originStation = stations[i].name }
        }
        with(binding.destStationAutoComplete) {
            setAdapter(destStationsAdapter)
            setOnItemClickListener { _, _, i, _ -> destinationStation = stations[i].name }
        }
    }

    private fun fillValuesFromInputs() {
        Log.d(TAG, "fillValuesFromInputs()")
        adults = binding.adultsCountEdit.toIntOrNull()
        teens = binding.teensCountEdit.toIntOrNull()
        children = binding.childrenCountEdit.toIntOrNull()
    }

    private fun searchFlights() = ScopeUtils.ioScope().launch {
        Log.d(TAG, "searchFlights()")

        if (!isFormValid()) {
            Log.d(TAG, "searchFlights() isFormValid is false")

            withContext(Dispatchers.Main) {
                showInvalidFieldsError()
            }
            return@launch
        }

        onLoadingStarted()

        WebApi.flightsService
            .getFlight(
                origin = getCodeFromStationName(originStation),
                destination = getCodeFromStationName(destinationStation),
                dateOut = departureDate.toString(),
                dateIn = null,
                flexDaysBeforeIn = defaultFlexDays,
                flexDaysBeforeOut = defaultFlexDays,
                flexDaysIn = defaultFlexDays,
                flexDaysOut = defaultFlexDays,
                adultCount = adults!!,
                teenCount = teens!!,
                childrenCount = children!!,
                roundTrip = defaultRoundTrip,
                toUs = defaultToUs
            )
            .enqueue(object : Callback<FlightSearch> {
                override fun onResponse(
                    call: Call<FlightSearch>,
                    response: Response<FlightSearch>
                ) {
                    Log.d(TAG, "searchFlights().onResponse()")

                    val responseResult = response.body()
                    if (responseResult == null) {
                        onLoadingFinished()
                        showGenericError()
                        return
                    }

                    flightApiData = responseResult
                    handleSearchResult()
                }

                override fun onFailure(call: Call<FlightSearch>, t: Throwable) {
                    Log.d(TAG, "searchFlights().onFailure()")
                    onLoadingFinished()
                    showGenericError()
                }
            })
    }

    private fun handleSearchResult() = ScopeUtils.mainScope().launch {
        Log.d(TAG, "handleSearchResult()")

        try {
            val flightResults = withContext(Dispatchers.Default) {
                transformApiResponseIntoResults()
            }
            goToResultsPage(flightResults)
        }
        catch (e: Exception) {
            e.printStackTrace()
            showGenericError()
        }
        finally {
            onLoadingFinished()
        }
    }

    private fun transformApiResponseIntoResults() : FlightResults {
        Log.d(TAG, "transformApiResponseIntoResults()")

        val results = mutableListOf<FlightResult>()
        flightApiData.trips?.forEach { trip ->
            trip.dates?.forEach { flightDate ->
                flightDate.flights.forEach {flight ->
                    results.add(
                        FlightResult(
                            flightDate = flight.time!![0],
                            flightNumber = flight.flightNumber!!,
                            duration = flight.duration!!,
                            fareClass = flight.regularFare!!.fareClass!!,
                            infantsLeft = flight.infantsLeft!!,
                            farePrice = flight.regularFare!!.fares!![0].amount!!,
                            discountInPercent = flight.regularFare!!.fares!![0].discountInPercent!!
                        )
                    )
                }
            }
        }

        return FlightResults(
            origin = originStation!!,
            destination = destinationStation!!,
            currency = flightApiData.currency!!,
            results = results
        )
    }

    private fun isFormValid(): Boolean {
        Log.d(TAG, "isFormValid()")

        if (!isOriginStationValid()) {
            Log.d(TAG, "isFormValid() isOriginStationValid: false")
            return false
        }

        if (!isDestStationValid()) {
            Log.d(TAG, "isFormValid() isDestStationValid: false")
            return false
        }

        if (!isDepartureDateValid()) {
            Log.d(TAG, "isFormValid() isDepartureDateValid: false")
            return false
        }

        if (!arePassengersValid()) {
            Log.d(TAG, "isFormValid() arePassengersValid: false")
            return false
        }

        return true
    }

    private fun isOriginStationValid() = originStation != null

    private fun isDestStationValid() = destinationStation != null

    private fun isDepartureDateValid() = departureDate?.after(Calendar.getInstance().time) == true

    private fun arePassengersValid(): Boolean {
        Log.d(TAG, "arePassengersValid()")

        if (adults == null || teens == null || children == null) {
            Log.d(TAG, "arePassengersValid() " +
                        "adults == null? ${adults == null} | " +
                        "teens == null? ${teens == null} | " +
                        "children == null? ${children == null}"
            )

            return false
        }

        if (adults!! > 0) {
            Log.d(TAG, "arePassengersValid() adults is valid")
            return true
        }

        if (teens!! > 0) {
            Log.d(TAG, "arePassengersValid() teens is valid")
            return true
        }

        if (children!! > 0) {
            Log.d(TAG, "arePassengersValid() children is valid")
            return true
        }

        return false
    }

    private fun goToResultsPage(flightResults : FlightResults) {
        Log.d(TAG, "goToResultsPage()")
        val intent = Intent(this, FlightResultsActivity::class.java).apply {
            putExtra(flightResultsKey, flightResults)
        }
        startActivity(intent)
    }

    private fun getCodeFromStationName(name: String?): String? {
        Log.d(TAG, "getCodeFromStationName()")

        if (name.isNullOrEmpty()) {
            return null
        }
        return stations.firstOrNull { it.name == name }?.code
    }

    private fun getStationNames() = stations.map { it.name }

    private fun showGenericError() {
        Log.d(TAG, "showGenericError()")
        ToastUtils.showGenericErrorToast(this@FlightSearchActivity)
    }

    private fun showInvalidFieldsError() {
        Log.d(TAG, "showInvalidFieldsError()")
        ToastUtils.showToast(this@FlightSearchActivity, R.string.toast_invalid_fields_error)
    }

    private fun onLoadingStarted() = ScopeUtils.mainScope().launch {
        Log.d(TAG, "onLoadingStarted()")
        disableForm()
        showLoading()
    }

    private fun onLoadingFinished() = ScopeUtils.mainScope().launch {
        Log.d(TAG, "onLoadingFinished()")
        enableForm()
        hideLoading()
    }

    private fun enableForm() {
        Log.d(TAG, "enableForm()")
        binding.parentLayout.isEnabled = true
    }

    private fun disableForm() {
        Log.d(TAG, "disableForm()")
        binding.parentLayout.isEnabled = false
    }

    private fun showLoading() {
        Log.d(TAG, "showLoading()")
        binding.loadingLayout.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        Log.d(TAG, "hideLoading()")
        binding.loadingLayout.visibility = View.GONE
    }
}