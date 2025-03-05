package com.zybooks.adventure.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.zybooks.adventure.data.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPostScreen(
    postId: Int,
    onEditClick: (Post) -> Unit,
    onUpClick: () -> Unit = { },
    modifier: Modifier = Modifier,
    viewModel: ViewPostViewModel = viewModel()
) {
    val post by viewModel.post.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post?.title ?: "View Post") },
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            post?.let { currentPost ->
                FloatingActionButton(onClick = { onEditClick(currentPost) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Post")
                }
            }
        }
    ) { innerPadding ->
        post?.let { currentPost ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Image
                if (currentPost.mediaUri.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(currentPost.mediaUri)
                                .build()
                        ),
                        contentDescription = currentPost.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = currentPost.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Date
                val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                val dateString = dateFormat.format(Date(currentPost.timestamp))
                Text(
                    text = "Posted on $dateString",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = currentPost.description,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Tags
                if (currentPost.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tags",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = currentPost.tags.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Location
                if (currentPost.latitude != null && currentPost.longitude != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Location",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

