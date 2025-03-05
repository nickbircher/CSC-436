package com.zybooks.adventure.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zybooks.adventure.data.Post
import com.zybooks.adventure.data.PostRepository

class ViewPostViewModel(private val repository: PostRepository) : ViewModel() {

    // The post being viewed
    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post> = _post

    fun loadPost(postId: Int) {
        viewModelScope.launch {
            val postLiveData = repository.getPostById(postId)
            postLiveData.observeForever { post ->
                if (post != null) {
                    _post.value = post
                    postLiveData.removeObserver { }
                }
            }
        }
    }
}