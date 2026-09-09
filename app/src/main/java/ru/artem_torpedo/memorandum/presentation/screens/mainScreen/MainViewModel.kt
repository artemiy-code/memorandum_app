@file:OptIn(ExperimentalCoroutinesApi::class)

package ru.artem_torpedo.memorandum.presentation.screens.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artem_torpedo.memorandum.domain.GetAllNotesUseCase
import ru.artem_torpedo.memorandum.domain.Note
import ru.artem_torpedo.memorandum.domain.SearchNoteUseCase
import ru.artem_torpedo.memorandum.domain.SwitchPinnedStatusUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val searchNoteUseCase: SearchNoteUseCase,
    private val switchPinnedStatusUseCase: SwitchPinnedStatusUseCase,
) : ViewModel() {

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
                        pinnedNotes = pinnedNotes,
                        unPinnedNotes = otherNotes
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun processCommand(command: Commands) {
        when (command) {
            is Commands.SearchNote -> {
                _state.update {
                    it.copy(query = command.query)
                }
                query.update {
                    command.query
                }
            }

            is Commands.SwitchPinnedStatus -> {
                viewModelScope.launch {
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