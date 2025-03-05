package com.zybooks.adventure.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    postId: Int,
    onSaveComplete: () -> Unit,
    onUpClick: () -> Unit = { },
    modifier: Modifier = Modifier,
    viewModel: EditPostViewModel = viewModel()
) {
    val post by viewModel.post.observeAsState()
    val updatedTitle by viewModel.updatedTitle.observeAsState("")
    val updatedLatitude by viewModel.updatedLatitude.observeAsState("")
    val updatedLongitude by viewModel.updatedLongitude.observeAsState("")
    val updatedDirections by viewModel.updatedDirections.observeAsState("")
    val updatedTags by viewModel.updatedTags.observeAsState(emptyList())
    val updatedDescription by viewModel.updatedDescription.observeAsState("")
    val updatedMediaUri by viewModel.updatedMediaUri.observeAsState()
    val updateStatus by viewModel.updateStatus.observeAsState()
    val context = LocalContext.current

    var tagsText by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    LaunchedEffect(updatedTags) {
        tagsText = updatedTags.joinToString(", ")
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.addNewMedia(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Post") },
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        post?.let { currentPost ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Current or updated image
                if (updatedMediaUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(updatedMediaUri),
                        contentDescription = "Updated image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                } else if (currentPost.mediaUri.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(currentPost.mediaUri)
                                .build()
                        ),
                        contentDescription = currentPost.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Image")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = updatedTitle,
                    onValueChange = { viewModel.setUpdatedTitle(it) },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = updatedDescription,
                    onValueChange = { viewModel.setUpdatedDescription(it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tags
                OutlinedTextField(
                    value = tagsText,
                    onValueChange = {
                        tagsText = it
                        viewModel.setUpdatedTags(it.split(",").map { tag -> tag.trim() }.filter { tag -> tag.isNotBlank() })
                    },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location input fields
                Text(
                    text = "Location (optional)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = updatedLatitude,
                        onValueChange = { viewModel.setUpdatedLatitude(it) },
                        label = { Text("Latitude") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = updatedLongitude,
                        onValueChange = { viewModel.setUpdatedLongitude(it) },
                        label = { Text("Longitude") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Directions
                OutlinedTextField(
                    value = updatedDirections,
                    onValueChange = { viewModel.setUpdatedDirections(it) },
                    label = { Text("Directions") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.updatePost() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }

                // Handle update status
                when (updateStatus) {
                    is EditPostViewModel.UpdateStatus.Success -> {
                        LaunchedEffect(Unit) {
                            onSaveComplete()
                        }
                    }
                    is EditPostViewModel.UpdateStatus.Error -> {
                        Text(
                            text = (updateStatus as EditPostViewModel.UpdateStatus.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

