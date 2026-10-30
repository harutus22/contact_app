package com.example.contactapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.contactapp.navigation.NavigationRoute

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current

            var isPermissionGranted by remember {
                mutableStateOf(
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }
            RequestPermission(isPermissionGranted) {
                isPermissionGranted = it
            }
            if (isPermissionGranted)
                NavigationRoute()
        }
    }
}

@Composable
fun RequestPermission(
    isPermissionGranted: Boolean,
    onPermissionGranted: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var permissionDeniedOnce by remember { mutableStateOf(false) }
    var permanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        onPermissionGranted(isGranted)

        if (!isGranted && activity != null) {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_CONTACTS
            )
            if (showRationale) {
                permissionDeniedOnce = true
            } else {
                permanentlyDenied = true
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!isPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            onPermissionGranted(true)
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (permissionDeniedOnce && !permanentlyDenied) {
            Text(stringResource(R.string.we_need_your_contacts_permission))
            Button(onClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }) {
                Text(
                    text = stringResource(R.string.try_again),
                    textAlign = TextAlign.Center
                )
            }
        } else if (permanentlyDenied) {
            Text(
                text = stringResource(R.string.you_have_denied_permission),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            ) {
                Text(stringResource(R.string.open_app_settings))
            }
        }
    }
}