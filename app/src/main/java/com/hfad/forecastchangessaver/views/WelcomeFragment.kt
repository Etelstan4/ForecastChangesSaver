package com.hfad.forecastchangessaver.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.datepicker.*
import com.hfad.forecastchangessaver.R
import com.hfad.forecastchangessaver.databinding.FragmentWelcomeBinding
import com.hfad.forecastchangessaver.db.WeatherDataBase
import com.hfad.forecastchangessaver.services.WeatherParser.parseAndSave
import com.hfad.forecastchangessaver.services.WeatherUpdateWorker
import com.hfad.forecastchangessaver.utils.Utilities.toMillisUTC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        val view = binding.root
        lifecycleScope.launch(Dispatchers.IO) {
            checkPreferences(view.context)
            val dao = WeatherDataBase.getInstance(view.context).dateDao()
            val firstDate = dao.getFirstDate().toMillisUTC() ?: 0L
            val lastDate = dao.getLastDate()?.plusDays(1).toMillisUTC() ?: 0L
            val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            val compositeValidator = CompositeDateValidator.allOf(
                listOf(
                    DateValidatorPointForward.from(firstDate),
                    DateValidatorPointBackward.before(lastDate)
                )
            )
            val constraintsBuilder =
                CalendarConstraints.Builder().setValidator(compositeValidator)
            datePickerBuilder.setCalendarConstraints(constraintsBuilder.build())
            val datePicker = datePickerBuilder.build()

            binding.chooseDateButton.setOnClickListener {
                datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
            }
            datePicker.addOnPositiveButtonClickListener {
                val action = WelcomeFragmentDirections.actionWelcomeFragmentToForecastsFragment(it)
                view.findNavController().navigate(action)
            }

            binding.settingsButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_welcomeFragment_to_settingsFragment)
            }
        }
        return view
    }

    /**
     * Start the worker at first launch
     */
    private suspend fun checkPreferences(context: Context) {
        val prefs = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val key = getString(R.string.preferences_update_key)
        if (!prefs.contains(key)) {
            parseAndSave(context)
            WeatherUpdateWorker.addUpdateWorker(requireNotNull(activity).applicationContext)
            prefs.edit { putBoolean(key, true) }
        }
    }
}