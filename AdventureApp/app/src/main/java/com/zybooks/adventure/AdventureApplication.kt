package com.zybooks.adventure

import com.zybooks.adventure.data.PostRepository
import android.app.Application

class AdventureApplication: Application() {
    // Needed to create ViewModels with the ViewModelProvider.Factory
    lateinit var postRepository: PostRepository

    // For onCreate() to run, android:name=".StudyHelperApplication" must
    // be added to <application> in AndroidManifest.xml
    override fun onCreate() {
        super.onCreate()
        postRepository = PostRepository(this.applicationContext)
    }
}