package com.bangkit.submissionreal5.ui.navigation.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bangkit.submissionreal5.MainActivity
import com.bangkit.submissionreal5.R
import com.bangkit.submissionreal5.data.response.Story
import com.bangkit.submissionreal5.data.viewmodel.StoryViewModel
import com.bangkit.submissionreal5.data.viewmodel.ViewModelFactory
import com.bangkit.submissionreal5.databinding.FragmentMapsBinding
import com.bangkit.submissionreal5.utility.ObjectConstanta
import com.bangkit.submissionreal5.utility.Preferences
import com.bangkit.submissionreal5.utility.dataStore
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter, AdapterView.OnItemSelectedListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentMapsBinding? = null
    private val binding get() =_binding!!
    private lateinit var mMap: GoogleMap
    private val storyViewModel: StoryViewModel by viewModels()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(layoutInflater,container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())



//        if (allPermissionsGranted()) {
//            getMyLocation()
//        } else {
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                REQUIRED_PERMISSIONS,
//                REQUEST_CODE_PERMISSIONS
//            )
//        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onMapReady(googleMap: GoogleMap){
        mMap = googleMap
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true

        setMapStyle()
        getMyLocation()

        val preferences = Preferences.getInstance((activity as MainActivity).dataStore)

        val viewModelFactory = ViewModelFactory(requireContext(), preferences)

        val storyViewModel = viewModelFactory.let {
            ViewModelProvider(this, it)[StoryViewModel::class.java]
        }

        storyViewModel.storyList.observe(viewLifecycleOwner) { storyList ->
            for (story in storyList) {
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            story.lat as Double,
                            story.lon as Double
                        )
                    )
                )?.tag = story
            }
        }

        storyViewModel.temp_coordinate.observe(this) {
            CameraUpdateFactory.newLatLngZoom(it, 4f)
        }

        storyViewModel.getAllStory(
            requireContext(),
            (activity as MainActivity).getUserToken()
        )

        mMap.setInfoWindowAdapter(this)

        mMap.setOnInfoWindowClickListener { marker ->
         val data: Story = marker.tag as Story
          routeToDetailStory(data)
      }
    }
    override fun getInfoContents(marker: Marker): View? {
        return null
    }


    override fun getInfoWindow(p0: Marker): View? {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val level: Float = when (position) {
            0 -> 4f
            1 -> 8f
            2 -> 11f
            3 -> 14f
            4 -> 17f
            else -> 4f
        }
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(storyViewModel.temp_coordinate.value!!, level)
        )
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(ObjectConstanta.indonesiaLocation, 4f)
        )
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                (activity as MainActivity),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    storyViewModel.temp_coordinate.postValue(
                        LatLng(
                            location.latitude,
                            location.longitude
                        )
                    )
                } else {
                    storyViewModel.temp_coordinate.postValue(ObjectConstanta.indonesiaLocation)
                }
            }
        } else {
            allPermissionsGranted()
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        (activity as MainActivity),
                        R.string.style_json
                    )
                )
            if (!success) {
                Log.e(TAG_MAPS, "Style parsing  failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG_MAPS, "Can't find style. Error: ", exception)
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        private val TAG_MAPS = "MAPS"
    }

    private fun routeToDetailStory(data: Story) {
        val intent = Intent(requireContext(), DetailFragment::class.java)
        intent.putExtra(ObjectConstanta.StoryPreferences.Username.name, data.name)
        intent.putExtra(ObjectConstanta.StoryPreferences.ImageUri.name, data.photoUrl)
        intent.putExtra(
            ObjectConstanta.StoryPreferences.Story_Desc.name,
            data.description
        )
        intent.putExtra(ObjectConstanta.StoryPreferences.Latitude.name, data.lat.toString())
        intent.putExtra(ObjectConstanta.StoryPreferences.Longitude.name, data.lon.toString())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        requireContext().startActivity(intent)
    }
}
