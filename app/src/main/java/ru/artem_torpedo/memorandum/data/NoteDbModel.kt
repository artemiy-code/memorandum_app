package ru.artem_torpedo.memorandum.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val content: String,
    val updatedAt: Long,
    val isPinned: Boolean,
)