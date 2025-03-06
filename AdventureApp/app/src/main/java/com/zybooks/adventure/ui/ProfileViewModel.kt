package com.zybooks.adventure.ui

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

class ProfileViewModel(
   savedStateHandle: SavedStateHandle,
   private val repository: PostRepository
) : ViewModel() {

   companion object {
      val Factory: ViewModelProvider.Factory = viewModelFactory {
         initializer {
            val application = (this[APPLICATION_KEY] as AdventureApplication)
            ProfileViewModel(
               this.createSavedStateHandle(),
               application.postRepository
            )
         }
      }
   }

   // All posts
   var allPosts by mutableStateOf<List<Post>>(emptyList())
      private set

   // Filtered posts
   var filteredPosts by mutableStateOf<List<Post>>(emptyList())
      private set

   // Available tags
   var availableTags by mutableStateOf<Set<String>>(emptySet())
      private set

   // Initialize the ViewModel
   init {
      fetchUserPosts()
   }

   // Fetch all posts
   fun fetchUserPosts() {
      viewModelScope.launch {
         repository.allPosts.observeForever { posts ->
            allPosts = posts ?: emptyList()
            filteredPosts = posts ?: emptyList()

            // Extract all unique tags from posts
            availableTags = posts?.flatMap { it.tags }?.toSet() ?: emptySet()
         }
      }
   }

   // Filter posts by tag
   fun filterPostsByTag(tag: String) {
      if (tag.isEmpty()) {
         filteredPosts = allPosts
      } else {
         filteredPosts = allPosts.filter { post ->
            post.tags.contains(tag)
         }
      }
   }

   // Delete a post
   fun deletePost(postId: Int) {
      viewModelScope.launch(Dispatchers.IO) {
         val post = repository.getPostById(postId).value
         post?.let {
            repository.delete(it)

            // Update the lists after deletion
            withContext(Dispatchers.Main) {
               fetchUserPosts()
            }
         }
      }
   }
}

