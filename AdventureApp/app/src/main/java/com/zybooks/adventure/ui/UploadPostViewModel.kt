package com.zybooks.adventure.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zybooks.adventure.data.Post
import com.zybooks.adventure.data.PostRepository

class UploadPostViewModel(private val repository: PostRepository) : ViewModel() {

   // Media URI
   private val _mediaUri = MutableLiveData<Uri?>()
   val mediaUri: LiveData<Uri?> = _mediaUri

   // Latitude and longitude
   private val _latitude = MutableLiveData<String>("")
   val latitude: LiveData<String> = _latitude

   private val _longitude = MutableLiveData<String>("")
   val longitude: LiveData<String> = _longitude

   // Specific directions
   private val _specificDirections = MutableLiveData<String>("")
   val specificDirections: LiveData<String> = _specificDirections

   // Selected tags
   private val _selectedTags = MutableLiveData<List<String>>(emptyList())
   val selectedTags: LiveData<List<String>> = _selectedTags

   // Upload status
   private val _uploadStatus = MutableLiveData<UploadStatus>()
   val uploadStatus: LiveData<UploadStatus> = _uploadStatus

   // Form fields
   private val _title = MutableLiveData<String>("")
   val title: LiveData<String> = _title

   private val _description = MutableLiveData<String>("")
   val description: LiveData<String> = _description

   fun setMediaUri(uri: Uri?) {
      _mediaUri.value = uri
   }

   fun setTitle(title: String) {
      _title.value = title
   }

   fun setDescription(description: String) {
      _description.value = description
   }

   fun setLatitude(latitude: String) {
      _latitude.value = latitude
   }

   fun setLongitude(longitude: String) {
      _longitude.value = longitude
   }

   fun setSpecificDirections(directions: String) {
      _specificDirections.value = directions
   }

   fun setSelectedTags(tags: List<String>) {
      _selectedTags.value = tags
   }

   fun savePost() {
      viewModelScope.launch {
         _uploadStatus.value = UploadStatus.Loading

         try {
            val uri = mediaUri.value
            if (uri != null) {
               val savedMediaUri = withContext(Dispatchers.IO) {
                  repository.saveMediaToStorage(uri)
               }

               if (savedMediaUri != null) {
                  // Parse latitude and longitude from user input
                  val lat = latitude.value?.toDoubleOrNull()
                  val lng = longitude.value?.toDoubleOrNull()

                  val post = Post(
                     mediaUri = savedMediaUri,
                     title = title.value ?: "",
                     description = description.value ?: "",
                     latitude = lat,
                     longitude = lng,
                     tags = selectedTags.value ?: emptyList(),
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

   sealed class UploadStatus {
      object Loading : UploadStatus()
      data class Success(val postId: Int) : UploadStatus()
      data class Error(val message: String) : UploadStatus()
   }}