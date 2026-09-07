package ru.artem_torpedo.memorandum.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<FullNoteDbModel>>

    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId : Int): FullNoteDbModel

    @Query("""SELECT DISTINCT notes.* FROM notes 
        JOIN contentItem on id == noteId
        WHERE contentType == 0 OR contentType == 'Text'
        AND title LIKE '%' || :query || '%' 
        OR pieceOfContent LIKE '%' || :query || '%' 
        ORDER BY updatedAt DESC""")
    fun filterNotes(query: String): Flow<List<FullNoteDbModel>>

    @Query("DELETE FROM notes WHERE id == :noteId ")
    suspend fun deleteNote(noteId: Int)

    @Query("DELETE FROM contentItem WHERE noteId == :noteId ")
    suspend fun deleteContent(noteId: Int)

    @Upsert
    suspend fun addOrEditNote(note: NoteDbModel) : Long

    @Upsert
    suspend fun addOrEditContent(content : List<ContentItemDbModel>)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId ")
    suspend fun switchPinnedStatus(noteId: Int)
}