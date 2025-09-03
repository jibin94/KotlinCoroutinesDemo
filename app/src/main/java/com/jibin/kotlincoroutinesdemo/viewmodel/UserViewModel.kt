package com.jibin.kotlincoroutinesdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jibin.kotlincoroutinesdemo.model.Post
import com.jibin.kotlincoroutinesdemo.model.User
import com.jibin.kotlincoroutinesdemo.network.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class UserViewModel(
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState

    private val _postsState = MutableStateFlow<List<Post>>(emptyList())
    val postsState: StateFlow<List<Post>> = _postsState

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val apiService = RetrofitClient.apiService

    fun fetchUserData() {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                // Use async with IO dispatcher to fetch data in parallel
                val userDeferred = async(ioDispatcher) { apiService.getUser(1) }
                val postsDeferred = async(ioDispatcher) { apiService.getPostsByUserId(1) }

                // Await the results
                val userResponse = userDeferred.await()
                val postsResponse = postsDeferred.await()

                // Switch back to Main thread to update UI
                withContext(mainDispatcher) {
                    _userState.value = userResponse
                    _postsState.value = postsResponse
                    _errorState.value = null // Clear previous errors
                }
            } catch (e: IOException) {
                withContext(mainDispatcher) {
                    _errorState.value =
                        "Network connectivity issue. Please check your internet connection."
                }
            } catch (e: HttpException) {
                withContext(mainDispatcher) {
                    _errorState.value = "Server error: ${e.code()} ${e.message()}"
                }
            } catch (e: Exception) {
                withContext(mainDispatcher) {
                    _errorState.value = "Invalid data received. Please try again later."
                }
            } finally {
                withContext(mainDispatcher) {
                    _loadingState.value = false
                }
            }
        }
    }
}