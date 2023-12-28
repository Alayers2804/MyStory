package com.bangkit.submissionreal5.data.api

import com.bangkit.submissionreal5.data.response.Login
import com.bangkit.submissionreal5.data.response.Register
import com.bangkit.submissionreal5.data.response.StoryList
import com.bangkit.submissionreal5.data.response.StoryUpload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    @FormUrlEncoded
    fun doRegister (
        @Field("name") name: String,
        @Field("email") email:String,
        @Field("password") password: String
    ) : Call<Register>

    @POST("login")
    @FormUrlEncoded
    fun doLogin (
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<Login>

    @GET("stories")
    fun getAllStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ) : StoryList

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ) : Call<StoryUpload>

    @GET("stories?location=1")
    fun getStoryLocation(
        @Header("Authorization") token: String,
        @Query("size") size: Int
    ) : Call<StoryList>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ) : Call<StoryUpload>

}