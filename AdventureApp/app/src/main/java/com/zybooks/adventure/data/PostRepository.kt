package com.zybooks.adventure.data

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostRepository(private val context: Context) {

    private val databaseCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
            }
        }
    }

    private val database: PostDatabase = Room.databaseBuilder(
        context,
        PostDatabase::class.java,
        "post.db"
    )
        .addCallback(databaseCallback)
        .build()

    private val postDao = database.postDao()

    val allPosts: LiveData<List<Post>> = postDao.getAllPosts()

    fun getPostById(id: Int): LiveData<Post> {
        return postDao.getPostById(id)
    }

    suspend fun insert(post: Post): Long {
        return postDao.insertPost(post)
    }

    suspend fun update(post: Post) {
        postDao.updatePost(post)
    }

    suspend fun delete(post: Post) {
        postDao.deletePost(post)
    }

    fun getPostsByTag(tag: String): LiveData<List<Post>> {
        return postDao.getPostsByTag(tag)
    }

    // Media storage methods
    suspend fun saveMediaToStorage(inputUri: Uri): String? {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(inputUri) ?: return null

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "POST_${timeStamp}"
        val fileExtension = getFileExtension(contentResolver, inputUri)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveMediaToMediaStore(inputStream, fileName, fileExtension)
        } else {
            saveMediaToExternalStorage(inputStream, fileName, fileExtension)
        }
    }

    private fun getFileExtension(contentResolver: ContentResolver, uri: Uri): String {
        val mimeType = contentResolver.getType(uri) ?: return ".jpg"
        return when {
            mimeType.contains("image") -> ".jpg"
            mimeType.contains("video") -> ".mp4"
            else -> ".jpg"
        }
    }

    private suspend fun saveMediaToMediaStore(inputStream: InputStream, fileName: String, fileExtension: String): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName$fileExtension")
            put(MediaStore.MediaColumns.MIME_TYPE, if (fileExtension == ".jpg") "image/jpeg" else "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Posts")
        }

        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return null

        contentResolver.openOutputStream(uri)?.use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        return uri.toString()
    }

    private suspend fun saveMediaToExternalStorage(inputStream: InputStream, fileName: String, fileExtension: String): String? {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Posts")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "$fileName$fileExtension")
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        return Uri.fromFile(file).toString()
    }
}

