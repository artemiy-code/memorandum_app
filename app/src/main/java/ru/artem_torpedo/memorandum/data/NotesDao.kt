package ru.artem_torpedo.memorandum.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteDbModel>>

    @Query("SELECT * FROM notes WHERE id == :noteId")
    suspend fun getNote(noteId : Int): NoteDbModel

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun filterNotes(query: String): Flow<List<NoteDbModel>>

    @Query("DELETE FROM notes WHERE id == :noteId ")
    suspend fun deleteNote(noteId: Int)

    @Query("UPDATE notes SET isPinned = NOT isPinned WHERE id == :noteId ")
    suspend fun switchPinnedStatus(noteId: Int)

    //@Insert(onConflict = OnConflictStrategy.REPLACE) - более плохая альтернатива
    @Upsert
    suspend fun addOrEditNote(note: NoteDbModel)
}