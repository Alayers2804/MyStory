package com.bangkit.submissionreal5.ui.navigation.home

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bangkit.submissionreal5.MainActivity
import com.bangkit.submissionreal5.databinding.FragmentDetailBinding
import com.bangkit.submissionreal5.utility.FileUtility
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bumptech.glide.Glide

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
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
        _binding = FragmentDetailBinding.inflate(layoutInflater,container, false)


        val username = arguments?.getString(ObjectConstanta.StoryPreferences.Username.name)
        val imageUri = arguments?.getString(ObjectConstanta.StoryPreferences.ImageUri.name)
        val storyDesc = arguments?.getString(ObjectConstanta.StoryPreferences.Story_Desc.name)
        val latitude = arguments?.getString(ObjectConstanta.StoryPreferences.Latitude.name)
        val longitude = arguments?.getString(ObjectConstanta.StoryPreferences.Longitude.name)

        // Use the retrieved data as needed
        // For example, set the values to the corresponding views in your layout
        binding.userName.text = username
        Glide.with(binding.root)
            .load(imageUri)
            .into(binding.imgDetail)
        binding.storyDescription.text = storyDesc

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        try {
            binding.storyLocation.text =
                FileUtility.parseAddressLocation(requireContext(), latitude!!.toDouble(), longitude!!.toDouble())
            binding.storyLocation.isVisible = true
        } catch (e: Exception) {
            binding.storyLocation.isVisible = false
        }
        return binding.root

    }
    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setBottomNavigationBar(false)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).setBottomNavigationBar(true)
    }

}