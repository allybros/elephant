package com.allybros.elephant_todo_app.ui.screen.main

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.allybros.elephant_todo_app.R
import com.allybros.elephant_todo_app.db.Item
import com.allybros.elephant_todo_app.ui.dialogs.AddDialog
import com.allybros.elephant_todo_app.ui.theme.*
import com.allybros.elephant_todo_app.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Elephant_todo_appTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val taskList by viewModel.taskListStateFlow.collectAsState()
    val doneTaskList by viewModel.doneTaskListStateFlow.collectAsState()
    val dialogStateStateFlow by viewModel.dialogStateStateFlow.collectAsState()
    val dayNameLabelStateFlow by viewModel.dayNameLabelStateFlow.collectAsState()
    val dayAndMonthLabelStateFlow by viewModel.dayAndMonthLabelStateFlow.collectAsState()
    val formattedDateStateFlow by viewModel.formattedDateStateFlow.collectAsState()


    val datePickerDialog = elephantDatePickerDialog(
        LocalContext.current,
        callback = { pickedDay, pickedMonth, pickedYear ->
            viewModel.onDatePicked(
                pickedDay,
                pickedMonth,
                pickedYear
            )
        }
    )

    viewModel.getNotes(formattedDateStateFlow)

    if(dialogStateStateFlow){
        AddDialog(
            setShowDialog = { viewModel.showAddDialog(it) },
            date = formattedDateStateFlow,
            buttonClicked = {
                if (it.uid != null){
                    viewModel.updateItem(it)
                } else if(it.note?.isNotBlank() == true){
                    viewModel.addItem(it)
                    viewModel.getNotes(dayAndMonthLabelStateFlow)
                }
            },
            updateItem = viewModel.updatedItemStateFlow.value
        )
    }

    Scaffold(
        topBar = {
            ElephantAppBar(
            onBackClicked = {
                onBackClicked(viewModel)
            },
            onForwardClicked = {
                onForwardClicked(viewModel)
           },
            dateClicked = {
                datePickerDialog.show()
            },
            dayNameLabelStateFlow,
            dayAndMonthLabelStateFlow
        )},
        bottomBar = {
            ElephantBottomBar(
                addNew = {
                    viewModel.setUpdatedItem(Item())
                    viewModel.showAddDialog(true)
                         },
                taskCount = "${taskList.size - doneTaskList.size}"+ stringResource(R.string.tasks) +
                        " ${doneTaskList.size}" +  stringResource(R.string.done)
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(bottom = 40.dp)) {
            items(taskList){ item ->
                NoteRow(
                    item,
                    {
                        viewModel.deleteItem(item)
                    },
                    {
                        viewModel.setUpdatedItem(item)
                        viewModel.showAddDialog(true)
                    }
                )
            }
        }
    }
}

fun onBackClicked(
    viewModel: MainViewModel
) {
    viewModel.onBackButtonClicked()
}

fun onForwardClicked(
    viewModel: MainViewModel
) {
    viewModel.onForwardButtonClicked()
}

fun elephantDatePickerDialog(
    mContext: Context,
    callback: (
        pickedDay :Int,
        pickedMonth: Int,
        pickedYear: Int
    ) -> Unit
): DatePickerDialog {
    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.getYear()
    mMonth = mCalendar.getMonth()
    mDay = mCalendar.getDay()

    mCalendar.time = Date()
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, pickedYear: Int, pickedMonth: Int, pickedDay: Int ->
            callback.invoke(pickedDay, pickedMonth, pickedYear)
        }, mYear, mMonth, mDay
    )
    return mDatePickerDialog
}

