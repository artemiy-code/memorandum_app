package ru.artem_torpedo.memorandum.presentation.screens.noteCreation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artem_torpedo.memorandum.domain.AddNoteUseCase
import ru.artem_torpedo.memorandum.domain.IContent
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: Command) {
        when (command) {
            is Command.AddTitle -> _state.update {
                if (it is EditNoteState.Creation) {
                    it.copy(title = command.title)
                } else it
            }

            is Command.AddDescription -> _state.update {
                if (it is EditNoteState.Creation) {
                    val newContent = it.content
                        .mapIndexed { index, content ->
                            if (index == command.index && content is IContent.Text) {
                                content.copy(text = command.description)
                            } else {
                                content
                            }
                        }
                    it.copy(content = newContent)
                } else {
                    it
                }
            }

            is Command.AddImage -> {
                _state.update {
                    if (it is EditNoteState.Creation) {

                        val newContent = it.content.toMutableList()
                            .dropLastWhile { last ->
                                last is IContent.Text && last.text.isBlank()
                            }.toMutableList()
                            .apply {
                                add(IContent.Image(url = command.uri.toString()))
                                add(IContent.Text(text = ""))
                            }

                        it.copy(content = newContent)
                    } else {
                        it
                    }
                }

            }

            is Command.Back -> _state.update {
                EditNoteState.Finished
            }

            is Command.Save -> {
                _state.update { state ->
                    if (state is EditNoteState.Creation) {
                        val title = state.title
                        val content = state.content.filter {
                            it is IContent.Image || (it as IContent.Text).text.isNotBlank()
                        }
                        viewModelScope.launch {
                            addNoteUseCase(title, content)
                        }
                        EditNoteState.Finished
                    } else {
                        state
                    }
                }
            }
        }
    }
}

sealed interface Command {
    data class AddTitle(val title: String) : Command
    data class AddDescription(val description: String, val index: Int) : Command
    data class AddImage(val uri: Uri) : Command
    object Save : Command
    object Back : Command
}

sealed interface EditNoteState {
    data class Creation(
        val title: String = "",
        val content: List<IContent> = listOf(IContent.Text("")),
    ) : EditNoteState {

        val isActive: Boolean
            get() {
                if (title.isBlank() || content.isEmpty()) {
                    return false
                }
                return content.any {
                    it is IContent.Image || (it as IContent.Text).text.isNotBlank()
                }
            }

    }

    data object Finished : EditNoteState
}