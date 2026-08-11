package ru.artem_torpedo.memorandum.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.artem_torpedo.memorandum.domain.IContent
import ru.artem_torpedo.memorandum.domain.Note
import ru.artem_torpedo.memorandum.domain.NotesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val dao: NotesDao,
) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: List<IContent>,
        updatedAt: Long,
        isPinned: Boolean,
    ) {
        val note = Note(0, title, content, updatedAt, isPinned).convertToDB()
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
}