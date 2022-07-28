package com.allybros.elephant_todo_app.ui.screen.main

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
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
    val showDialog = remember { mutableStateOf(false) }
    val dayName: String
    var date: String
    val noteList by viewModel.noteListLiveData.collectAsState()
    // Fetching the Local Context
    val mContext = LocalContext.current

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
    val dayNames: Array<String> = DateFormatSymbols().weekdays
    val monthNames: Array<String> = DateFormatSymbols().months
    dayName = dayNames[mCalendar.get(Calendar.MONTH)].plus( ",")
    date = mDay.toString().plus(" "+ monthNames[mCalendar.get(Calendar.MONTH)])


    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf("") }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, mYear, mMonth, mDay
    )


    if(showDialog.value)
        AddDialog(
            setShowDialog = { showDialog.value = it },
            date = "10/10/2022",
            buttonClicked = {
                viewModel.addItem(it)
                viewModel.getNotes()
            }
        )

    Scaffold(
        topBar = {
            ElephantAppBar(
            onBackClicked = {  },
            onForwardClicked = {  },
            dateClicked = { mDatePickerDialog.show() },
            dayName,
            date
        ) },
        bottomBar = {
            ElephantBottomBar(
                addNew = { showDialog.value = true },
                taskCount = "${noteList.size} tasks"
            )
        }
    ) {
        LazyColumn{
            items(noteList){
                NoteRow(it)
            }
        }
    }
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
            Divider(color = Purple100, thickness = 1.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.note.toString(),
                    fontSize = 24.sp,
                    color = Purple700,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }


    }

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
        Column {
            Divider(color = Purple100, thickness = 1.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = taskCount,
                    fontSize = 24.sp,
                    color = Purple700,
                    modifier = Modifier.padding(start = 16.dp)
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
                    fontWeight = FontWeight.Bold,
                    color = Purple500,
                    fontSize = 18.sp
                )
                Text(
                    text = dateText,
                    color = Purple500,
                    fontSize = 14.sp
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
                    .size(36.dp)
            )
            Text(
                text = "Add New",
                fontSize = 24.sp
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