package com.zybooks.adventure.ui

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zybooks.adventure.AdventureApplication
import com.zybooks.adventure.data.Post
import com.zybooks.adventure.data.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UploadPostViewModel(
   savedStateHandle: SavedStateHandle,
   private val repository: PostRepository
) : ViewModel() {

   companion object {
      val Factory: ViewModelProvider.Factory = viewModelFactory {
         initializer {
            val application = (this[APPLICATION_KEY] as AdventureApplication)
            UploadPostViewModel(
               this.createSavedStateHandle(),
               application.postRepository
            )
         }
      }
   }

   // Media URI
   var mediaUri by mutableStateOf<Uri?>(null)
      private set

   // Form fields
   var title by mutableStateOf("")
      private set

   var description by mutableStateOf("")
      private set

   var latitude by mutableStateOf("")
      private set

   var longitude by mutableStateOf("")
      private set

   var specificDirections by mutableStateOf("")
      private set

   var selectedTags by mutableStateOf<List<String>>(emptyList())
      private set

   // Upload status
   private val _uploadStatus = MutableStateFlow<UploadStatus?>(null)
   val uploadStatus: StateFlow<UploadStatus?> = _uploadStatus.asStateFlow()

   // Set media URI
   fun setMediaUri(uri: Uri?) {
      mediaUri = uri
   }

   // Set title
   fun setTitle(value: String) {
      title = value
   }

   // Set description
   fun setDescription(value: String) {
      description = value
   }

   // Set latitude
   fun setLatitude(value: String) {
      latitude = value
   }

   // Set longitude
   fun setLongitude(value: String) {
      longitude = value
   }

   // Set specific directions
   fun setSpecificDirections(value: String) {
      specificDirections = value
   }

   // Set selected tags
   fun setSelectedTags(tags: List<String>) {
      selectedTags = tags
   }

   // Save post
   fun savePost() {
      viewModelScope.launch {
         _uploadStatus.value = UploadStatus.Loading

         try {
            val uri = mediaUri
            if (uri != null) {
               val savedMediaUri = withContext(Dispatchers.IO) {
                  repository.saveMediaToStorage(uri)
               }

               if (savedMediaUri != null) {
                  // Parse latitude and longitude from user input
                  val lat = latitude.toDoubleOrNull()
                  val lng = longitude.toDoubleOrNull()

                  val post = Post(
                     mediaUri = savedMediaUri,
                     title = title,
                     description = description,
                     latitude = lat,
                     longitude = lng,
                     tags = selectedTags,
                     timestamp = System.currentTimeMillis()
                  )

                  val id = repository.insert(post)
                  _uploadStatus.value = UploadStatus.Success(id.toInt())
               } else {
                  _uploadStatus.value = UploadStatus.Error("Failed to save media")
               }
            } else {
               _uploadStatus.value = UploadStatus.Error("No media selected")
            }
         } catch (e: Exception) {
            _uploadStatus.value = UploadStatus.Error(e.message ?: "Unknown error")
         }
      }
   }

   // Reset upload status
   fun resetUploadStatus() {
      _uploadStatus.value = null
   }

   // Upload status sealed class
   sealed class UploadStatus {
      object Loading : UploadStatus()
      data class Success(val postId: Int) : UploadStatus()
      data class Error(val message: String) : UploadStatus()
   }
}

