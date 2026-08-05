package ru.artem_torpedo.memorandum.presentation.navigation

import androidx.compose.runtime.Composable
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
        startDestination = Screen.Notes
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