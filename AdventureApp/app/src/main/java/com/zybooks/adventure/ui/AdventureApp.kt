package com.zybooks.adventure.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

sealed class Routes {
   @Serializable
   data object Profile

   @Serializable
   data class ViewPost(
      val postId: Int
   )

   @Serializable
   data class EditPost(
      val postId: Int
   )

   @Serializable
   data object Upload
}

@Composable
fun AdventureApp() {
   val navController = rememberNavController()

   NavHost(
      navController = navController,
      startDestination = Routes.Profile
   ) {
      composable<Routes.Profile> {
         ProfileScreen(
            onPostClick = { post ->
               navController.navigate(Routes.ViewPost(post.id))
            },
            onAddClick = {
               navController.navigate(Routes.Upload)
            }
         )
      }

      composable<Routes.ViewPost> { backstackEntry ->
         val viewPost: Routes.ViewPost = backstackEntry.toRoute()

         ViewPostScreen(
            postId = viewPost.postId,
            onEditClick = { post ->
               navController.navigate(Routes.EditPost(post.id))
            },
            onUpClick = {
               navController.navigateUp()
            }
         )
      }

      composable<Routes.Upload> {
         UploadScreen(
            onUploadComplete = { postId ->
               navController.navigate(Routes.ViewPost(postId)) {
                  popUpTo(Routes.Profile)
               }
            },
            onUpClick = {
               navController.navigateUp()
            }
         )
      }

      composable<Routes.EditPost> { backstackEntry ->
         val editPost: Routes.EditPost = backstackEntry.toRoute()

         EditPostScreen(
            postId = editPost.postId,
            onSaveComplete = {
               navController.navigateUp()
            },
            onUpClick = {
               navController.navigateUp()
            }
         )
      }
   }
}
