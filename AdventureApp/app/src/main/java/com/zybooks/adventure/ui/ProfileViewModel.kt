package com.zybooks.adventure.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zybooks.adventure.data.Post
import com.zybooks.adventure.data.PostRepository

class ProfileViewModel(private val repository: PostRepository) : ViewModel() {

   // LiveData for user posts
   private val _userPosts = MutableLiveData<List<Post>>(emptyList())
   val userPosts: LiveData<List<Post>> = _userPosts

   // LiveData for filtered posts
   private val _filteredPosts = MutableLiveData<List<Post>>(emptyList())
   val filteredPosts: LiveData<List<Post>> = _filteredPosts

   // LiveData for available tags
   private val _availableTags = MutableLiveData<Set<String>>(emptySet())
   val availableTags: LiveData<Set<String>> = _availableTags

   init {
      fetchUserPosts()
   }

   fun fetchUserPosts() {
      viewModelScope.launch {
         val posts = withContext(Dispatchers.IO) {
            repository.allPosts.value ?: emptyList()
         }
         _userPosts.value = posts
         _filteredPosts.value = posts

         // Extract all unique tags from posts
         val tags = posts.flatMap { it.tags }.toSet()
         _availableTags.value = tags
      }
   }

   fun filterPostsByTag(tag: String) {
      viewModelScope.launch {
         if (tag.isEmpty()) {
            _filteredPosts.value = _userPosts.value
         } else {
            val filtered = _userPosts.value?.filter { post ->
               post.tags.contains(tag)
            } ?: emptyList()
            _filteredPosts.value = filtered
         }
      }
   }

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