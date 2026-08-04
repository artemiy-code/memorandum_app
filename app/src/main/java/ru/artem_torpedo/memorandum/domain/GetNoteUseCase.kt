package ru.artem_torpedo.memorandum.domain

class GetNoteUseCase (
    private val repository: NotesRepository
){
    suspend operator fun invoke(noteId: Int) : Note {
        return repository.getNote(noteId)
    }
}