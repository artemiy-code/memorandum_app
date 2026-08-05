package ru.artem_torpedo.memorandum.presentation.screens.editNote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artem_torpedo.memorandum.domain.DeleteNoteUseCase
import ru.artem_torpedo.memorandum.domain.EditNoteUseCase
import ru.artem_torpedo.memorandum.domain.GetNoteUseCase
import ru.artem_torpedo.memorandum.domain.Note

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    private val editNoteUseCase : EditNoteUseCase,
    private val deleteNoteUseCase : DeleteNoteUseCase,
    private val getNoteUseCase : GetNoteUseCase,
    @Assisted("noteId") private val noteId: Int,
) : ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val note = getNoteUseCase(noteId)
            _state.update {
                EditNoteState.Edit(note)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("noteId") noteId: Int,
        ): EditNoteViewModel
    }

    fun processCommand(command: Command) {
        when (command) {
            is Command.ChangeTitle -> _state.update {
                val previousState = it as EditNoteState.Edit
                val newNote = previousState.note.copy(title = command.title)
                previousState.copy(note = newNote)
            }

            is Command.ChangeDescription -> _state.update {
                val previousState = it as EditNoteState.Edit
                val newNote = previousState.note.copy(description = command.description)
                previousState.copy(note = newNote)
            }

            is Command.Back -> _state.update {
                EditNoteState.Finished
            }

            is Command.Save -> {
                val note = (_state.value as EditNoteState.Edit).note
                viewModelScope.launch {
                    editNoteUseCase(note)
                    _state.update {
                        EditNoteState.Finished
                    }
                }
            }

            Command.Delete -> {
                val noteId = (_state.value as EditNoteState.Edit).note.id
                viewModelScope.launch {
                    deleteNoteUseCase(noteId)
                    _state.update {
                        EditNoteState.Finished
                    }
                }
            }
        }
    }
}

sealed interface Command {
    data class ChangeTitle(val title: String) : Command
    data class ChangeDescription(val description: String) : Command
    object Save : Command
    object Back : Command
    object Delete : Command
}

sealed interface EditNoteState {
    data object Initial : EditNoteState

    data class Edit(
        val note: Note,
    ) : EditNoteState {
        val isEnabled
            get() = note.title.isNotBlank() && note.description.isNotBlank()
    }

    data object Finished : EditNoteState
}