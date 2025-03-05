package com.zybooks.adventure.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
   @PrimaryKey(autoGenerate = true)
   val title: String = "",
   val description: String? = null,
   val id: Int = 0,
   val mediaUri: String = "",
   val latitude: Double? = null,
   val longitude: Double? = null,
   val tags: List<String> = emptyList(),
   val timestamp: Long = System.currentTimeMillis()
)



