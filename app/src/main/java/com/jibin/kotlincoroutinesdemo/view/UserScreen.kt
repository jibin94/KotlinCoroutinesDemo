package com.jibin.kotlincoroutinesdemo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.jibin.kotlincoroutinesdemo.viewmodel.UserViewModel
import com.jibin.kotlincoroutinesdemo.viewmodel.UserViewModelFactory
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kotlin Coroutines Demo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
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
                                    color = Color(0xFFF5F5F5),
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
