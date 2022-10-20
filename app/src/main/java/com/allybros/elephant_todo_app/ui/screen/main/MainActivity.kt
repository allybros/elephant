package com.allybros.elephant_todo_app.ui.screen.main

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
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
        val mCalendar = GregorianCalendar.getInstance()

        setContent {
            Elephant_todo_appTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(mCalendar = mCalendar)
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    mCalendar: Calendar
) {
    val showDialog = remember { mutableStateOf(false) }

    val dayNameLabel = remember {
        mutableStateOf(mCalendar.getDayName().plus( ","))
    }

    val dayAndMonthLabel = remember {
        mutableStateOf(mCalendar.getDay().toString().plus(" ".plus(mCalendar.getMonthName())))
    }

    val formattedDate = remember {
        val month = mCalendar.getMonth() + 1
        val day = mCalendar.getDay()
        val year = mCalendar.getYear()
        mutableStateOf("$day/${month}/$year")
    }

    val noteList by viewModel.noteListStateFlow.collectAsState()

    val datePickerDialog = elephantDatePickerDialog(
        LocalContext.current,
        callback = { pickedDay, pickedMonth, pickedYear ->
            onDatePicked(
                pickedDay,
                pickedMonth,
                pickedYear,
                formattedDate,
                dayNameLabel,
                dayAndMonthLabel,
                viewModel,
                mCalendar
            )
        }
    )

    viewModel.getNotes(formattedDate.value)

    if(showDialog.value){
        AddDialog(
            setShowDialog = { showDialog.value = it },
            date = formattedDate.value,
            buttonClicked = {
                viewModel.addItem(it)
                viewModel.getNotes(dayAndMonthLabel.value)
            }
        )
    }

    Scaffold(
        topBar = {
            ElephantAppBar(
            onBackClicked = {
                onBackClicked(
                    mCalendar,
                    formattedDate,
                    dayNameLabel,
                    dayAndMonthLabel,
                    viewModel
                )
            },
            onForwardClicked = {
                onForwardClicked(
                    mCalendar,
                    formattedDate,
                    dayNameLabel,
                    dayAndMonthLabel,
                    viewModel
                )
           },
            dateClicked = { datePickerDialog.show() },
            dayNameLabel.value,
            dayAndMonthLabel.value
        )},
        bottomBar = {
            ElephantBottomBar(
                addNew = { showDialog.value = true },
                taskCount = "${noteList.size} tasks"
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 40.dp)
        ){
            items(noteList){
                NoteRow(it)
            }
        }
    }
}

fun onBackClicked(
    mCalendar: Calendar,
    mDate: MutableState<String>,
    mDayName: MutableState<String>,
    mDayAndMonth: MutableState<String>,
    viewModel: MainViewModel
) {
    mCalendar.add(Calendar.DAY_OF_MONTH, -1)
    onDatePicked(
        mCalendar.getDay(),
        mCalendar.getMonth(),
        mCalendar.getYear(),
        mDate,
        mDayName,
        mDayAndMonth,
        viewModel,
        mCalendar
    )
}

fun onForwardClicked(
    mCalendar: Calendar,
    mDate: MutableState<String>,
    mDayName: MutableState<String>,
    mDayAndMonth: MutableState<String>,
    viewModel: MainViewModel
) {
    mCalendar.add(Calendar.DAY_OF_MONTH, 1)
    onDatePicked(
        mCalendar.getDay(),
        mCalendar.getMonth(),
        mCalendar.getYear(),
        mDate,
        mDayName,
        mDayAndMonth,
        viewModel,
        mCalendar
    )
}

fun onDatePicked(
    pickedDay: Int,
    pickedMonth: Int,
    pickedYear: Int,
    formattedDate: MutableState<String>,
    dayName: MutableState<String>,
    mDayAndMonth: MutableState<String>,
    viewModel: MainViewModel,
    mCalendar: Calendar
) {
    formattedDate.value = "${pickedDay.toString().addZeroStart()}/${pickedMonth + 1}/$pickedYear"
    mCalendar.set(Calendar.DAY_OF_MONTH,pickedDay)
    mCalendar.set(Calendar.MONTH,pickedMonth)
    mCalendar.set(Calendar.YEAR,pickedYear)

    viewModel.getNotes(formattedDate.value)
    dayName.value = mCalendar.getDayName().plus( ",")
    mDayAndMonth.value = mCalendar.getDay().toString().plus(" "+ mCalendar.getMonthName())
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
fun NoteRow(item: Item) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 16.dp)
                ){
                    ElephantCheckbox(item.isComplete?: false)
                    Text(
                        text = item.note.toString(),
                        fontSize = 20.sp,
                        color = Purple650,
                        modifier = Modifier
                            .padding(start = 16.dp),
                        fontFamily = FontFamily.Serif
                    )
                }
                Text(
                    text = item.time.toString(),
                    fontSize = 16.sp,
                    color = Purple650,
                    fontFamily = FontFamily.Serif
                )
            }
            Divider(
                modifier = Modifier.padding(top = 16.dp),
                color = Purple100,
                thickness = 1.dp
            )
        }
    }
}

@Composable
fun ElephantCheckbox(isComplete: Boolean) {
    val checkedState = remember { mutableStateOf(isComplete) }
    Checkbox(
        checked = checkedState.value,
        onCheckedChange = { checkedState.value = it },
        colors = CheckboxDefaults.colors(
            Purple650,
            Purple650
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
        Column(modifier = Modifier.background(Color.White)) {
            Divider(color = Purple100, thickness = 1.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = taskCount,
                    fontSize = 24.sp,
                    color = Purple800,
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
        backgroundColor = Color.White
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
                    color = Purple650,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = dateText,
                    color = Purple650,
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
            tint = Purple700
        )
    }
}

@Composable
fun AddNew(onClicked: () -> Unit) {
    Box{
        OutlinedButton(
            onClick = { onClicked.invoke() },
            border = null
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "New",
                modifier = Modifier
                    .size(36.dp),
                tint = Purple800
            )
            Text(
                text = "Add New",
                fontSize = 24.sp,
                color = Purple800,
                fontFamily = FontFamily.Serif
            )
        }
    }
}