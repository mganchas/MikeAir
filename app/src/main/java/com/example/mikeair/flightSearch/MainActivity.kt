package com.example.mikeair.flightSearch

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.mikeair.R
import com.example.mikeair.databinding.MainActivityBinding

class MainActivity : AppCompatActivity()
{
    private val viewModel : MainViewModel by viewModels()

    private lateinit var originStation : AutoCompleteTextView
    private lateinit var destStation : AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))

        val binding : MainActivityBinding = DataBindingUtil.setContentView(this,
            R.layout.main_activity
        )
        binding.lifecycleOwner = this
        binding.vm = viewModel

        originStation = findViewById(R.id.originStationAutoComplete)
        destStation = findViewById(R.id.destStationAutoComplete)

        setObservers()
        viewModel.getStations()
    }

    private fun setObservers() {
        observeGenericError()
        observeMissingFieldsError()
        observeOnStationsLoaded()
    }

    private fun observeGenericError()
    {
        viewModel.showGenericError.observe(this, Observer {
            it ?: return@Observer
            Toast.makeText(this, getString(R.string.toast_generic_error), Toast.LENGTH_LONG).show()
            viewModel.clearGenericErrorEvent()
        })
    }

    private fun observeMissingFieldsError()
    {
        viewModel.showMissingFieldsError.observe(this, Observer {
            it ?: return@Observer
            Toast.makeText(this, getString(R.string.toast_missing_fields_error), Toast.LENGTH_LONG).show()
            viewModel.clearMissingFieldsErrorEvent()
        })
    }

    private fun observeOnStationsLoaded()
    {
        viewModel.updateStations.observe(this, Observer {
            it ?: return@Observer

            val stationNames = viewModel.getStationNames()
            val originStationsAdapter = ArrayAdapter(this, R.layout.stations_item, stationNames)
            val destStationsAdapter = ArrayAdapter(this, R.layout.stations_item, stationNames)

            with(originStation) {
                setAdapter(originStationsAdapter)
                setOnItemClickListener { _, _, i, _ -> viewModel.setSelectedOriginStation(i) }
            }
            with(destStation) {
                setAdapter(destStationsAdapter)
                setOnItemClickListener { _, _, i, _ -> viewModel.setSelectedDestStation(i) }
            }

            viewModel.clearUpdateStationsEvent()
        })
    }
}