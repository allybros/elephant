package com.allybros.elephant_todo_app.ui.screen.add

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allybros.elephant_todo_app.addZeroStart
import com.allybros.elephant_todo_app.db.Item
import com.allybros.elephant_todo_app.ui.theme.Purple500
import com.allybros.elephant_todo_app.ui.theme.Teal200


/**
 * Created by orcun on 7.07.2022
 */


@Composable
fun AddDialog(setShowDialog: (Boolean) -> Unit, date: String) {
    val padding = 16.dp

    var note: String by remember {
        mutableStateOf("")
    }

    var time: String by remember {
        mutableStateOf("")
    }

    val item: Item by remember {
        mutableStateOf(Item())
    }

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ){
                Row (
                    modifier = Modifier
                        .absolutePadding(top = padding, left = padding, right = padding)
                        .fillMaxWidth()
                ){
                    Text(
                        text = "Enter New Note",
                        style = TextStyle.Default.copy(
                            color = Purple500,
                            fontWeight = FontWeight.Bold,
                        ),fontSize = 24.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .absolutePadding(top = padding, left = padding, right = padding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    InsertArea { text ->
                        note = text
                    }
                }
                Row(
                    modifier = Modifier
                        .absolutePadding(top = padding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TimeSetText { text ->
                        time = text
                    }
                }
                Row(
                    modifier = Modifier
                        .absolutePadding(top = padding)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    item.note = note
                    item.date = date
                    item.time = time
                    if(time.isNotBlank()) item.hasTime = true

                    Button(
                        onClick = {
                            onCompleted(item)
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .absolutePadding(top = padding, left = padding, right = padding, bottom = padding)
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "Done")
                    }
                }
            }
        }

    }
}

fun onCompleted(item: Item) {
    Log.d("AddDialog",item.toString())
}

@Composable
fun TimeSetText(callback: (String) -> Unit) {

    val mContext = LocalContext.current

    val mTime = remember { mutableStateOf("No time set") }

    val mTimePickerDialog = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            val time = getTime(mHour,mMinute)
            mTime.value = time
            callback.invoke(time)
        }, 12, 0, true
    )

    Text(
        text = mTime.value,
        Modifier
            .clickable { mTimePickerDialog.show() },
        color = Color.Gray,
        textAlign = TextAlign.End,
        fontSize = 28.sp,
    )
}

private fun getTime(mHour: Int, mMinute: Int): String {
    return "${mHour.toString().addZeroStart()}:${mMinute.toString().addZeroStart()}"
}

@Composable
fun InsertArea(callback: (String) -> Unit) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            callback.invoke(newText.text)
        },
        label = { Text(text = "What's next :)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}