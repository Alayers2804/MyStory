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
import com.bangkit.submissionreal5.databinding.FragmentLoginBinding
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bangkit.submissionreal5.utility.Preferences
import com.bangkit.submissionreal5.utility.dataStore


class LoginFragment : Fragment() {

    private var viewModel: AuthenticationViewmodel? = null
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
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

        viewModel = viewModelFactory.let {
            ViewModelProvider(this, it).get(AuthenticationViewmodel::class.java)
        }


        authenticationViewModel!!.loginResult.observe(viewLifecycleOwner){ login ->
            if(!login.error){
                settingsViewModel?.setUserPreferences(
                    login.loginResult.token,
                    login.loginResult.userId,
                    login.loginResult.name,
                    viewModel!!.tempEmail.value ?: ObjectConstanta.preferenceDefaultValue
                )
                Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
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
            btnSignIn.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                signIn(email, password)
            }
            tvToRegister.setOnClickListener {
                val extras = FragmentNavigatorExtras(
                    binding.tvMystory to "My Story",
                    binding.etEmail to "Email",
                    binding.etPassword to "Password",
                    binding.btnSignIn to "Button",
                    binding.linearContainer to "Container"
                )
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment, null, null, extras)
            }
        }
    }

    private fun signIn(email:String, password: String){
        Log.d(ContentValues.TAG, "signIn:$email")
        if (validateForm()){
            viewModel?.login(email, password)
        } else {
            Toast.makeText(requireContext(), "Login Gagal: Invalid form", Toast.LENGTH_LONG).show()
        }


    }

    private fun validateForm(): Boolean{
        var valid = true

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
            binding.etPassword.error = "Password minimal harus 8 karakter"
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
