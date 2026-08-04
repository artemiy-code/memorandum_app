package ru.artem_torpedo.memorandum.presentation.screens.mainScreen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.artem_torpedo.memorandum.R
import ru.artem_torpedo.memorandum.domain.Note
import ru.artem_torpedo.memorandum.presentation.ui.theme.ElectricBlue
import ru.artem_torpedo.memorandum.presentation.ui.theme.OtherNotesColors
import ru.artem_torpedo.memorandum.presentation.ui.theme.PinnedNotesColors
import ru.artem_torpedo.memorandum.presentation.utils.DateConverter

@Composable
fun MainScreen(
    context: Context = LocalContext.current.applicationContext,
    viewModel: MainViewModel = viewModel {
        MainViewModel(context)
    },
    onClick: (Note) -> Unit,
    onFABClick: () -> Unit,
) {
    val stateValue by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 8.dp, end = 16.dp),
                onClick = onFABClick,
                shape = FloatingActionButtonDefaults.largeShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_note),
                    contentDescription = "Add a note"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = paddingValues
        ) {
            item {
                Title(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "All Notes"
                )
            }

            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    query = stateValue.query
                ) {
                    viewModel.processCommand(Commands.SearchNote(it))
                }
            }

            if (stateValue.pinnedNotes.isNotEmpty()) {
                item {
                    Subtitle(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Pinned"
                    )
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        stateValue.pinnedNotes.forEachIndexed { index, note ->
                            item(key = note.id) {
                                NoteCard(
                                    modifier = Modifier.width(160.dp),
                                    note = note,
                                    color = PinnedNotesColors[index % PinnedNotesColors.size],
                                    onClick = onClick,
                                    onLongClick = {
                                        viewModel.processCommand(Commands.SwitchPinnedStatus(it.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (stateValue.unPinnedNotes.isEmpty() && stateValue.pinnedNotes.isEmpty() && stateValue.query.isBlank()) {
                item {
                    Title(
                        modifier = Modifier.padding(top = 240.dp, start = 24.dp),
                        text = " Add your first note⇩"
                    )
                }
            } else {
                item {
                    Subtitle(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "Others"
                    )
                }

                stateValue.unPinnedNotes.forEachIndexed { index, note ->
                    item(key = note.id) {
                        NoteCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            note = note,
                            color = OtherNotesColors[index % OtherNotesColors.size],
                            onClick = onClick,
                            onLongClick = {
                                viewModel.processCommand(Commands.SwitchPinnedStatus(it.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Title(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onTextInput: (String) -> Unit,
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp)),
        value = query,
        onValueChange = onTextInput,
        placeholder = {
            Text(
                text = "Search your notes...",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = "Search",
                tint = ElectricBlue
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        singleLine = true
    )
}

@Composable
fun Subtitle(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier.padding(top = 8.dp, bottom = 4.dp),
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    )
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    color: Color,
    onClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .combinedClickable(
                onLongClick = { onLongClick(note) },
                onClick = { onClick(note) },
            )
            .padding(16.dp)
    ) {
        Text(
            text = note.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = note.description,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            modifier = Modifier.align(Alignment.End),
            text = DateConverter.convertDate(note.updatedAt),
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
        )
    }
}