package com.bangkit.submissionreal5.data.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.submissionreal5.R
import com.bangkit.submissionreal5.data.response.Story
import com.bangkit.submissionreal5.databinding.ItemStoryBinding
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        Log.d("StoryItem", story.toString()) // Check if the story object is retrieved correctly
        story?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            Log.d("StoryItem", story.toString())
            binding.apply {
                userName.text = story.name
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(imgStory)
                itemView.setOnClickListener {
                    val bundle = Bundle().apply {
                        putString(ObjectConstanta.StoryPreferences.Username.name, story.name)
                        putString(ObjectConstanta.StoryPreferences.ImageUri.name, story.photoUrl)
                        putString(ObjectConstanta.StoryPreferences.Story_Desc.name, story.description)
                        putString(ObjectConstanta.StoryPreferences.Latitude.name, story.lat.toString())
                        putString(ObjectConstanta.StoryPreferences.Longitude.name, story.lon.toString())
                    }
                    val navController = itemView.findNavController()
                    navController.navigate(R.id.detailFragment, bundle)
                }
            }
        }
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id // Replace `id` with the unique identifier of your Story model
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem // Compare the content of the items using the equals() method of the Story model
            }
        }
    }
}
