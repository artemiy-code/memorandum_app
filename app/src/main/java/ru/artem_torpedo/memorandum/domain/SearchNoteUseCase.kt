package ru.artem_torpedo.memorandum.domain

import kotlinx.coroutines.flow.Flow

class SearchNoteUseCase(
    private val repository: NotesRepository,
) {
    operator fun invoke(query: String): Flow<List<Note>> {
        return repository.searchNote(query)
    }
}