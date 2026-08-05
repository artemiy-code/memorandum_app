package ru.artem_torpedo.memorandum.domain

import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: NotesRepository,
) {
    suspend operator fun invoke(title: String, description: String) {
        repository.addNote(
            title = title,
            description = description,
            updatedAt = System.currentTimeMillis(),
            isPinned = false
        )
    }
}