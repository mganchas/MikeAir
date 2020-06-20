package com.example.mikeair.flightResults

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mikeair.R
import com.example.mikeair.utils.ScopeUtils
import com.example.model.flights.app.FlightResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FlightResultsAdapter(
    context: Context,
    private val currency : String,
    private val onItemClick : (FlightResult) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var flights = listOf<FlightResult>()

    override fun getItemCount() = flights.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        val itemView = inflater.inflate(R.layout.flight_result_item, parent, false)
        return FlightViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        val currentFlight = flights[position]
        (holder as FlightViewHolder).bind(currentFlight, onItemClick)
    }

    fun setFlights(flightList: List<FlightResult>) = ScopeUtils.defaultScope().launch {
        synchronized(this) {
            flights = flightList
        }
        withContext(Dispatchers.Main) {
            notifyDataSetChanged()
        }
    }

    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val flightDate : TextView = itemView.findViewById(R.id.date)
        private val flightNumber : TextView = itemView.findViewById(R.id.flightNumber)
        private val duration : TextView = itemView.findViewById(R.id.duration)
        private val price : TextView = itemView.findViewById(R.id.price)

        fun bind(flight: FlightResult, onItemClick: (FlightResult) -> Unit)
        {
            itemView.run {
                flightDate.text = flight.flightDate.toString()
                flightNumber.text = flight.flightNumber
                duration.text = flight.duration
                price.text = "${flight.farePrice} $currency"

                setOnClickListener {
                    onItemClick(flight)
                }
            }
        }
    }
}