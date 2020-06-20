package com.example.mikeair.flightResults

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.mikeair.R
import com.example.mikeair.utils.ToastUtils
import com.example.model.flights.app.FlightDetails
import java.lang.Exception

class FlightDetailsActivity : AppCompatActivity() {
    companion object {
        private val TAG = FlightDetailsActivity::class.java.simpleName
    }

    private val flightDetailsDataKey: String by lazy {
        getString(R.string.intent_key_flight_details)
    }

    private lateinit var toolbar: Toolbar
    private lateinit var originTextView: TextView
    private lateinit var destinationTextView: TextView
    private lateinit var infantsLeftTextView: TextView
    private lateinit var fareClassTextView: TextView
    private lateinit var discountTextView: TextView

    private lateinit var flightDetails: FlightDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flight_details_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        getIncomingData()
        prepareLayout()
        setToolbar()
        fillLayout()
    }

    private fun getIncomingData() {
        Log.d(TAG, "getIncomingData()")
        try {
            flightDetails = intent.getSerializableExtra(flightDetailsDataKey) as FlightDetails
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
        toolbar = findViewById(R.id.toolbar)
        originTextView = findViewById(R.id.origin)
        destinationTextView = findViewById(R.id.destination)
        infantsLeftTextView = findViewById(R.id.infantsLeft)
        fareClassTextView = findViewById(R.id.fareClass)
        discountTextView = findViewById(R.id.discount)
    }

    private fun setToolbar() {
        Log.d(TAG, "setToolbar()")
        toolbar.title = flightDetails.flightNumber
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    private fun fillLayout() {
        Log.d(TAG, "fillLayout()")

        with(flightDetails) {
            originTextView.text = this.origin
            destinationTextView.text = this.destination
            infantsLeftTextView.text = this.infantsLeft.toString()
            fareClassTextView.text = this.fareClass
            discountTextView.text = this.discountInPercent.toString()
        }
    }
}