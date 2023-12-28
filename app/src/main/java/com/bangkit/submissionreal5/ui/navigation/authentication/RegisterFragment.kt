package com.bangkit.submissionreal5.ui.navigation.authentication

import android.content.ContentValues
import android.os.Bundle
import android.text.TextUtils
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bangkit.submissionreal5.MainActivity
import com.bangkit.submissionreal5.R
import com.bangkit.submissionreal5.data.viewmodel.AuthenticationViewmodel
import com.bangkit.submissionreal5.data.viewmodel.SettingsViewModel
import com.bangkit.submissionreal5.data.viewmodel.ViewModelFactory
import com.bangkit.submissionreal5.databinding.FragmentRegisterBinding
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bangkit.submissionreal5.utility.Preferences
import com.bangkit.submissionreal5.utility.dataStore


class RegisterFragment : Fragment() {

    private var viewModel: AuthenticationViewmodel? = null
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = Preferences.getInstance((activity as MainActivity).dataStore)

        val viewModelFactory = ViewModelFactory(requireContext(), preferences)

        val settingsViewModel = viewModelFactory.let {
            ViewModelProvider(this, it)[SettingsViewModel::class.java]
        }
        val authenticationViewModel = viewModelFactory.let {
            ViewModelProvider(this, it)[AuthenticationViewmodel::class.java]
        }
        viewModel = authenticationViewModel


        authenticationViewModel!!.registrationResult.observe(viewLifecycleOwner){ register ->
            if(!register.error){
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
        }
        authenticationViewModel.error.observe(viewLifecycleOwner){ error ->
            if(error.isNotEmpty()){
                Toast.makeText(requireContext(), error.toString(),Toast.LENGTH_SHORT).show()
            }
        }
        settingsViewModel?.getUserPreference(ObjectConstanta.UserPreferences.User_Token)?.observe(viewLifecycleOwner){
                token -> if(token != ObjectConstanta.preferenceDefaultValue){
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }

        with(binding){
            btnRegister.setOnClickListener {
                val name = etUsername.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                register(name,email, password)
            }
            tvRegister.setOnClickListener {
                val extras = FragmentNavigatorExtras (
                    binding.tvMystory to "My Story",
                    binding.etEmail to "Email",
                    binding.etPassword to "Password",
                    binding.btnRegister to "Button",
                    binding.linearContainer to "Container"
                        )
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment, null,null, extras)
            }
        }

    }

    private fun register(name: String, email: String, password: String) {
        Log.d(ContentValues.TAG, "createAccount:$email")
        if (validateForm()) {
            viewModel?.register(name,email, password)
            return
        }else{
            Toast.makeText(requireContext(), "Registrasi Gagal: Invalid form", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateForm(): Boolean{
        var valid = true

        val name = binding.etUsername.text.toString()
        if (TextUtils.isEmpty(name)) {
            binding.etUsername.error = "Required."
            valid = false
        } else {
            binding.etUsername.error = null
        }

        val email = binding.etEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.error = "Required."
            valid = false
        } else {
            binding.etEmail.error = null
        }

        val password = binding.etPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.error = "Required."
            valid = false
        } else if(password.length < 8){
            binding.etPassword.error = "Password minimal harus 6 karakter"
            valid = false
        }
        else {
            binding.etPassword.error = null
        }


        return valid
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setBottomNavigationBar(false)
    }

}