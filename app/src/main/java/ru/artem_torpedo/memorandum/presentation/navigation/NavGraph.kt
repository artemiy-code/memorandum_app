package ru.artem_torpedo.memorandum.presentation.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ru.artem_torpedo.memorandum.presentation.screens.editNote.EditNote
import ru.artem_torpedo.memorandum.presentation.screens.mainScreen.MainScreen
import ru.artem_torpedo.memorandum.presentation.screens.noteCreation.CreateNote

@Serializable
sealed interface Screen {
    @Serializable
    data object Notes : Screen

    @Serializable
    data object AddNote : Screen

    @Serializable
    data class EditNote(val noteId: Int) : Screen
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Notes,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(500)
            )
        }
    ) {
        composable<Screen.Notes> {
            MainScreen(
                onClick = { note ->
                    navController.navigate(Screen.EditNote(note.id))
                },
                onFABClick = {
                    navController.navigate(Screen.AddNote)
                }
            )
        }

        composable<Screen.AddNote> {
            CreateNote(onFinished = {
                navController.popBackStack()
            })
        }

        composable<Screen.EditNote> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.EditNote>()
            EditNote(
                noteId = args.noteId,
                onFinished = {
                    navController.popBackStack()
                }
            )
        }
    }
}