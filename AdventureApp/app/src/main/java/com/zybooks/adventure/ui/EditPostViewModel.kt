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
import androidx.navigation.toRoute
import com.zybooks.adventure.AdventureApplication
import com.zybooks.adventure.data.Post
import com.zybooks.adventure.data.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditPostViewModel(
   savedStateHandle: SavedStateHandle,
   private val repository: PostRepository
) : ViewModel() {

   companion object {
      val Factory: ViewModelProvider.Factory = viewModelFactory {
         initializer {
            val application = (this[APPLICATION_KEY] as AdventureApplication)
            EditPostViewModel(
               this.createSavedStateHandle(), application.postRepository
            )
         }
      }
   }

   // Get from composable's route argument
   private val postId: Int = savedStateHandle.toRoute<Routes.EditPost>().postId

   // Post state
   var post by mutableStateOf(Post())
      private set

   // Media URI state
   var updatedMediaUri by mutableStateOf<Uri?>(null)
      private set

   // Update status
   private val _updateStatus = MutableStateFlow<UpdateStatus?>(null)
   val updateStatus: StateFlow<UpdateStatus?> = _updateStatus.asStateFlow()

   // Initialize the ViewModel
   init {
      viewModelScope.launch {
         val postLiveData = repository.getPostById(postId)
         postLiveData.observeForever { fetchedPost ->
            if (fetchedPost != null) {
               post = fetchedPost
               postLiveData.removeObserver { }
            }
         }
      }
   }

   // Update post title
   fun updateTitle(title: String) {
      post = post.copy(title = title)
   }

   // Update post description
   fun updateDescription(description: String) {
      post = post.copy(description = description)
   }

   // Update post latitude
   fun updateLatitude(latitude: String) {
      post = post.copy(latitude = latitude.toDoubleOrNull())
   }

   // Update post longitude
   fun updateLongitude(longitude: String) {
      post = post.copy(longitude = longitude.toDoubleOrNull())
   }

   // Set new media URI
   fun setMediaUri(uri: Uri) {
      updatedMediaUri = uri
   }

   // Save the updated post
   fun savePost() {
      viewModelScope.launch {
         _updateStatus.value = UpdateStatus.Loading

         try {
            // Handle media update if needed
            val mediaUri = if (updatedMediaUri != null) {
               repository.saveMediaToStorage(updatedMediaUri!!) ?: post.mediaUri
            } else {
               post.mediaUri
            }

            // Create updated post with new media URI
            val updatedPost = post.copy(mediaUri = mediaUri)

            // Update in database
            repository.update(updatedPost)
            _updateStatus.value = UpdateStatus.Success
         } catch (e: Exception) {
            _updateStatus.value = UpdateStatus.Error(e.message ?: "Unknown error")
         }
      }
   }

   // Reset update status
   fun resetUpdateStatus() {
      _updateStatus.value = null
   }

   // Update status sealed class
   sealed class UpdateStatus {
      object Loading : UpdateStatus()
      object Success : UpdateStatus()
      data class Error(val message: String) : UpdateStatus()
   }
}

