package com.hfad.forecastchangessaver.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.forecastchangessaver.db.ForecastEntity
import com.hfad.forecastchangessaver.databinding.ForecastItemBinding
import com.hfad.forecastchangessaver.utils.Utilities.toShortString

class ForecastItemAdapter : RecyclerView.Adapter<ForecastItemAdapter.ForecastItemViewHolder>() {

    var data = listOf<ForecastEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var _binding: ForecastItemBinding? = null
    private val binding get() = _binding!!

    override fun getItemCount(): Int = data.size

    class ForecastItemViewHolder(binding: ForecastItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastItemViewHolder {
        _binding =
            ForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastItemViewHolder, position: Int) {
        binding.forecastDate.text = data[position].forecastDate.toShortString()
        binding.forecastPrecipitation.text = data[position].precipitation
        binding.forecastTMax.text = "t\u2098\u2090\u2093 ${data[position].tMax}"
        binding.forecastTMin.text = "t\u2098\u1d62\u2099 ${data[position].tMin}"
    }

}
