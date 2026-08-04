package ru.artem_torpedo.memorandum.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.artem_torpedo.memorandum.domain.Note
import ru.artem_torpedo.memorandum.domain.NotesRepository

object TestNotesRepositoryImpl : NotesRepository {

    private val cookedList = mutableListOf<Note>().apply {
        repeat(10) {
            add(
                Note(
                    id = it,
                    title = "Title $it",
                    description = "Description $it",
                    updatedAt = 1645700000000,
                    isPinned = false
                )
            )
        }
    }

    private val notesState = MutableStateFlow<List<Note>>(cookedList)
    private val notesStateFiltered = MutableStateFlow<List<Note>>(cookedList)

    override suspend fun addNote(
        title: String,
        description: String,
        updatedAt: Long,
        isPinned: Boolean,
    ) {
        val newNote = Note(
            id = notesState.value.size,
            title = title,
            description = description,
            updatedAt = updatedAt,
            isPinned = isPinned
        )
        notesState.update {
            it + newNote
        }
        notesStateFiltered.update {
            it + newNote
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        notesState.value.find { it.id == noteId }?.also { note ->
            notesState.update {
                it - note
            }
            notesStateFiltered.update {
                it - note
            }
        }
    }

    override suspend fun editNote(note: Note) {
        notesState.update { notesList ->
            notesList.map {
                if (it.id != note.id)
                    it
                else
                    note
            }
        }

        notesStateFiltered.update { notesList ->
            notesList.map {
                if (it.id != note.id)
                    it
                else
                    note
            }
        }
    }


    override suspend fun getNote(noteId: Int): Note {
//        val listNotes = mutableListOf<Note>().apply {
//            repeat(10) {
//                add(
//                    Note(
//                        id = it,
//                        title = "Title $it",
//                        description = "Description $it",
//                        updatedAt = System.currentTimeMillis() - 138_300_000_000,
//                        isPinned = false
//                    )
//                )
//            }
//        }
//        return listNotes[noteId]
        return notesState.value.first { it.id == noteId }
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        notesStateFiltered.value = notesState.value.filter {
            it.title.contains(query) || it.description.contains(query)
        }
        return notesStateFiltered
    }

    override fun getAllNote(): Flow<List<Note>> {
        return notesState.asStateFlow()
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        var note = notesState.value.find { it.id == noteId }!!
        note = note.copy(isPinned = !note.isPinned)
        editNote(note)
    }
}