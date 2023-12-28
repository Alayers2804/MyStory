package com.bangkit.submissionreal5.data.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit.submissionreal5.data.api.ApiConfig
import com.bangkit.submissionreal5.data.response.Story
import com.bangkit.submissionreal5.data.response.StoryList
import com.bangkit.submissionreal5.data.response.StoryUpload
import com.bangkit.submissionreal5.utility.Preferences
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryViewModel(private val pref: Preferences) : ViewModel() {

    val storyList = MutableLiveData<List<Story>>()
    var error = MutableLiveData("")
    var isSuccessUploadStory = MutableLiveData(false)
    val isError = MutableLiveData(false)
    val isPickedLocation = MutableLiveData(false)
    val latitude = MutableLiveData(0.0)
    val longitude = MutableLiveData(0.0)
    val temp_coordinate = MutableLiveData(LatLng(-2.3932797, 108.8507139))
    val loading = MutableLiveData(View.GONE)

    fun getAllStory(context: Context,token : String){
        val client = ApiConfig.getApi().getStoryLocation(token, 100)
        client.enqueue(object : Callback<StoryList>{
            override fun onResponse(call: Call<StoryList>, response: Response<StoryList>) {
                if (response.isSuccessful){
                    isError.postValue(null)
                    storyList.postValue(response.body()?.listStory)
                } else {
                    error.postValue("Error ${response.code()} : ${response.message()} ")
                }
            }

            override fun onFailure(call: Call<StoryList>, t: Throwable) {
                Log.e("Error", "onFailure Call: ${t.message}")
                error.postValue("Cannot Load the data")
            }

        })
    }

    fun uploadStory(
        context: Context,
        token: String, image: File,
        description: String,
        withLocation: Boolean = false,
        lat: String? = null, lon: String? = null)
    {

        "${image.length() / 1024 / 1024} MB"
        val storyDescription = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            image.name,
            requestImageFile
        )
        val client = if (withLocation){
            val positionLat = lat!!.toRequestBody("text/plain".toMediaType())
            val positionLon = lon!!.toRequestBody("text/plain".toMediaType())

            ApiConfig.getApi().uploadStory(token, imageMultipart, storyDescription,
                positionLat, positionLon
            )
        } else{
            ApiConfig.getApi().uploadStory(token, imageMultipart, storyDescription)
        }
        client.enqueue(object : Callback<StoryUpload>{
            override fun onResponse(call: Call<StoryUpload>, response: Response<StoryUpload>) {
                if (response.isSuccessful){
                    isSuccessUploadStory.postValue(true)
                }
                else {
                    when (response.code()){
                        401 -> error.postValue("Sesi Login mu sudah habis, Silahkan Log in kembali")
                        413 -> error.postValue("Gambar mu kebesaran")
                    }
                }
            }

            override fun onFailure(call: Call<StoryUpload>, t: Throwable) {
                Log.e("error", "onFailure Call: ${t.message}")
                error.postValue("Error : ${t.message}")
            }

        })
    }

}