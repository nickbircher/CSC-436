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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onUploadComplete: (Int) -> Unit,
    onUpClick: () -> Unit = { },
    modifier: Modifier = Modifier,
    viewModel: UploadPostViewModel = viewModel()
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var directions by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    val uploadStatus by viewModel.uploadStatus.observeAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let { viewModel.setMediaUri(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Post") },
                navigationIcon = {
                    IconButton(onClick = onUpClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    viewModel.setTitle(it)
                },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    viewModel.setDescription(it)
                },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tags,
                onValueChange = {
                    tags = it
                    viewModel.setSelectedTags(it.split(",").map { tag -> tag.trim() }.filter { tag -> tag.isNotBlank() })
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
                    value = latitude,
                    onValueChange = {
                        latitude = it
                        viewModel.setLatitude(it)
                    },
                    label = { Text("Latitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = longitude,
                    onValueChange = {
                        longitude = it
                        viewModel.setLongitude(it)
                    },
                    label = { Text("Longitude") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = directions,
                onValueChange = {
                    directions = it
                    viewModel.setSpecificDirections(it)
                },
                label = { Text("Directions (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.savePost() },
                enabled = selectedImageUri != null && title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Upload Post")
            }

            // Handle upload status
            when (val status = uploadStatus) {
                is UploadViewModel.UploadStatus.Success -> {
                    LaunchedEffect(status) {
                        onUploadComplete(status.postId)
                    }
                }
                is UploadViewModel.UploadStatus.Error -> {
                    Text(
                        text = status.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                else -> {}
            }
        }
    }
}

