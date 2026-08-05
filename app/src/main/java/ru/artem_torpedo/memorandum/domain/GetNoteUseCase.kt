package ru.artem_torpedo.memorandum.domain

import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val repository: NotesRepository
){
    suspend operator fun invoke(noteId: Int) : Note {
        return repository.getNote(noteId)
    }
}