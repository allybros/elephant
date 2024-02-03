package com.allybros.elephant.ui.screen.main

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.allybros.elephant.R
import com.allybros.elephant.db.Item
import com.allybros.elephant.ui.dialogs.AddDialog
import com.allybros.elephant.ui.theme.*
import com.allybros.elephant.util.*
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val taskList by viewModel.taskListStateFlow.collectAsState()
    val doneTaskList by viewModel.doneTaskListStateFlow.collectAsState()
    val dialogStateStateFlow by viewModel.dialogStateStateFlow.collectAsState()
    val dayNameLabelStateFlow by viewModel.dayNameLabelStateFlow.collectAsState()
    val dayAndMonthLabelStateFlow by viewModel.dayAndMonthLabelStateFlow.collectAsState()
    val activeDateStateFlow by viewModel.activeDateStateFlow.collectAsState()
    val context = LocalContext.current

    val datePickerDialog = elephantDatePickerDialog(
        context,
        callback = { date ->
            viewModel.onDatePicked(date)
        }
    )

    if (dialogStateStateFlow) {
        AddDialog(
            setShowDialog = { viewModel.showAddDialog(it) },
            date = activeDateStateFlow.time.time,
            buttonClicked = { item->
                if (item.uid != null) {
                    viewModel.updateItem(item)
                } else if (item.note?.isNotBlank() == true) {
                    viewModel.addItem(item)
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
            )
        },
        bottomBar = {
            ElephantBottomBar(
                addNew = {
                    viewModel.setUpdatedItem(Item())
                    viewModel.showAddDialog(true)
                },
                taskCount = "${taskList.size - doneTaskList.size}" + stringResource(R.string.tasks) +
                        " ${doneTaskList.size}" + stringResource(R.string.done)
            )
        }
    ) { paddingValues ->
        
        if (taskList.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    ElephantListEmptyView()
                }
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(taskList, key = { taskList: Item-> taskList.uid!! }) { item ->
                    val dismissState = rememberDismissState()

                    context.setupNotification(item)

                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        item.isComplete = true
                        context.setupNotification(item)
                        viewModel.deleteItem(item)
                    }

                    SwipeToDismiss(
                        modifier = Modifier.animateItemPlacement(),
                        state = dismissState,
                        directions = setOf(
                            DismissDirection.EndToStart
                        ),
                        dismissThresholds = { direction ->
                            FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.1f else 0.05f)
                        },
                        background = {
                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    DismissValue.Default -> MaterialTheme.colors.background
                                    else -> Purple650
                                }
                            )
                            val alignment = Alignment.CenterEnd
                            val icon = Icons.Filled.Delete

                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                            )

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                if(dismissState.targetValue != DismissValue.Default){
                                    Icon(
                                        icon,
                                        contentDescription = "Delete Icon",
                                        modifier = Modifier.scale(scale),
                                        tint = if(isSystemInDarkTheme()) Color.White else Color.Black
                                    )
                                }

                            }
                        },
                        dismissContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement()
                            ) {

                                Card(
                                    elevation = animateDpAsState(
                                        if (dismissState.dismissDirection != null) 4.dp else 0.dp
                                    ).value
                                ) {
                                    NoteRow(
                                        item = item,
                                        onCheckedChangedListener = {
                                            viewModel.completeItem(item)
                                            context.setupNotification(item)
                                        },
                                        onTextClicked = {
                                            viewModel.setUpdatedItem(item)
                                            viewModel.showAddDialog(true)
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ElephantListEmptyView() {
    Icon(
        imageVector = Icons.Rounded.Done,
        contentDescription = "Empty list icon",
        tint = vectorColors()
    )
    Text(
        text = stringResource(R.string.emptyList),
        fontSize = 20.sp,
        color = MaterialTheme.colors.primary,
        modifier = Modifier.padding(64.dp, 0.dp),
        fontFamily = FontFamily.Serif,
        textAlign = TextAlign.Center
    )
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
    callback: (date: Long) -> Unit
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
            val pickedDateCalendar = Calendar.getInstance()
            pickedDateCalendar.set(pickedYear, pickedMonth, pickedDay,0,0,0)
            callback.invoke(pickedDateCalendar.time.time)
        }, mYear, mMonth, mDay
    )
    return mDatePickerDialog
}

@Composable
fun NoteRow(
    item: Item,
    onCheckedChangedListener: () -> Unit,
    onTextClicked: () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val textDecoration =
                remember { mutableStateOf(setTextStyle(item.isComplete ?: false)) }
            textDecoration.value = setTextStyle(item.isComplete ?: false)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ElephantCheckbox(onCheckedChangedListener, item, textDecoration)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTextClicked.invoke() }
                        .padding(end = 16.dp)

                ) {
                    Text(
                        text = item.note.toString(),
                        fontSize = 20.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 24.dp, horizontal = 12.dp)
                            .weight(1f),
                        fontFamily = FontFamily.Serif,
                        style = TextStyle(textDecoration = textDecoration.value),
                    )
                    Text(
                        text = (if(item.hasTime == true) item.date?.timeText() else "")!!,
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.primary,
                        fontFamily = FontFamily.Serif,
                        style = TextStyle(textDecoration = textDecoration.value)
                    )
                }
            }

        }
        Divider(
            color = Purple100,
            thickness = 1.dp
        )
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
    isChecked.value = item.isComplete ?: false
    Checkbox(
        checked = isChecked.value,
        onCheckedChange =
        {
            item.isComplete = it
            isChecked.value = (item.isComplete ?: false)
            textDecoration.value = setTextStyle(item.isComplete ?: false)
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
    addNew: () -> Unit,
    taskCount: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.background(MaterialTheme.colors.background)) {
            Divider(color = Purple100, thickness = 1.dp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = taskCount,
                    fontSize = 20.sp,
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1.25f),
                    fontFamily = FontFamily.Serif
                )
                Image(
                    painter = painterResource(R.drawable.elephant_icon),
                    contentDescription = "logo",
                    colorFilter = ColorFilter.tint(vectorColors()),
                    modifier = Modifier
                        .size(36.dp)
                        .weight(0.5f)
                )
                Box(modifier = Modifier.weight(1.25f)){
                    AddNew { addNew.invoke() }
                }
            }
        }
    }
}

@Composable
fun ElephantAppBar(
    onBackClicked: () -> Unit,
    onForwardClicked: () -> Unit,
    dateClicked: () -> Unit,
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
                    .constrainAs(backArrow) {
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
                    .constrainAs(forwardArrow) {
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
    if (!isSystemInDarkTheme()) {
        Purple700
    } else {
        Purple200
    }


@Composable
fun AddNew(onClicked: () -> Unit) {
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