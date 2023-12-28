package com.bangkit.submissionreal5.utility

import com.google.android.gms.maps.model.LatLng

object ObjectConstanta {

    enum class UserPreferences {
        UserId, Username, User_Email, User_Token, User_Loggedin
    }

    enum class StoryPreferences{
        Username, ImageUri, Story_Desc, Latitude, Longitude
    }

    enum class LocationPicker {
        IsPicked, Latitude, Longitude
    }

    val emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

    const val CAMERA_PERMISSION_CODE = 10

    const val LOCATION_PERMISSION_CODE = 30


    const val preferenceDefaultValue = "Not Set"

    val indonesiaLocation = LatLng(-2.3932797, 108.8507139)

}