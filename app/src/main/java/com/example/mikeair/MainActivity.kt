package com.example.mikeair

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()
{
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar))

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setViewModelDefaults()
        viewModel.loadStations()

        findViewById<Button>(R.id.searchBtn).setOnClickListener {
            Dialog(this).apply {
                setContentView(R.layout.loading_animation)
                window?.setBackgroundDrawableResource(android.R.color.transparent);
                show()
            }
        }
    }

    private fun setViewModelDefaults() {
        viewModel.title.set(getString(R.string.app_name))
    }

    private fun initViewModel() = CoroutineScope(Dispatchers.Main).launch {

    }
}