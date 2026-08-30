package ru.artem_torpedo.memorandum.presentation.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun NoteTitle(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        value = text,
        onValueChange = onValueChange,
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
                .clickable {
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

@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    enabled : Boolean,
    onClick: () -> Unit
){
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text = "Save", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}