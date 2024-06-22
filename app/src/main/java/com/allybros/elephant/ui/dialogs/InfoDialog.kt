package com.allybros.elephant.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.allybros.elephant.R
import com.allybros.elephant.ui.screen.main.vectorColors

/**
 * Created by orcun on 28.02.2024
 */
@Composable
fun InfoDialog(
    setShowDialog: (Boolean) -> Unit,
    mainText: String,
    bottomText: String
) {

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Card(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = painterResource(R.drawable.elephant_icon),
                        contentDescription = "logo",
                        colorFilter = ColorFilter.tint(vectorColors()),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "Elephant",
                        color = MaterialTheme.colors.primary,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Row {
                    Text(
                        color = MaterialTheme.colors.secondaryVariant,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        text = mainText,
                        textAlign = TextAlign.Center,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(0.dp, 8.dp)
                ) {
                    Text(
                        color = MaterialTheme.colors.secondaryVariant,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        text = bottomText,
                        textAlign = TextAlign.Center,
                    )
                }
            }

        }
    }
}