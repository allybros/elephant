package com.allybros.elephant_todo_app.ui.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.allybros.elephant_todo_app.ui.screen.add.AddDialog
import com.allybros.elephant_todo_app.ui.theme.Elephant_todo_appTheme
import dagger.hilt.android.AndroidEntryPoint

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
fun MainScreen() {
    val showDialog =  remember { mutableStateOf(false) }
    val taskCount by remember { mutableStateOf("0 tasks") }
    val dayName by remember { mutableStateOf("dayName,") }
    val date by remember { mutableStateOf("date") }

    if(showDialog.value)
        AddDialog(
            setShowDialog = {
            showDialog.value = it
        }, "10/10/2022")

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val (appBar, list, addNewButton, noteCount) = createRefs()

        ElephantAppBar(
            onBackClicked = {  },
            onForwardClicked = {  },
            dayName,
            date,
            modifier = Modifier
                .constrainAs(appBar){
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        val itemsList = (0..180).toList()
        LazyColumn(
            modifier = Modifier
                .constrainAs(list){
                    top.linkTo(appBar.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(bottom = 102.dp)
        ){
            items(itemsList){
                Text(
                    text = "$it",
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }


        Box(
            modifier = Modifier
                .constrainAs(addNewButton){
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        ) {
            AddNew { showDialog.value = true }
        }
        Box(
            modifier = Modifier
                .constrainAs(noteCount) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                .padding(12.dp)

        ){
            Text(
                text = taskCount,
                fontSize = 24.sp
            )
        }

        createVerticalChain(appBar,list,chainStyle = ChainStyle.Packed(0F))
    }

}

@Composable
fun ElephantAppBar(
    onBackClicked: ()-> Unit,
    onForwardClicked: ()-> Unit,
    dayNameText: String,
    dateText: String,
    modifier: Modifier
) {
    TopAppBar(modifier = modifier) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (backArrow, forwardArrow, dayName, date) = createRefs()

            Button(
                onClick = { onBackClicked.invoke() },
                modifier = Modifier
                    .constrainAs(backArrow){
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "backArrow",
                    modifier = Modifier.size(24.dp)
                )
            }
            Button(
                onClick = { onForwardClicked.invoke() },
                modifier = Modifier
                    .constrainAs(forwardArrow){
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "forwardArrow",
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = dayNameText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .constrainAs(dayName){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(backArrow.end)
                        end.linkTo(date.start)
                    }
            )

            Text(
                text = dateText,
                modifier = Modifier
                    .constrainAs(date) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(dayName.end)
                        end.linkTo(forwardArrow.start)
                    }
                    .padding(start = 4.dp)
            )





        }
    }
}

@Composable
fun AddNew(onClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
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