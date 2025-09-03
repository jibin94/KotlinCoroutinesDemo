package com.jibin.kotlincoroutinesdemo.network

import com.jibin.kotlincoroutinesdemo.model.Post
import com.jibin.kotlincoroutinesdemo.model.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): User

    @GET("posts")
    suspend fun getPostsByUserId(@Query("userId") userId: Int): List<Post>
}