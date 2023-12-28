package com.bangkit.submissionreal5

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.bangkit.submissionreal5.data.viewmodel.SettingsViewModel
import com.bangkit.submissionreal5.data.viewmodel.ViewModelFactory
import com.bangkit.submissionreal5.databinding.ActivitySplashScreenBinding
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bangkit.submissionreal5.utility.Preferences
import com.bangkit.submissionreal5.utility.dataStore
class SplashScreenActivity : AppCompatActivity() {

    private var _binding: ActivitySplashScreenBinding? = null
    private val binding get() = _binding!!
    private var mainActivityStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isMainActivityInBackStack()) {
            finish() // Finish SplashScreenActivity and return
            return
        }

        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val preferences = Preferences.getInstance(dataStore)
        val viewModelFactory = ViewModelFactory(this, preferences)
        val settingsViewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

        settingsViewModel.getUserPreference(ObjectConstanta.UserPreferences.User_Token)
            .observe(this) { token ->
                if (!mainActivityStarted) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("token", token)
                    startActivity(intent)
                    mainActivityStarted = true
                    finish()
                }
            }
    }
    private fun isMainActivityInBackStack(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskList = activityManager.getRunningTasks(Int.MAX_VALUE)

        for (taskInfo in taskList) {
            if (taskInfo.baseActivity?.className == "com.bangkit.submissionreal5.MainActivity") {
                return true
            }
        }

        return false
    }
}


