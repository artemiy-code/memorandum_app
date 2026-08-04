@file:OptIn(ExperimentalCoroutinesApi::class)

package ru.artem_torpedo.memorandum.presentation.screens.mainScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artem_torpedo.memorandum.data.NotesRepositoryImpl
import ru.artem_torpedo.memorandum.domain.GetAllNotesUseCase
import ru.artem_torpedo.memorandum.domain.Note
import ru.artem_torpedo.memorandum.domain.SearchNoteUseCase
import ru.artem_torpedo.memorandum.domain.SwitchPinnedStatusUseCase

class MainViewModel(context: Context) : ViewModel() {

    private val repository = NotesRepositoryImpl.getInstance(context)
    val getAllNotesUseCase = GetAllNotesUseCase(repository)
    val searchNoteUseCase = SearchNoteUseCase(repository)
    val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val _state = MutableStateFlow(NotesState())
    val state = _state.asStateFlow()

    private val query = MutableStateFlow("")

    init {
        query
            .flatMapLatest {
                if (it.isBlank())
                    getAllNotesUseCase()
                else
                    searchNoteUseCase(it)
            }
            .onEach { notesList ->
                val pinnedNotes = notesList.filter { it.isPinned }
                val otherNotes = notesList.filter { !it.isPinned }

                _state.update {
                    it.copy(
                        query = query.value,
                        pinnedNotes = pinnedNotes,
                        unPinnedNotes = otherNotes
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun processCommand(command: Commands) {
        viewModelScope.launch {
            when (command) {
                is Commands.SearchNote -> {
                    query.update {
                        command.query
                    }
                }

                is Commands.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }
            }
        }
    }
}

sealed interface Commands {
    class SearchNote(val query: String) : Commands

    class SwitchPinnedStatus(val noteId: Int) : Commands
}

data class NotesState(
    val query: String = "",
    val pinnedNotes: List<Note> = emptyList(),
    val unPinnedNotes: List<Note> = emptyList(),
)