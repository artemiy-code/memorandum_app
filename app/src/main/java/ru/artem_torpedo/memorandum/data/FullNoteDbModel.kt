package ru.artem_torpedo.memorandum.data

import androidx.room.Embedded
import androidx.room.Relation

class FullNoteDbModel(
    @Embedded
    val note: NoteDbModel,
    @Relation(
        entity = ContentItemDbModel::class,
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val content: List<ContentItemDbModel>,
)