package ru.artem_torpedo.memorandum.presentation.screens.editNote

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artem_torpedo.memorandum.data.NotesRepositoryImpl
import ru.artem_torpedo.memorandum.domain.DeleteNoteUseCase
import ru.artem_torpedo.memorandum.domain.EditNoteUseCase
import ru.artem_torpedo.memorandum.domain.GetNoteUseCase
import ru.artem_torpedo.memorandum.domain.Note

class EditNoteViewModel(private val noteId: Int, context: Context) : ViewModel() {
    private val repository = NotesRepositoryImpl.getInstance(context)

    val editNoteUseCase = EditNoteUseCase(repository)
    val deleteNoteUseCase = DeleteNoteUseCase(repository)
    val getNoteUseCase = GetNoteUseCase(repository)

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