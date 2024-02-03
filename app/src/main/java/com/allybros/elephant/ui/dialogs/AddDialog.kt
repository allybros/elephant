package com.allybros.elephant.ui.dialogs

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
import com.allybros.elephant.R
import com.allybros.elephant.util.addZeroStart
import com.allybros.elephant.db.Item
import com.allybros.elephant.util.timeText
import java.util.Calendar


/**
 * Created by orcun on 7.07.2022
 */


@Composable
fun AddDialog(
    setShowDialog: (Boolean) -> Unit,
    date: Long,
    buttonClicked: (item: Item) -> Unit,
    updateItem: Item
) {
    val padding = 16.dp

    val item: Item by remember {
        mutableStateOf(
            Item(
                updateItem.uid,
                updateItem.note,
                if (updateItem.date == null) date else updateItem.date,
                updateItem.isComplete,
                updateItem.hasTime
            )
        )
    }

    var note: String by remember {
        var note = ""
        if (updateItem.note?.isNotBlank() == true) note = updateItem.note!!
        mutableStateOf(note)
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
                            item.note = note
                        },
                        item.note,
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
                        item,
                        date
                    )
                }
                Row(
                    modifier = Modifier
                        .absolutePadding(top = padding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
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
fun TimeSetText(item: Item?, date: Long) {

    val mContext = LocalContext.current
    val timePlaceholder = "__ : __"

    val mTime = remember {
        var time = timePlaceholder
        if (item?.hasTime == true) time = item.date?.timeText()?: timePlaceholder
        mutableStateOf(time)
    }

    val mTimePickerDialog = TimePickerDialog(
        mContext,
        { _, mHour: Int, mMinute: Int ->
            mTime.value = getTime(mHour, mMinute)

            val newDate= Calendar.getInstance()
            newDate.timeInMillis = date
            newDate.set(Calendar.HOUR_OF_DAY, mHour)
            newDate.set(Calendar.MINUTE, mMinute)

            item?.date = newDate.time.time
            item?.hasTime = true
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

                    val newDate= Calendar.getInstance()
                    newDate.timeInMillis = date
                    newDate.set(Calendar.HOUR_OF_DAY, 0)
                    newDate.set(Calendar.MINUTE, 0)
                    newDate.set(Calendar.SECOND, 0)
                    newDate.set(Calendar.MILLISECOND, 0)

                    item?.date = newDate.time.time
                    item?.hasTime = false

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
            style = TextStyle.Default.copy(color = MaterialTheme.colors.error),
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
