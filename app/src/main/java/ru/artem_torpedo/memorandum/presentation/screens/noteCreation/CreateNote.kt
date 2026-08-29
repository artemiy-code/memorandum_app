package ru.artem_torpedo.memorandum.presentation.screens.noteCreation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ru.artem_torpedo.memorandum.R
import ru.artem_torpedo.memorandum.domain.IContent
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
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        value = stateValue.title,
                        onValueChange = {
                            createNoteViewModel.processCommand(Command.AddTitle(it))
                        },
                        placeholder = {
                            Text(
                                text = "Title",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
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
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        enabled = stateValue.isActive,
                        onClick = {
                            createNoteViewModel.processCommand(Command.Save)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = "Save", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

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
                text = "Create note",
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
                contentDescription = "Go back",
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
                contentDescription = "Add photo from gallery",
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}


@Composable
fun Content(
    modifier: Modifier = Modifier,
    contentList: List<IContent>,
    onDeleteImageClick: (Int) -> Unit,
    onTextInput: (String, Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        contentList.forEachIndexed { index, content ->
            when (content) {
                is IContent.Image -> {
                        val flag =
                            (index == 0) || (contentList[index - 1] is IContent.Text)
                        contentList.takeIf { flag }
                            ?.drop(index)
                            ?.takeWhile {
                                it is IContent.Image
                            }
                            ?.map {
                                (it as IContent.Image).url
                            }
                            ?.also { images ->
                                item(key = "${index}_${images.size}") {
                                DisplayImageRow(
                                    images = images,
                                    onDeleteImageClick = {
                                        onDeleteImageClick(index + it)
                                    }
                                )
                            }
                    }
                }

                is IContent.Text -> {
                    item(key = index) {
                        TextContent(
                            modifier = Modifier,
                            text = content.text,
                            onTextInput = {
                                onTextInput(it, index)
                            }
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    image: String,
    onDeleteImageClick: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp)),
            model = image,
            contentDescription = "Image from gallery",
            contentScale = ContentScale.FillWidth
        )

        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clickable{
                    onDeleteImageClick()
                },
            painter = painterResource(R.drawable.ic_delete),
            contentDescription = "Remove photo",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}


@Composable
fun DisplayImageRow(
    modifier: Modifier = Modifier,
    images: List<String>,
    onDeleteImageClick: (Int) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        images.forEachIndexed { index, value ->
            ImageContent(
                modifier = Modifier.weight(1f),
                image = value,
                onDeleteImageClick = {
                    onDeleteImageClick(index)
                }
            )
        }
    }
}

@Composable
fun TextContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextInput: (String) -> Unit,
    readOnly: Boolean = false,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        value = text,
        onValueChange = onTextInput,
        readOnly = readOnly,
        placeholder = {
            Text(
                text = "Description",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Light
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        minLines = 4
    )
}