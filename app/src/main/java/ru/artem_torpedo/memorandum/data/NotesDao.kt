package ru.artem_torpedo.memorandum.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.artem_torpedo.memorandum.domain.IContent
import ru.artem_torpedo.memorandum.domain.Note

@Dao
interface NotesDao {

    @Transaction
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<FullNoteDbModel>>

    @Transaction
    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId: Int): FullNoteDbModel

    @Transaction
    @Query(
        """SELECT DISTINCT notes.* FROM notes 
        JOIN contentItem on id == noteId
        WHERE title LIKE '%' || :query || '%' 
        OR (contentType== 'TEXT' AND pieceOfContent LIKE '%' || :query || '%') 
        ORDER BY updatedAt DESC"""
    )
    fun filterNotes(query: String): Flow<List<FullNoteDbModel>>

    @Transaction
    @Query("DELETE FROM notes WHERE id == :noteId ")
    suspend fun deleteNote(noteId: Int)

    @Query("DELETE FROM contentItem WHERE noteId == :noteId ")
    suspend fun deleteContent(noteId: Int)

    @Upsert
    suspend fun addOrEditNote(note: NoteDbModel): Long

    @Upsert
    suspend fun addOrEditContent(content: List<ContentItemDbModel>)

    @Transaction
    suspend fun editFullNote(
        note: Note,
        content: List<IContent>,
    ) {
        addOrEditNote(note.convertToDB())
        deleteContent(note.id)
        val contentDb = content.convertToDB(note.id)
        addOrEditContent(contentDb)
    }

    @Transaction
    suspend fun addFullNote(
        note: NoteDbModel,
        content: List<IContent>,
    ) {
        val noteId = addOrEditNote(note).toInt()
        val contentDb = content.convertToDB(noteId)
        addOrEditContent(contentDb)
    }

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId ")
    suspend fun switchPinnedStatus(noteId: Int)
}