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
    private val imagesProcessor: ImagesProcess,
) : NotesRepository {

    override suspend fun addNote(
        title: String,
        content: List<IContent>,
        updatedAt: Long,
        isPinned: Boolean,
    ) {
        val note = NoteDbModel(0, title, updatedAt, isPinned)
        val processedContent = content.processForStorage()
        dao.addFullNote(note, processedContent)
    }

    override suspend fun deleteNote(noteId: Int) {
        val content = getNote(noteId).content
        content.deleteImagesFromStorage()
        dao.deleteNote(noteId)
    }

    override suspend fun editNote(note: Note) {
        val oldContent = getNote(note.id).content.filterIsInstance<IContent.Image>()
        val newContent = note.content.filterIsInstance<IContent.Image>()
        (oldContent - newContent.toSet()).deleteImagesFromStorage()
        val processedContent = note.content.processForStorage()

        dao.editFullNote(note, processedContent)
    }

    override fun getAllNote(): Flow<List<Note>> {
        return dao.getAllNotes().map {
            it.map { fullNote ->
                fullNote.convertToEntity()
            }
        }
    }

    override suspend fun getNote(noteId: Int): Note {
        return dao.getNote(noteId).convertToEntity()
    }

    override fun searchNote(query: String): Flow<List<Note>> {
        return dao.filterNotes(query).map {
            it.map { fullNote ->
                fullNote.convertToEntity()
            }
        }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        dao.switchPinnedStatus(noteId)
    }

    private suspend fun List<IContent>.processForStorage(): List<IContent> {
        return this.map { contentItem ->
            when (contentItem) {
                is IContent.Image -> {
                    if (imagesProcessor.isInternalFile(contentItem.url)) {
                        contentItem
                    } else {
                        IContent.Image(imagesProcessor.internalStorageAdd(contentItem.url))
                    }
                }

                is IContent.Text -> {
                    contentItem
                }
            }
        }
    }

    private suspend fun List<IContent>.deleteImagesFromStorage() {
        this.forEach { content ->
            if (content is IContent.Image)
                imagesProcessor.internalStorageDelete(content.url)
        }
    }
}