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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.allybros.elephant_todo_app.addZeroStart
import com.allybros.elephant_todo_app.db.Item
import com.allybros.elephant_todo_app.ui.screen.addDialog.AddDialog
import com.allybros.elephant_todo_app.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormatSymbols
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
    val mCalendar = Calendar.getInstance()
    val showDialog = remember { mutableStateOf(false) }
    val dayName = remember {
        val dayNames: Array<String> = DateFormatSymbols().weekdays
        mutableStateOf(dayNames[mCalendar.get(Calendar.DAY_OF_WEEK)].plus( ","))
    }
    val date = remember {
        val monthNames: Array<String> = DateFormatSymbols().months
        mutableStateOf(
            mCalendar
                .get(Calendar.DAY_OF_MONTH)
                .toString()
                .plus(" ".plus(monthNames[mCalendar.get(Calendar.MONTH) + 1])))
    }

    val mDate = remember {
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_MONTH)
        val year = mCalendar.get(Calendar.YEAR)
        mutableStateOf("$day/${month+1}/$year")
    }

    val noteList by viewModel.noteListLiveData.collectAsState()

    val datePickerDialog = ElephantDatePickerDialog(
        LocalContext.current,
        callback = { pickedDay, pickedMonth, pickedYear ->
            onDatePicked(pickedDay, pickedMonth, pickedYear, mDate, dayName, date, viewModel)
        }
    )
    viewModel.getNotes(mDate.value)



    if(showDialog.value)
        AddDialog(
            setShowDialog = { showDialog.value = it },
            date = mDate.value,
            buttonClicked = {
                viewModel.addItem(it)
                viewModel.getNotes(date.value)
            }
        )

    Scaffold(
        topBar = {
            ElephantAppBar(
            onBackClicked = {  },
            onForwardClicked = {  },
            dateClicked = { datePickerDialog.show() },
            dayName.value,
            date.value
        ) },
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

fun onDatePicked(
    pickedDay: Int,
    pickedMonth: Int,
    pickedYear: Int,
    mDate: MutableState<String>,
    dayName: MutableState<String>,
    date: MutableState<String>,
    viewModel: MainViewModel
) {
    mDate.value = "${pickedDay.toString().addZeroStart()}/${pickedMonth+1}/$pickedYear"
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH,pickedDay)
    calendar.set(Calendar.MONTH,pickedMonth)
    calendar.set(Calendar.YEAR,pickedYear)

    viewModel.getNotes(mDate.value)
    val dayNames: Array<String> = DateFormatSymbols().weekdays
    val monthNames: Array<String> = DateFormatSymbols().months
    dayName.value = dayNames[calendar.get(Calendar.DAY_OF_WEEK)].plus( ",")
    date.value = pickedDay.toString().plus(" "+ monthNames[pickedMonth+1])
}

fun ElephantDatePickerDialog(
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
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Elephant_todo_appTheme {
        MainScreen()
    }
}