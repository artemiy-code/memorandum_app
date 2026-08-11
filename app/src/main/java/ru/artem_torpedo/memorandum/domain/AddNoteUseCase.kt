package ru.artem_torpedo.memorandum.domain

import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: NotesRepository,
) {
    suspend operator fun invoke(title: String, content: List<IContent>) {
        repository.addNote(
            title = title,
            content = content,
            updatedAt = System.currentTimeMillis(),
            isPinned = false
        )
    }
}