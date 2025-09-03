package com.jibin.kotlincoroutinesdemo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.jibin.kotlincoroutinesdemo.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class UserViewModelFactory(
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(ioDispatcher, mainDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun UserScreen(
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            ioDispatcher = Dispatchers.IO,
            mainDispatcher = Dispatchers.Main
        )
    )
) {
    val user by viewModel.userState.collectAsState()
    val posts by viewModel.postsState.collectAsState()
    val isLoading by viewModel.loadingState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserData()
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            user?.let {
                Text(text = "User: ${it.name}", style = MaterialTheme.typography.titleLarge)
                Text(text = "Email: ${it.email}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(text = "User Posts", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(posts) { post ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                color = Color.LightGray,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(text = post.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = post.body, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
@Composable
@Preview(showBackground = true)
fun PreviewUserScreen() {
    UserScreen(
        viewModel = UserViewModel(
            ioDispatcher = Dispatchers.IO,
            mainDispatcher = Dispatchers.Main
        )
    )
}
