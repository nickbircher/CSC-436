package com.zybooks.adventure.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
    viewModel: ViewPostViewModel = viewModel(factory = ViewPostViewModel.Factory)
) {
    val post = viewModel.post
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(post.title.ifEmpty { "View Post" }) },
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEditClick(post) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Post")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Image
            if (post.mediaUri.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(post.mediaUri)
                            .build()
                    ),
                    contentDescription = post.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = post.title,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
            val dateString = dateFormat.format(Date(post.timestamp))
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
                text = post.description,
                style = MaterialTheme.typography.bodyMedium
            )

            // Tags
            if (post.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = post.tags.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Location - display coordinates for copying
            if (post.latitude != null && post.longitude != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleMedium
                )

                // Display coordinates in a selectable format
                Text(
                    text = "Latitude: ${post.latitude}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "Longitude: ${post.longitude}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Optional: Add a note about copying
                Text(
                    text = "Copy these coordinates to use in your preferred map application",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

