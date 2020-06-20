package com.example.mikeair.flightResults

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mikeair.R
import com.example.mikeair.utils.ToastUtils
import com.example.model.flights.api.Flight
import com.example.model.flights.app.FlightDetails
import com.example.model.flights.api.FlightSearch
import com.example.model.flights.app.FlightResult
import com.example.model.flights.app.FlightResults
import java.lang.Exception

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

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

    private lateinit var listAdapter: FlightResultsAdapter

    private lateinit var flightResults: FlightResults

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flight_results_activity)

        getIncomingData()
        prepareLayout()
        setToolbar()
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

    private fun prepareLayout() {
        Log.d(TAG, "prepareLayout()")
        recyclerView = findViewById(R.id.results)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setToolbar() {
        Log.d(TAG, "setToolbar()")
        toolbar.title = "${flightResults.origin} -> ${flightResults.destination}"
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    private fun fillRecyclerView() {
        Log.d(TAG, "fillRecyclerView()")
        listAdapter = FlightResultsAdapter(this, flightResults.currency) {
            goToDetailsPage(it)
        }
        listAdapter.setFlights(flightResults.results)

        recyclerView.run {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(this@FlightResultsActivity)
            setHasFixedSize(true)
        }
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