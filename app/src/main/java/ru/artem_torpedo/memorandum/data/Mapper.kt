package ru.artem_torpedo.memorandum.data

import kotlinx.serialization.json.Json
import ru.artem_torpedo.memorandum.domain.IContent
import ru.artem_torpedo.memorandum.domain.Note

fun Note.convertToDB(): NoteDbModel {
    val contentToString = Json.encodeToString(content)
    return NoteDbModel(
        id = id,
        title = title,
        content = contentToString,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}

fun NoteDbModel.convertToEntity(): Note {
    val contentList = Json.decodeFromString<List<IContent>>(content)
    return Note(
        id = id,
        title = title,
        content = contentList,
        updatedAt = updatedAt,
        isPinned = isPinned
    )
}