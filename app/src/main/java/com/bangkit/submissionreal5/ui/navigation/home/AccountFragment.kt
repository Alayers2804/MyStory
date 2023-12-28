package com.bangkit.submissionreal5.ui.navigation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bangkit.submissionreal5.MainActivity
import com.bangkit.submissionreal5.R
import com.bangkit.submissionreal5.data.viewmodel.SettingsViewModel
import com.bangkit.submissionreal5.data.viewmodel.ViewModelFactory
import com.bangkit.submissionreal5.databinding.FragmentAccountBinding
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bangkit.submissionreal5.utility.Preferences
import com.bangkit.submissionreal5.utility.dataStore


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)

        val preferences = Preferences.getInstance((activity as MainActivity).dataStore)

        val viewModelFactory = ViewModelFactory(requireContext(), preferences)

        val settingsViewModel = viewModelFactory.let {
            ViewModelProvider(this, it)[SettingsViewModel::class.java]
        }
        settingsViewModel.getUserPreference(ObjectConstanta.UserPreferences.User_Token).observe(viewLifecycleOwner) {
            if (it == ObjectConstanta.preferenceDefaultValue) {
                findNavController().navigate(R.id.action_accountFragment_to_loginFragment)
            }
        }
        binding.btnLogout.setOnClickListener {
            settingsViewModel.clearUserPreferences()
        }
        return binding.root
    }

}