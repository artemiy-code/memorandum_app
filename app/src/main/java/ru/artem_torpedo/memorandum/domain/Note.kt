package ru.artem_torpedo.memorandum.domain

data class Note(
    val id: Int,
    val title: String,
    val content: List<IContent>,
    val updatedAt: Long,
    val isPinned: Boolean,
)
