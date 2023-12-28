package com.bangkit.submissionreal5

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.Preference
import com.bangkit.submissionreal5.data.api.ApiConfig
import com.bangkit.submissionreal5.data.viewmodel.SettingsViewModel
import com.bangkit.submissionreal5.data.viewmodel.StoryPagerViewModel
import com.bangkit.submissionreal5.data.viewmodel.ViewModelFactory
import com.bangkit.submissionreal5.data.viewmodel.ViewModelStoryFactory
import com.bangkit.submissionreal5.databinding.ActivityMainBinding
import com.bangkit.submissionreal5.ui.navigation.home.AccountFragment
import com.bangkit.submissionreal5.ui.navigation.home.HomeFragment
import com.bangkit.submissionreal5.ui.story.CameraActivity
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bangkit.submissionreal5.utility.Preferences
import com.bangkit.submissionreal5.utility.dataStore
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private var fragmentHome : HomeFragment = HomeFragment()
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var bottomAppBar: BottomAppBar
    private var isInitialCreation = true
    private var token = ""
    private val settingViewModel: SettingsViewModel by viewModels { ViewModelFactory(this,pref = Preferences.getInstance(dataStore)) }
    private val pref = Preferences.getInstance(dataStore)
    private lateinit var startNewStory: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = binding.bottomNavigationView
        bottomAppBar = binding.bottomAppBar

        supportFragmentManager.addOnBackStackChangedListener(onBackStackChangedListener)

        settingViewModel.getUserPreference(ObjectConstanta.UserPreferences.User_Token)
            .observe(this) {
                token = "Bearer $it"
                navController.navigate(R.id.homeFragment)
            }

        startNewStory =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    fragmentHome.onRefresh()
                }
            }

        if (intent.getBooleanExtra("navigateToHome",false )) {
            val navController = findNavController(R.id.nav_host_fragment_container)
            navController.navigate(R.id.homeFragment)
        }

        val token = intent.getStringExtra("token")

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        if (isInitialCreation) {
            if (token == ObjectConstanta.preferenceDefaultValue) {
                navController.navigate(R.id.loginFragment)
            } else {
                navController.navigate(R.id.homeFragment)
            }
            isInitialCreation = false
        }

        val currentFragments = navHostFragment.childFragmentManager.fragments
        if (currentFragments.isNotEmpty()) {
            val currentFragment = currentFragments[0]
            if (currentFragment is HomeFragment) {
                fragmentHome = currentFragment
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                }
                R.id.navigation_add -> {
                    if (isPermissionGranted(this, Manifest.permission.CAMERA)) {
                        val intent = Intent(this@MainActivity, CameraActivity::class.java)
                        startActivity(intent)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            ObjectConstanta.CAMERA_PERMISSION_CODE
                        )
                    }
                }
                R.id.navigation_account -> {
                    navController.navigate(R.id.accountFragment)
                }
            }
            true
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    bottomNavigationView.menu.findItem(R.id.navigation_home).isChecked = true

                }
                else -> {
                    bottomNavigationView.menu.findItem(destination.id)?.isChecked = true

                }
            }
        }

        binding.btnMap.setOnClickListener {
            if (isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                navController.navigate(R.id.mapsFragment)
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    ObjectConstanta.LOCATION_PERMISSION_CODE
                )
            }
        }
        setBottomNavigationBar(true)


        this.token = intent.getStringExtra("token") ?: ""
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ObjectConstanta.CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navController.navigate(R.id.action_homeFragment_to_cameraActivity)
            } else {

            }
        }
        if (requestCode == ObjectConstanta.LOCATION_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navController.navigate(R.id.action_homeFragment_to_cameraActivity)
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentDestination = navController.currentDestination
        when (currentDestination?.id) {
            R.id.loginFragment -> {
                finish()
            }
            R.id.homeFragment -> {
                setBottomNavigationBar(false) // Hide bottom navigation bar
                finish()
            }
            else -> {
                super.onBackPressed()

            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener)
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private val onBackStackChangedListener = FragmentManager.OnBackStackChangedListener {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
        if (currentFragment is HomeFragment) {
            bottomNavigationView.selectedItemId = R.id.navigation_home
        } else if (currentFragment is AccountFragment) {
            bottomNavigationView.selectedItemId = R.id.navigation_account
        }
    }
    fun setBottomNavigationBar(visible: Boolean) {
        if (visible) {
            bottomNavigationView.visibility = View.VISIBLE
            bottomAppBar.visibility = View.VISIBLE
        } else {
            bottomNavigationView.visibility = View.GONE
            bottomAppBar.visibility = View.GONE
        }
    }
    fun getUserToken() = token

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getStoryViewModel(): StoryPagerViewModel {
        val viewModel: StoryPagerViewModel by viewModels {
            ViewModelStoryFactory(
                this,
                ApiConfig.getApi(),
                getUserToken()
            )
        }
        return viewModel
    }

}