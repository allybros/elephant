package com.allybros.elephant_todo_app.ui.dialogs

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allybros.elephant_todo_app.R
import com.allybros.elephant_todo_app.util.addZeroStart
import com.allybros.elephant_todo_app.db.Item


/**
 * Created by orcun on 7.07.2022
 */


@Composable
fun AddDialog(
    setShowDialog: (Boolean) -> Unit,
    date: String,
    buttonClicked: (item: Item) -> Unit,
    updateItem: Item? = null
) {
    val padding = 16.dp

    var note: String by remember {
        var note = ""
        if (updateItem?.note?.isNotBlank() == true) note = updateItem.note!!
        mutableStateOf(note)
    }

    var time: String by remember {
        var time = ""
        if (updateItem?.time?.isNotBlank() == true) {
            time = updateItem.time!!
        }

        mutableStateOf(time)
    }

    val item: Item by remember {
        mutableStateOf(Item())
    }

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.background
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .absolutePadding(top = padding, left = padding, right = padding)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.enterNewTask),
                        style = TextStyle.Default.copy(
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold,
                        ), fontSize = 24.sp
                    )
                }
                var isFirstClick by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .absolutePadding(top = padding, left = padding, right = padding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    InsertArea(
                        { text ->
                            note = text
                        },
                        updateItem?.note,
                        isFirstClick
                    )
                }
                Row(
                    modifier = Modifier
                        .absolutePadding(top = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeSetText(
                        { text ->
                            time = text
                        },
                        updateItem?.time
                    )
                }
                Row(
                    modifier = Modifier
                        .absolutePadding(top = padding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    item.uid = updateItem?.uid
                    item.note = note
                    item.date = date
                    item.time = time
                    item.isComplete = updateItem?.isComplete
                    item.hasTime = time.isNotBlank()
                    Button(
                        onClick = {
                            isFirstClick = true
                            if (note.isNotBlank()){
                                buttonClicked(item)
                                setShowDialog(false)
                            }
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .absolutePadding(
                                top = padding,
                                left = padding,
                                right = padding,
                                bottom = padding
                            )
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = stringResource(R.string.save))
                    }
                }
            }
        }

    }
}

@Composable
fun TimeSetText(callback: (String) -> Unit, initialTime: String? = "") {

    val mContext = LocalContext.current

    val mTime = remember {
        var time = "__ : __"
        if (initialTime?.isNotBlank() == true) time = initialTime!!
        mutableStateOf(time)
    }

    val mTimePickerDialog = TimePickerDialog(
        mContext,
        { _, mHour: Int, mMinute: Int ->
            val time = getTime(mHour, mMinute)
            mTime.value = time
            callback.invoke(time)
        }, 12, 0, true
    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Text(
                text = mTime.value,
                Modifier
                    .clickable { mTimePickerDialog.show() },
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.End,
                fontSize = 28.sp,
            )
        }
        Row {
            OutlinedButton(
                onClick = {
                    callback.invoke("")
                    mTime.value = "__ : __"
                },
                border = null,
                modifier = Modifier.wrapContentSize(Alignment.Center)
            ) {
                Text(text = stringResource(R.string.clearTimer))
            }
        }
    }

}

private fun getTime(mHour: Int, mMinute: Int): String {
    return "${mHour.toString().addZeroStart()}:${mMinute.toString().addZeroStart()}"
}

@Composable
fun InsertArea(
    callback: (String) -> Unit,
    autoFillNote: String? = "",
    isFirstClick: Boolean
) {
    var text by remember { mutableStateOf(TextFieldValue(getInitialText(autoFillNote))) }
    var isTextChanged by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                isTextChanged = true
                text = newText
                callback.invoke(newText.text)
            },
            label = { Text(text = stringResource(R.string.newTaskHint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = isErrorShow(isFirstClick, isTextChanged, isInputEmpty(text))
        )

        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = if (isErrorShow(isFirstClick, isTextChanged, isInputEmpty(text))) stringResource(R.string.errorEmptyError) else "",
            style = TextStyle.Default.copy(color = MaterialTheme.colors.error,),
            fontSize = 12.sp
        )
    }
}

fun isErrorShow(isFirstClick: Boolean, isTextChanged: Boolean, isInputEmpty: Boolean): Boolean {
    return ((isFirstClick || isTextChanged) && isInputEmpty)
}


fun isInputEmpty(text: TextFieldValue): Boolean {
    return text.text.isBlank()
}

fun getInitialText(autoFillNote: String?): String {
    return if (autoFillNote.isNullOrBlank()) "" else autoFillNote
}
