package ru.artem_torpedo.memorandum.presentation.screens.noteCreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artem_torpedo.memorandum.domain.AddNoteUseCase
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val addNoteUseCase : AddNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: Command) {
        when (command) {
            is Command.AddTitle -> _state.update {
                (it as EditNoteState.Creation).copy(
                    title = command.title,
                    isActive = command.title.isNotBlank() && it.description.isNotBlank()
                )
            }

            is Command.AddDescription -> _state.update {
                (it as EditNoteState.Creation).copy(
                    description = command.description,
                    isActive = command.description.isNotBlank() && it.title.isNotBlank()
                )
            }

            is Command.Back -> _state.update {
                EditNoteState.Finished
            }

            is Command.Save -> {
                val note = _state.value as EditNoteState.Creation
                viewModelScope.launch {
                    addNoteUseCase(note.title, note.description)
                    _state.update {
                        EditNoteState.Finished
                    }
                }
            }
        }
    }
}

sealed interface Command {
    data class AddTitle(val title: String) : Command
    data class AddDescription(val description: String) : Command
    object Save : Command
    object Back : Command
}

sealed interface EditNoteState {
    data class Creation(
        val title: String = "",
        val description: String = "",
        val isActive: Boolean = false,
    ) : EditNoteState

    data object Finished : EditNoteState
}