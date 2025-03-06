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
import androidx.navigation.toRoute
import com.zybooks.adventure.AdventureApplication
import com.zybooks.adventure.data.Post
import com.zybooks.adventure.data.PostRepository
import kotlinx.coroutines.launch

class ViewPostViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: PostRepository
) : ViewModel() {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdventureApplication)
                ViewPostViewModel(
                    this.createSavedStateHandle(),
                    application.postRepository
                )
            }
        }
    }

    // Get from composable's route argument
    private val postId: Int = savedStateHandle.toRoute<Routes.ViewPost>().postId

    // Post state
    var post by mutableStateOf(Post())
        private set

    // Initialize the ViewModel
    init {
        loadPost()
    }

    // Load post data
    private fun loadPost() {
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
}

