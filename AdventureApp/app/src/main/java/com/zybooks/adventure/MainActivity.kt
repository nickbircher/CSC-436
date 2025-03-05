package com.zybooks.adventure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.zybooks.adventure.ui.AdventureApp
import com.zybooks.adventure.ui.theme.AdventureTheme

class MainActivity : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         AdventureTheme {
            Surface(
               modifier = Modifier.fillMaxSize(),
               color = MaterialTheme.colorScheme.background
            ) {
               AdventureApp()
            }
         }
      }
   }
}
