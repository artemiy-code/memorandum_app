package ru.artem_torpedo.memorandum.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun addNote(title: String, description: String, updatedAt: Long, isPinned: Boolean)

    suspend fun deleteNote(noteId: Int)

    suspend fun editNote(note: Note)

    fun getAllNote(): Flow<List<Note>>

    suspend fun getNote(noteId: Int): Note

    fun searchNote(query: String): Flow<List<Note>>

    suspend fun switchPinnedStatus(noteId: Int)
}