package ru.artem_torpedo.memorandum.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.artem_torpedo.memorandum.domain.Note
import ru.artem_torpedo.memorandum.domain.NotesRepository

class NotesRepositoryImpl private constructor(context: Context) : NotesRepository {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.notesDao()

    override suspend fun addNote(
        title: String,
        description: String,
        updatedAt: Long,
        isPinned: Boolean,
    ) {
        val note = NoteDbModel(0, title, description, updatedAt, isPinned)
        dao.addOrEditNote(note)
    }

    override suspend fun deleteNote(noteId: Int) {
        dao.deleteNote(noteId)
    }

    override suspend fun editNote(note: Note) {
        dao.addOrEditNote(note.convertToDB())
    }

    override fun getAllNote(): Flow<List<Note>> {
        return dao.getAllNotes().map {
            it.map { noteDbModel ->
                noteDbModel.convertToEntity()
            }
        }
    }

    override suspend fun getNote(noteId: Int): Note {
        return dao.getNote(noteId).convertToEntity()
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return dao.filterNotes(query).map {
            it.map { noteDbModel ->
                noteDbModel.convertToEntity()
            }
        }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        dao.switchPinnedStatus(noteId)
    }

    companion object {
        private var INSTANCE: NotesRepositoryImpl? = null
        private val lock = Any()

        fun getInstance(context: Context): NotesRepositoryImpl {
            INSTANCE?.also { return it }
            synchronized(lock) {
                INSTANCE?.also { return it }
                return NotesRepositoryImpl(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}