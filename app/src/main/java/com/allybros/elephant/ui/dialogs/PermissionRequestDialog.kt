package com.allybros.elephant.ui.dialogs

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.allybros.elephant.R
import com.allybros.elephant.ui.screen.main.vectorColors
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


/**
 * Created by orcun on 2.04.2024
 */

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestDialog(context: Context, showDialog: MutableState<Boolean>) {
    val notificationPermissionState =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val padding = 18.dp

    if (!notificationPermissionState.status.isGranted && showDialog.value) {
        Dialog(
            onDismissRequest = { showDialog.value = false }
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colors.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = padding, start = padding, end = padding, bottom = padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val textToShow = if (notificationPermissionState.status.shouldShowRationale)
                        stringResource(R.string.open_settings_info_text)
                    else
                        stringResource(R.string.open_notification_permission_text)

                    Image(
                        painter = painterResource(R.drawable.elephant_icon),
                        contentDescription = "logo",
                        colorFilter = ColorFilter.tint(vectorColors()),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        textToShow,
                        modifier = Modifier
                            .padding(top = padding, start = padding, end = padding)
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = padding, start = padding, end = padding),
                        onClick = {
                            if (notificationPermissionState.status.shouldShowRationale) {
                                openAppSettings(context = context)
                                showDialog.value = false
                            } else {
                                notificationPermissionState.launchPermissionRequest()
                                showDialog.value = false
                            }
                        }
                    ) {
                        val buttonText =
                            if (notificationPermissionState.status.shouldShowRationale) {
                                stringResource(R.string.open_elephant_settings)
                            } else {
                                stringResource(R.string.request_permission)
                            }
                        Text(text = buttonText)
                    }
                }
            }
        }
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(intent)
}