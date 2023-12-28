package com.bangkit.submissionreal5.ui.navigation.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.submissionreal5.MainActivity
import com.bangkit.submissionreal5.R
import com.bangkit.submissionreal5.data.adapter.StoryAdapter
import com.bangkit.submissionreal5.data.viewmodel.SettingsViewModel
import com.bangkit.submissionreal5.data.viewmodel.StoryViewModel
import com.bangkit.submissionreal5.data.viewmodel.ViewModelFactory
import com.bangkit.submissionreal5.databinding.FragmentHomeBinding
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bangkit.submissionreal5.utility.Preferences
import com.bangkit.submissionreal5.utility.dataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.Timer
import kotlin.concurrent.schedule

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var tempToken = ""
    private var viewModel: StoryViewModel? = null
    private val rvAdapter = StoryAdapter()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        val preferences = Preferences.getInstance((activity as MainActivity).dataStore)

        val viewModelFactory = ViewModelFactory(requireContext(), preferences)

        val settingsViewModel = viewModelFactory.let {
            ViewModelProvider(this, it)[SettingsViewModel::class.java]
        }

        viewModel = viewModelFactory.let {
            ViewModelProvider(this, it)[StoryViewModel::class.java]
        }

//        settingsViewModel.getUserPreference(ObjectConstanta.UserPreferences.User_Token)
//            .observe(viewLifecycleOwner) { token ->
//                tempToken = StringBuilder("Bearer ").append(token).toString()
//                viewModel?.getAllStory(requireContext(),tempToken) // Observe story data here
//                Log.d("Data", "Successful")
//            }

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            // Add bottom padding to the RecyclerView
            val padding = resources.getDimensionPixelSize(R.dimen.recycler_view_bottom_padding)
            setPadding(0, 0, 0, padding)
            clipToPadding = false
            isNestedScrollingEnabled = false
            adapter = rvAdapter
        }

        val storyPagerViewModel = (activity as MainActivity).getStoryViewModel()
        storyPagerViewModel.story.observe(viewLifecycleOwner) { stories ->
            Log.d("StoryData", stories.toString()) // Check if the story data is received
            Log.d("Adapter", "Submitting data") // Check if submitData is called
            rvAdapter.submitData(lifecycle, stories)
        }

             // Submit the updated list of stories to the adapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomNavigationBar(true)
    }

    fun onRefresh() {
        rvAdapter.refresh()
        Timer().schedule(2000) {
            binding.rvStory.smoothScrollToPosition(0)
        }
    }

}