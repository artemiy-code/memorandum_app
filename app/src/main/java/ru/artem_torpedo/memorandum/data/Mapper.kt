package ru.artem_torpedo.memorandum.data

import ru.artem_torpedo.memorandum.domain.IContent
import ru.artem_torpedo.memorandum.domain.Note

fun Note.convertToDB(): NoteDbModel {
    return NoteDbModel(
        id = id,
        title = title,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

fun List<IContent>.convertToDB(noteId: Int): List<ContentItemDbModel> {
    return mapIndexed { index, content ->
        when (content) {
            is IContent.Image -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.IMAGE,
                    pieceOfContent = content.url,
                    contentOrder = index
                )
            }

            is IContent.Text -> {
                ContentItemDbModel(
                    noteId = noteId,
                    contentType = ContentType.TEXT,
                    pieceOfContent = content.text,
                    contentOrder = index
                )
            }
        }
    }
}

fun List<ContentItemDbModel>.convertToEntity(): List<IContent> {
    return map { content ->
        when (content.contentType) {
            ContentType.TEXT -> {
                IContent.Text(content.pieceOfContent)
            }

            ContentType.IMAGE -> {
                IContent.Image(content.pieceOfContent)
            }
        }
    }
}

fun FullNoteDbModel.convertToEntity(): Note {
    return Note(
        id = note.id,
        title = note.title,
        content = content.convertToEntity(),
        updatedAt = note.updatedAt,
        isPinned = note.isPinned
    )
}