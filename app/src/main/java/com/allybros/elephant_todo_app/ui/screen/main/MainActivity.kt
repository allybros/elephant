package com.allybros.elephant_todo_app.ui.screen.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
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

    if(showDialog.value)
        AddDialog(
            setShowDialog = {
            showDialog.value = it
        }, "10/10/2022")

    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {
            Text(
                text = "hrell",
                Modifier
                    .clickable{ showDialog.value = true },
                fontSize = 124.sp
            )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Elephant_todo_appTheme {
        MainScreen()
    }
}