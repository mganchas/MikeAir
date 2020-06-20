package com.example.mikeair.flightResults

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mikeair.R
import com.example.mikeair.databinding.FlightResultsActivityBinding
import com.example.mikeair.utils.ScopeUtils
import com.example.mikeair.utils.ToastUtils
import com.example.model.flights.app.FlightDetails
import com.example.model.flights.app.FlightResult
import com.example.model.flights.app.FlightResults
import kotlinx.coroutines.launch

class FlightResultsActivity : AppCompatActivity() {
    companion object {
        private val TAG = FlightResultsActivity::class.java.simpleName
    }

    private val flightResultsKey: String by lazy {
        getString(R.string.intent_key_flight_results)
    }

    private val flightDetailsDataKey: String by lazy {
        getString(R.string.intent_key_flight_details)
    }

    private lateinit var binding : FlightResultsActivityBinding
    private lateinit var listAdapter: FlightResultsAdapter

    private lateinit var flightResults: FlightResults
    private var priceFilter = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FlightResultsActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        getIncomingData()
        setToolbar()
        setSliderListener()
        fillRecyclerView()
    }

    private fun getIncomingData() {
        Log.d(TAG, "getIncomingData()")
        try {
            flightResults = intent.getSerializableExtra(flightResultsKey) as FlightResults
        } catch (e: Exception) {
            e.printStackTrace()
            onIncomingDataError()
        }
    }

    private fun onIncomingDataError() {
        Log.d(TAG, "onIncomingDataError()")
        ToastUtils.showGenericErrorToast(this)
        finish()
    }

    private fun setToolbar() {
        Log.d(TAG, "setToolbar()")
        binding.toolbar.title = "${flightResults.origin} -> ${flightResults.destination}"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    private fun setSliderListener() {
        Log.d(TAG, "setSliderListener()")

        priceFilter = resources.getInteger(R.integer.default_price_filter).toFloat()
        binding.priceSlider.value = priceFilter
        binding.priceSlider.addOnChangeListener { _, value, fromUser ->
            Log.d(TAG, "setSliderListener().onValueChanged() value: $value | fromUser: $fromUser")
            if (!fromUser) {
                return@addOnChangeListener
            }

            priceFilter = value
            updateFlightsWithPriceFilter()
        }
    }

    private fun fillRecyclerView() {
        Log.d(TAG, "fillRecyclerView()")
        listAdapter = FlightResultsAdapter(this, flightResults.currency) {
            goToDetailsPage(it)
        }
        updateFlights()

        binding.results.run {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(this@FlightResultsActivity)
            setHasFixedSize(true)
        }
    }

    private fun updateFlights() = ScopeUtils.defaultScope().launch {
        Log.d(TAG, "updateFlights()")
        listAdapter.setFlights(flightResults.results)
    }

    private fun updateFlightsWithPriceFilter() = ScopeUtils.defaultScope().launch {
        Log.d(TAG, "updateFlightsWithPriceFilter()")
        listAdapter.setFlights(
            flightResults.results.filter { it.farePrice > priceFilter }
        )
    }

    private fun goToDetailsPage(selectedFlight: FlightResult) {
        Log.d(TAG, "goToDetailsPage() selectedFlight: $selectedFlight")
        val details = FlightDetails(
            origin = flightResults.origin,
            destination = flightResults.destination,
            infantsLeft = selectedFlight.infantsLeft,
            fareClass = selectedFlight.fareClass,
            discountInPercent = selectedFlight.discountInPercent,
            flightNumber = selectedFlight.flightNumber
        )

        val intent = Intent(this, FlightDetailsActivity::class.java).apply {
            putExtra(flightDetailsDataKey, details)
        }
        startActivity(intent)
    }
}