@Composable
fun NoteRow(
    item: Item,
    onCheckedChangedListener:()->Unit,
    onRowClicked: ()->Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRowClicked.invoke() }
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRowClicked.invoke() }
                    .padding(vertical = 16.dp, horizontal = 8.dp)
            ) {
                val textDecoration = remember { mutableStateOf(setTextStyle(item.isComplete?: false)) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 16.dp)
                ){
                    ElephantCheckbox(onCheckedChangedListener, item, textDecoration)
                    Text(
                        text = item.note.toString(),
                        fontSize = 20.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .padding(start = 16.dp),
                        fontFamily = FontFamily.Serif,
                        style = TextStyle(textDecoration = textDecoration.value)
                    )
                }
                Text(
                    text = item.time.toString(),
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.primary,
                    fontFamily = FontFamily.Serif,
                    style = TextStyle(textDecoration = textDecoration.value)
                )
            }
            Divider(
                color = Purple100,
                thickness = 1.dp
            )
        }
    }
}

fun setTextStyle(isCompleted: Boolean): TextDecoration {
    return if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
}

@Composable
fun ElephantCheckbox(
    onCheckedChangedListener: () -> Unit,
    item: Item,
    textDecoration: MutableState<TextDecoration>
) {
    val isChecked = remember { mutableStateOf((item.isComplete ?: false)) }
    Checkbox(
        checked = isChecked.value,
        onCheckedChange =
        {
            item.isComplete = it
            isChecked.value = (item.isComplete?: false)
            textDecoration.value = setTextStyle(item.isComplete?:false)
            onCheckedChangedListener.invoke()
        },
        colors = CheckboxDefaults.colors(
            MaterialTheme.colors.primary,
            MaterialTheme.colors.primary
        )
    )
}


@Composable
fun ElephantBottomBar(
    addNew: ()-> Unit,
    taskCount: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.background(MaterialTheme.colors.background)) {
            Divider(color = Purple100, thickness = 1.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = taskCount,
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(start = 16.dp),
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.weight(1f))
                AddNew { addNew.invoke() }

            }
        }
    }
}

@Composable
fun ElephantAppBar(
    onBackClicked: ()-> Unit,
    onForwardClicked: ()-> Unit,
    dateClicked: ()-> Unit,
    dayNameText: String,
    dateText: String
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.background
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (backArrow, forwardArrow, headerText) = createRefs()

            DirectionButton(
                onButtonClicked = { onBackClicked.invoke() },
                modifier = Modifier
                    .constrainAs(backArrow){
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                imageVector = Icons.Filled.ArrowBack,
                description = "backArrow",
                iconModifier = Modifier.size(24.dp)
            )

            DirectionButton(
                onButtonClicked = { onForwardClicked.invoke() },
                modifier = Modifier
                    .constrainAs(forwardArrow){
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                imageVector = Icons.Filled.ArrowForward,
                description = "forwardArrow",
                iconModifier = Modifier.size(24.dp)
            )

            Row(
                modifier = Modifier
                    .constrainAs(headerText) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(backArrow.end)
                        end.linkTo(forwardArrow.start)
                    }
                    .fillMaxHeight()
                    .clickable { dateClicked.invoke() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dayNameText,
                    color = MaterialTheme.colors.primary,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = dateText,
                    color = MaterialTheme.colors.primary,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Composable
fun DirectionButton(
    onButtonClicked: () -> Unit,
    modifier: Modifier,
    imageVector: ImageVector,
    description: String,
    iconModifier: Modifier
) {
    OutlinedButton(
        onClick = { onButtonClicked.invoke() },
        modifier = modifier.fillMaxHeight(),
        border = null
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = description,
            modifier = iconModifier,
            tint = vectorColors()
        )
    }
}

@Composable
fun vectorColors(): Color =
    if (!isSystemInDarkTheme()){ Purple700 } else { Purple200 }


@Composable
fun AddNew(onClicked: () -> Unit) {
    Box{
        OutlinedButton(
            onClick = { onClicked.invoke() },
            border = null,
            modifier = Modifier.wrapContentSize(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "New",
                modifier = Modifier
                    .size(36.dp),
                tint = MaterialTheme.colors.primary
            )
            Text(
                text = stringResource(R.string.newTask),
                fontSize = 20.sp,
                color = MaterialTheme.colors.primary,
                fontFamily = FontFamily.Serif
            )
        }
    }
}