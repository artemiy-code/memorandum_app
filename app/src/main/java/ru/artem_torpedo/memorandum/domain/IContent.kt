package ru.artem_torpedo.memorandum.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface IContent {
    @Serializable
    data class Text(val text: String) : IContent

    @Serializable
    data class Image(val url: String) : IContent
}