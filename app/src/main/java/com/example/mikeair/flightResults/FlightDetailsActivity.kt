package com.example.mikeair.flightResults

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mikeair.R
import com.example.mikeair.databinding.FlightDetailsActivityBinding
import com.example.mikeair.utils.ToastUtils
import com.example.model.flights.app.FlightDetails

class FlightDetailsActivity : AppCompatActivity() {
    companion object {
        private val TAG = FlightDetailsActivity::class.java.simpleName
    }

    private val flightDetailsDataKey: String by lazy {
        getString(R.string.intent_key_flight_details)
    }

    private lateinit var binding : FlightDetailsActivityBinding
    private lateinit var flightDetails: FlightDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FlightDetailsActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        getIncomingData()
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

    private fun setToolbar() {
        Log.d(TAG, "setToolbar()")
        binding.toolbar.title = flightDetails.flightNumber
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    private fun fillLayout() {
        Log.d(TAG, "fillLayout()")

        with(flightDetails) {
            binding.origin.text = this.origin
            binding.destination.text = this.destination
            binding.infantsLeft.text = this.infantsLeft.toString()
            binding.fareClass.text = this.fareClass
            binding.discount.text = this.discountInPercent.toString()
        }
    }
}