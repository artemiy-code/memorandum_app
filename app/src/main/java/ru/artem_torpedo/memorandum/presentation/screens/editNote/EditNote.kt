package ru.artem_torpedo.memorandum.presentation.screens.editNote

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.artem_torpedo.memorandum.R
import ru.artem_torpedo.memorandum.presentation.ui.theme.Content
import ru.artem_torpedo.memorandum.presentation.ui.theme.NoteTitle
import ru.artem_torpedo.memorandum.presentation.ui.theme.SaveButton
import ru.artem_torpedo.memorandum.presentation.utils.DateConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNote(
    modifier: Modifier = Modifier,
    editNoteViewModel: EditNoteViewModel,
    onFinished: () -> Unit,
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.also {
                editNoteViewModel.processCommand(Command.AddImage(it))
            }
        }
    )

    when (val stateValue = editNoteViewModel.state.collectAsState().value) {
        is EditNoteState.Edit -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                modifier = Modifier.padding(start = 12.dp),
                                text = stringResource(R.string.edit_note),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .size(24.dp)
                                    .clickable {
                                        editNoteViewModel.processCommand(Command.Back)
                                    },
                                painter = painterResource(R.drawable.ic_angle_double_left),
                                contentDescription = stringResource(R.string.go_back),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        actions = {
                            Icon(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(24.dp)
                                    .clickable {
                                        imagePicker.launch("image/*")
                                    },
                                painter = painterResource(R.drawable.ic_add_photo),
                                contentDescription = stringResource(R.string.add_photo_from_gallery),
                                tint = MaterialTheme.colorScheme.secondary
                            )

                            Icon(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(24.dp)
                                    .clickable {
                                        editNoteViewModel.processCommand(Command.Delete)
                                    },
                                painter = painterResource(R.drawable.ic_delete),
                                contentDescription = stringResource(R.string.delete_note),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                ) {
                    Spacer(Modifier.height(16.dp))

                    // Заголовок заметки
                    NoteTitle(
                        modifier = Modifier,
                        text = stateValue.note.title,
                        onValueChange = {
                            editNoteViewModel.processCommand(Command.ChangeTitle(it))
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Дата последнего изменения заметки
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = DateConverter.convertDate(stateValue.note.updatedAt),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))

                    // Контент заметки
                    Content(
                        modifier = Modifier.weight(1f),
                        contentList = stateValue.note.content,
                        onDeleteImageClick = {
                            editNoteViewModel.processCommand(Command.DeleteImage(it))
                        },
                        onTextInput = { content: String, index: Int ->
                            editNoteViewModel.processCommand(
                                Command.ChangeText(
                                    index = index,
                                    content = content
                                )
                            )
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    SaveButton(
                        modifier = Modifier,
                        enabled = stateValue.isEnabled,
                        onClick = {
                            editNoteViewModel.processCommand(Command.Save)
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        is EditNoteState.Finished -> {
            SideEffect {
                onFinished()
            }
        }

        is EditNoteState.Initial -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
            {
                Text(text = stringResource(R.string.loading), fontSize = 40.sp)
            }
        }
    }
}