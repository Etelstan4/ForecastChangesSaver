package com.hfad.forecastchangessaver.views

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.hfad.forecastchangessaver.R
import com.hfad.forecastchangessaver.databinding.FragmentSettingsBinding
import com.hfad.forecastchangessaver.services.WeatherParser
import com.hfad.forecastchangessaver.services.WeatherUpdateWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.updateDataCheckbox.isChecked =
            activity?.getPreferences(ContextThemeWrapper.MODE_PRIVATE)
                ?.getBoolean(getString(R.string.preferences_update_key), false) ?: false

        binding.updateDataCheckbox.setOnCheckedChangeListener { _, isChecked ->
            requireNotNull(activity).apply {
                if (isChecked) {
                    WeatherUpdateWorker.addUpdateWorker(applicationContext)
                } else {
                    WorkManager.getInstance(applicationContext)
                        .cancelUniqueWork(WeatherUpdateWorker.WORKER_TAG)
                }
                val key = getString(R.string.preferences_update_key)
                getPreferences(Context.MODE_PRIVATE).edit { putBoolean(key, isChecked) }
            }
        }

        binding.forceUpdateButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                WeatherParser.parseAndSave(requireNotNull(activity).applicationContext)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireNotNull(activity).applicationContext,
                        R.string.settings_updated_toast,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
        return binding.root
    }
}