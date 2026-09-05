package ru.artem_torpedo.memorandum.presentation.screens.noteCreation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.artem_torpedo.memorandum.R
import ru.artem_torpedo.memorandum.presentation.ui.theme.Content
import ru.artem_torpedo.memorandum.presentation.ui.theme.NoteTitle
import ru.artem_torpedo.memorandum.presentation.ui.theme.SaveButton
import ru.artem_torpedo.memorandum.presentation.utils.DateConverter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNote(
    modifier: Modifier = Modifier,
    createNoteViewModel: CreateNoteViewModel,
    onFinished: () -> Unit,
) {

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.also {
                createNoteViewModel.processCommand(Command.AddImage(it))
            }
        }
    )

    when (val stateValue = createNoteViewModel.state.collectAsState().value) {
        is EditNoteState.Creation -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopBar(
                        onNavIconClick = { createNoteViewModel.processCommand(Command.Back) },
                        onActionIconClick = { imagePicker.launch("image/*") }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .padding(horizontal = 8.dp)
                        .fillMaxSize()
                ) {
                    Spacer(Modifier.height(16.dp))

                    // Field for title
                    NoteTitle(
                        modifier = Modifier,
                        text = stateValue.title,
                        onValueChange = {
                            createNoteViewModel.processCommand(Command.AddTitle(it))
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Field for date
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = DateConverter.currentDate(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))

                    // Field for content
                    Content(
                        modifier = Modifier.weight(1f),
                        contentList = stateValue.content,
                        onDeleteImageClick = {
                            createNoteViewModel.processCommand(
                                Command.DeleteImage(it)
                            )
                        },
                        onTextInput = { string, i ->
                            createNoteViewModel.processCommand(
                                Command.AddDescription(
                                    description = string,
                                    index = i
                                )
                            )
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Save button
                    SaveButton(
                        modifier = Modifier,
                        enabled = stateValue.isActive,
                        onClick = {
                            createNoteViewModel.processCommand(Command.Save)
                        }
                    )

                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        EditNoteState.Finished -> {
            SideEffect {
                onFinished()
            }
        }
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onNavIconClick: () -> Unit,
    onActionIconClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = stringResource(R.string.create_note),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .clickable { onNavIconClick() },
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
                        onActionIconClick()
                    },
                painter = painterResource(R.drawable.ic_add_photo),
                contentDescription = stringResource(R.string.add_photo_from_gallery),
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}