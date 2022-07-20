package com.hfad.forecastchangessaver.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.hfad.forecastchangessaver.R
import com.hfad.forecastchangessaver.databinding.FragmentForecastsBinding
import com.hfad.forecastchangessaver.utils.Converters.timestampToDate
import com.hfad.forecastchangessaver.utils.Utilities.toShortString
import com.hfad.forecastchangessaver.viewmodels.ForecastsViewModel
import com.hfad.forecastchangessaver.viewmodels.ForecastsViewModelFactory

class ForecastsFragment : Fragment() {

    private var _binding: FragmentForecastsBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: ForecastsViewModel
    lateinit var viewModelFactory: ForecastsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForecastsBinding.inflate(inflater, container, false)
        val view = binding.root
        val timeStamp = ForecastsFragmentArgs.fromBundle(requireArguments()).timeStamp
        val dateStr = timestampToDate(timeStamp).toShortString()
        binding.headerTitle.text = "${resources.getString(R.string.forecasts_header)} $dateStr"
        viewModelFactory =
            ForecastsViewModelFactory(requireNotNull(activity).application, timeStamp)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ForecastsViewModel::class.java)
        val adapter = ForecastItemAdapter()
        viewModel.data.asLiveData().observe(viewLifecycleOwner) { data ->
            if (data?.isNotEmpty() == true) adapter.data = data
        }
        binding.forecastList.adapter = adapter
        return view
    }
}