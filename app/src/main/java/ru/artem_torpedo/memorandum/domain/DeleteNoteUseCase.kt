package ru.artem_torpedo.memorandum.domain

import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NotesRepository,
) {
    suspend operator fun invoke(noteId: Int) {
        repository.deleteNote(noteId)
    }
}