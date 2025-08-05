package com.example.storyappkotlin.data.remote.retrofit


import com.example.storyappkotlin.data.remote.response.LoginResponse
import com.example.storyappkotlin.data.remote.response.RegisterResponse
import com.example.storyappkotlin.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int
    ): Call<StoryResponse>

    @GET("stories")
    suspend fun getPagedStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int
    ): StoryResponse

    @GET("stories/{id}")
    fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") storyId: String
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part?,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Call<StoryResponse>
}