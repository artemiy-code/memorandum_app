package ru.artem_torpedo.memorandum.data

import ru.artem_torpedo.memorandum.domain.Note

fun Note.convertToDB(): NoteDbModel {
    return NoteDbModel(
        id = id,
        title = title,
        description = description,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

fun NoteDbModel.convertToEntity(): Note {
    return Note(
        id = id,
        title = title,
        description = description,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}