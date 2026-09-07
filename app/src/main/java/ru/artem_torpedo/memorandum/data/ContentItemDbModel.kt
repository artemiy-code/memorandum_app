package ru.artem_torpedo.memorandum.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "contentItem",
    primaryKeys = ["noteId", "contentOrder"],
    foreignKeys = [
        ForeignKey(
            entity = NoteDbModel::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ContentItemDbModel(
    val noteId: Int,
    val contentType: ContentType,
    val pieceOfContent: String,
    val contentOrder: Int,
)

enum class ContentType {
    TEXT, IMAGE
}