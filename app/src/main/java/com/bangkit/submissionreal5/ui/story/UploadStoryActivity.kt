package com.bangkit.submissionreal5.ui.story

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bangkit.submissionreal5.MainActivity
import com.bangkit.submissionreal5.data.viewmodel.SettingsViewModel
import com.bangkit.submissionreal5.data.viewmodel.StoryViewModel
import com.bangkit.submissionreal5.data.viewmodel.ViewModelFactory
import com.bangkit.submissionreal5.databinding.ActivityUploadStoryBinding
import com.bangkit.submissionreal5.utility.FileUtility
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bangkit.submissionreal5.utility.Preferences
import com.bangkit.submissionreal5.utility.dataStore
import java.io.File
import java.lang.StringBuilder

class UploadStoryActivity : AppCompatActivity() {

    private var userToken: String? = null
    private var storyViewModel: StoryViewModel? = null
    private var _binding: ActivityUploadStoryBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = Preferences.getInstance(dataStore)

        val viewModelFactory = ViewModelFactory(this, preferences)

        val settingsViewModel = viewModelFactory.let {
            ViewModelProvider(this, it)[SettingsViewModel::class.java]
        }
        storyViewModel = viewModelFactory.let {
            ViewModelProvider(this, it)[StoryViewModel::class.java]
        }

        settingsViewModel.getUserPreference(ObjectConstanta.UserPreferences.User_Token)
            .observe(this){ token ->
                userToken = StringBuilder("Bearer ").append(token).toString()
            }
        val myFile = intent?.getSerializableExtra(EXTRA_PHOTO_RESULT) as File
        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) as Boolean
        val rotatedBitmap = FileUtility.rotateBitmap(
            BitmapFactory.decodeFile(myFile.path),
            isBackCamera
        )
        binding.imgUpload.setImageBitmap(rotatedBitmap)
        binding.btnUpload.setOnClickListener {
            if (binding.storyDescription.text.isNotEmpty()) {
                uploadImage(myFile, binding.storyDescription.text.toString())
            } else {
                Toast.makeText(this,"Isi Deskripsi Kamu", Toast.LENGTH_SHORT).show()

            }
        }
        storyViewModel?.isSuccessUploadStory?.observe(this){
            if (it){
                Toast.makeText(this, "Image Has been Uploaded", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("navHomeFragment", true)
                startActivity(intent)
            }
        }
        storyViewModel?.error?.observe(this){
            if(it.isNotEmpty()){
                Toast.makeText(this, "Image Upload has been Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage(image: File, description: String) {
        if (userToken != null) {
            if (storyViewModel!!.isPickedLocation.value != true){
                storyViewModel!!.uploadStory(this,userToken!!, image, description)
            } else{
                storyViewModel!!.uploadStory(
                    this,
                    userToken!!,
                    image,
                    description,
                    true,
                    storyViewModel!!.latitude.value.toString(),
                    storyViewModel!!.longitude.value.toString(),
                )
            }

        } else {
            Toast.makeText(this,"Error, Please Relog", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}